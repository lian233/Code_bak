package com.wofu.ecommerce.ming_xie_ku;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ming_xie_ku.utils.Utils;
public class OrderDelivery extends Thread{
	private static String jobname = "名鞋库订单发货处理作业";
	
	private boolean is_exporting = false;

	@Override
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				delivery(connection);	
				
//				modifiRemark(connection);

			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_exporting = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	private void delivery(Connection conn)  throws Exception
	{
		
		String sql = "select  a.sheetid,b.tid, upper(ltrim(rtrim(b.companycode))) companycode,"
			+"upper(ltrim(rtrim(b.outsid))) outsid from it_upnote a with(nolock), ns_delivery b with(nolock)"
			+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
			+ Params.tradecontactid + "' and b.iswait=0";
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		Log.info("本次要处理的订单发货条数为: "+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();   
			String orderid = hto.get("tid").toString(); //订单号   网店制定的外部订单号
			String post_company = hto.get("companycode").toString();
			String post_no = hto.get("outsid").toString();		
			
			//如果物流公司为空则忽略处理
			if (post_company.trim().equals(""))
			{
				Log.warn(jobname, "快递公司为空！订单号:"+orderid+"");
				continue;
			}
			String postcompanyid=getCompanyID(post_company);
			if (postcompanyid.equals(""))
			{
				//如果物流公司为空则忽略处理
				if (post_company.trim().equals(""))
				{
					Log.warn(jobname, "快递公司未配置！快递公司："+post_company+" 订单号:"+orderid+"");
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
					continue;
				}
			}
			if("".equals(post_no)){
				Log.warn(jobname, "快递单号未配置，快递公司："+post_company+" 订单号:"+orderid+"");
				conn.setAutoCommit(false);

				sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);

				sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

				SQLHelper.executeSQL(conn, sql);
				conn.commit();
				conn.setAutoCommit(true);
				continue;
			}
			if("".equals(postcompanyid)){
				Log.warn(jobname, "快递公司编号未配置，快递公司："+post_company+" 订单号:"+orderid+"");
				continue;
			}
	        Date now=new Date();
			String method="scn.vendor.order.delivery";
			/**快递信息**/
			JSONArray kuai_di=new JSONArray();
			JSONObject OrdExpress=new JSONObject();
			//OrdExpress.put("ExpressCompanyId",new String(post_company.getBytes("ISO-8859-1"),"utf-8"));
			OrdExpress.put("ExpressCompanyId",postcompanyid);
			OrdExpress.put("ExpressNo", post_no);
			kuai_di.put(OrdExpress);
			/***data部分***/
			JSONObject data=new JSONObject();
			//需要返回的字段：
			data.put("ShippingFee", 0);   //配送费用
			data.put("VendorOrderNo", orderid);   //供货商订单号
//			data.put("VendorMemo", "");    //供货商备注
			data.put("OrdExpress", kuai_di);    //快递明细
			/**sign部分***/
			String sign=Utils.get_sign(Params.app_Secret,Params.app_key,data, method, now,Params.ver,Params.format);
			/***合并为输出语句****/
			String output_to_server=Utils.post_data_process(method, data, Params.app_key,now, sign).toString();
			//System.out.println(output_to_server);
			String responseOrderListData = Utils.sendByPost(Params.url, output_to_server);
			Log.info("responseOrderListData: "+responseOrderListData);
			JSONObject responseproduct=new JSONObject(responseOrderListData);
					
			//int errorCount=responseproduct.getJSONObject("response").getInt("errorCount");  /*这些判断语句一定要参考GetOrders进行修改，不能因为没有报错就随便他*/
			
			if (responseproduct.getBoolean("IsError"))
			{
				String errdesc="";
				errdesc=errdesc+" "+responseproduct.get("ErrCode").toString()+" "+responseproduct.get("ErrMsg").toString(); 
				Log.warn("订单发货失败,订单号:["+orderid+"],快递公司:["+post_company+"],快递单号:["+post_no+"] 错误信息:"+errdesc);
				if (errdesc.indexOf("订单状态无法发货")>=0/* ||errdesc.indexOf("订单发货失败（查找订单失败）")>0*/)
				{
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
				}
				continue ;
			}
				
			
			try {
				conn.setAutoCommit(false);

				sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);

				sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

				SQLHelper.executeSQL(conn, sql);
				conn.commit();
				conn.setAutoCommit(true);
			} catch (SQLException sqle) {
				if (!conn.getAutoCommit())
					try {
						conn.rollback();
					} catch (Exception e1) {
					}
				try {
					conn.setAutoCommit(true);
				} catch (Exception e1) {
				}
				throw new JSQLException(sql, sqle);
			}
			Log.info(jobname,"处理订单【" + orderid + "】发货成功,快递公司【"+ post_company + "】,快递单号【" + post_no + "】");

		}
	}
	
	private String getCompanyID(String companycode) throws Exception
	{
		String companyid="";
		Object[] cys=StringUtil.split(Params.company, ";").toArray();
		for(int i=0;i<cys.length;i++)
		{
			String cy=(String) cys[i];
			String[] temp  =cy.split(":");
			if(temp[0].equals(companycode)){
				companyid=temp[1];
				break;
			}
				
		}
		return companyid;//
	}
	
	
	private String getRemark(String orderId) throws Exception{
		String result="";
		
		return result;
	}
	
	
	@Override
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
