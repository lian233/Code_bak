package com.wofu.ecommerce.zdt;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.zdt.utils.Utils;
public class OrderDelivery extends Thread {

	private static String jobname = "掌店通订单发货处理作业";
	
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(Params.dbname);

				delivery(connection);	
				
				//modifiRemark(connection);

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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))
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
			String orderid = hto.get("tid").toString();
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
					continue;
				}
			}
	
			Map<String, String> params = new HashMap<String, String>();
	        //系统级参数设置
			params.put("app_key", Params.app_key);
			params.put("format", Params.format);
			params.put("method", "ecm.order.send");
			params.put("sign_method", "MD5");
			params.put("v", Params.ver);
			params.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			params.put("order_no", orderid);
			params.put("company",postcompanyid);
			params.put("out_sid", post_no);
		
			String responseOrderListData = Utils.sendByPost(params, Params.app_secret, Params.url);
			Log.info("result:　"+responseOrderListData);
			JSONObject responseproduct=new JSONObject(responseOrderListData).getJSONObject("res_data");
					
			if (!responseproduct.isNull("code"))
			{
				if(-403!=responseproduct.getInt("code")){
					String errdesc=responseproduct.getString("msg");
					Log.warn("订单发货失败,订单号:["+orderid+"],快递公司:["+post_company+"],快递单号:["+post_no+"] 错误信息:"+errdesc);
					
					continue ;
				}
				
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
			
			Object[] cs=StringUtil.split(cy, ":").toArray();
			
			String ccode=(String) cs[0];
			String cid=(String) cs[1];
			
			if(ccode.equals(companycode))
			{
				companyid=cid;
				break;
			}
		}
		
		return companyid;
	}
	
	
	
	//修改订单备注
	private void modifiRemark(Connection conn) throws Exception{
		String sql = "select sheetid,sender from it_upnote where sheettype=5 and flag=0 and receiver='"+Params.tradecontactid+"'";
		Vector modifiReList = SQLHelper.multiRowSelect(conn, sql);
		if(modifiReList.size()==0) return;
		for(int i=0;i<modifiReList.size();i++){
			try{
				Hashtable item = (Hashtable)modifiReList.get(i);
				String sheetid = item.get("sheetid").toString();
				String sender = item.get("sender").toString();
				String[] remarks = sender.split(":");
				//订单号
				String tid = remarks[0];
				//备注
				String remark = remarks[1];
				Map<String, String> params = new HashMap<String, String>();
		        //系统级参数设置
				params.put("appKey", Params.app_key);
				params.put("sessionKey", Params.token);
				params.put("format", Params.format);
				params.put("method", "yhd.order.merchant.remark.update");
				params.put("ver", Params.ver);
				params.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				params.put("orderCode", tid);
				params.put("remark",remark);
		        
				String response = Utils.sendByPost(params, Params.app_secret, Params.url);
				//Log.info("修改备注:　"+response);
				
				JSONObject responseCount=new JSONObject(response);
				if(0==(responseCount.getJSONObject("response").getInt("errorCount"))){
					try{
						conn.setAutoCommit(false);
						sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 5";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=5";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
					Log.info(jobname,"处理订单【" + tid + "】 sheetid【"+sheetid+"】 修改备注成功");	
					}catch(Exception ex){
						if(!conn.getAutoCommit()){
							try{
								conn.rollback();
							}catch(Exception el){}
							
						}
						try{
							conn.setAutoCommit(true);
						}catch(Exception es){}
						throw new Exception ("事务回滚失败！");
					}
				}else{
					Log.info(jobname,"处理订单【" + tid + "】 sheetid【"+sheetid+"】 修改备注失败");
				}
			}catch(Exception ex){
				Log.error(jobname, ex.getMessage());
			}
			
		}

		Log.info(jobname+",修改订单备注完成");
		
	}
	
	private String getRemark(String orderId) throws Exception{
		String result="";
		
		return result;
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
