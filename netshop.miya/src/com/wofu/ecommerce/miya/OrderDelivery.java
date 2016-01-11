package com.wofu.ecommerce.miya;
import java.sql.Connection;
import java.sql.SQLException;
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
import com.wofu.ecommerce.miya.Params;
import com.wofu.ecommerce.miya.utils.Utils;
public class OrderDelivery extends Thread {

	private static String jobname = "贝贝网订单发货处理作业";
	
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(Params.dbname);

				delivery(connection);	

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
			+ Params.tradecontactid + "' and b.iswait=0 ";
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
			Map<String, String> confirmlistparams = new HashMap<String, String>();
	        //系统级参数设置
			confirmlistparams.put("method", "mia.order.confirm");
			confirmlistparams.put("vendor_key", Params.vendor_key);
			confirmlistparams.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
			confirmlistparams.put("version", Params.ver);
			//应用级输入参数
			confirmlistparams.put("order_id", orderid);
			String responseConfirmListData = Utils.sendByPost(confirmlistparams, Params.secret_key, Params.url);
		    JSONObject confirmData = new JSONObject(responseConfirmListData);
			//判断是否已经打单或者打单成功
			int code = confirmData.optInt("code");
			String msg = confirmData.optString("msg");
			if(code!=200){
				Log.error("打单失败失败，跳过此单","订单号:"+orderid+" 错误信息:"+ msg+" 错误号:"+code);
				if(code==181){
					Log.info("订单已经发货，删除IT_UpNote" +sheetid+"订单号"+orderid);
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
			}
			//查询iid 商品流水号
			Vector<Hashtable> itemList = new Vector<Hashtable>();
			sql = "select iid from ns_orderitem(NOLOCK) where oid = '"+orderid+"'";
			itemList=SQLHelper.multiRowSelect(conn, sql);

			if(itemList.size()<=0){
				Log.error("发货找不到商品明细","订单号:"+orderid);
				continue;
			}
			StringBuffer item_id = new StringBuffer();
			System.out.println(itemList.size());
			for(int j=0;j<itemList.size();j++){
				Hashtable ht=(Hashtable) itemList.get(j);
				String item = ht.get("iid").toString().trim();
				if(j<1){
					item_id.append(item);
				}else{
					item_id.append(","+item);
				}
			}
			
			JSONArray sheet_code_info = new  JSONArray();
			JSONObject express = new JSONObject();
			express.put("sheet_code", post_no);
			express.put("logistics_id", postcompanyid);
			sheet_code_info.put(express);
			
			System.out.println(sheet_code_info.toString());
			System.out.println(item_id.toString());
			
			
			Map<String, String> deliverlistparams = new HashMap<String, String>();
	        //系统级参数设置
			deliverlistparams.put("method", "mia.order.deliver.upgrade");
			deliverlistparams.put("vendor_key", Params.vendor_key);
			deliverlistparams.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
			deliverlistparams.put("version", Params.ver);
			//应用级输入参数
			deliverlistparams.put("sheet_code_info", sheet_code_info.toString());
			deliverlistparams.put("item_id", item_id.toString());
			deliverlistparams.put("order_id", orderid);
			
			
			String responseOrderListData2 = Utils.sendByPost(deliverlistparams, Params.secret_key, Params.url);
			JSONObject responseproduct=new JSONObject(responseOrderListData2);
			System.out.println(Utils.Unicode2GBK(responseOrderListData2));
			String msg2 = responseproduct.optString("msg");
			int code2 = responseproduct.optInt("code");

			if(code2!=200&&code2!=164){
				Log.warn("订单发货失败,订单号:["+orderid+"],快递公司:["+post_company+"],快递单号:["+post_no+"] 错误信息:"+msg2);
//				if (errdesc.indexOf("状态异常")>=0 ||errdesc.indexOf("订单发货失败（查找订单失败）")>0)
//				{
//					conn.setAutoCommit(false);
//
//					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
//							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
//							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
//					SQLHelper.executeSQL(conn, sql);
//
//					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";
//
//					SQLHelper.executeSQL(conn, sql);
//					conn.commit();
//					conn.setAutoCommit(true);
//				}
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
	
	

	public String time() {
		Long time= System.currentTimeMillis()/1000;
		return time.toString();
	}
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
