package com.wofu.ecommerce.meilishuo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.meilishuo.util.CommHelper;
import com.wofu.ecommerce.meilishuo.util.Utils;

public class OrderDelivery extends Thread
{
	private static String jobname = "美丽说订单发货处理作业";
	private static String tradecontactid=Params.tradecontactid ;
	private static Hashtable<String, String> htComCode = Params.htComCode ;
	private boolean is_exporting = false;
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.meilishuo.Params.dbname);
				Params.token = PublicUtils.getToken(connection, Integer.parseInt(Params.tradecontactid));
				doDelivery(connection,getDeliveryOrders(connection));		
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.meilishuo.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	private Vector<Hashtable> getDeliveryOrders(Connection conn)    //在表it_upnote  sheettype=3,ns_delivery,deliveryref表中查询出来要发货的订单
	{	
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
		
				sql = "select  a.notetime,a.sheetid,b.tid, b.companycode,b.outsid from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock)"
					+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
					+ tradecontactid + "' and b.companycode=c.companycode";
			
	
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int k=0; k<vt.size();k++)
			{	
				Hashtable<String,String> ht=new Hashtable<String,String>();
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(k);
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString());
				ht.put("express_code", hto.get("companycode").toString());
				ht.put("post_no", hto.get("outsid").toString());     //快递单号
				//ht.put("notetime", Formatter.format(hto.get("notetime"), Formatter.DATE_TIME_FORMAT));  //发货时间
				vtorders.add(ht);
				//Log.info("getDeliveryOrders.express_code: "+hto.get("companycode").toString());
			}
		}
		catch(SQLException sqle)
		{
			Log.error(jobname, "查询发货单信息出错:"+sqle.getMessage());
		}
		catch(Exception e)
		{
			Log.error(jobname, "查询发货单信息出错:"+e.getMessage());
			//e.printStackTrace() ;
		}
		return vtorders;
	}
	
	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws SQLException
	{
		String sql = "" ;
		Log.info("发货总数为:　"+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString().trim();
			String orderID = hto.get("orderid").toString().trim();
			String postCompany = hto.get("express_code").toString().trim();
			String postNo = hto.get("post_no").toString().trim();
			//Log.info("doDelivery.express_code: "+hto.get("companycode").toString());
			//System.out.println(postCompany);
			try
			{
				
				boolean	success = delivery(jobname, conn, hto) ;
				Log.info("发货成功状态："+success);
				if(success)
				{
					conn.setAutoCommit(false);
					
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
	
					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";
					SQLHelper.executeSQL(conn, sql);
					
					conn.commit();
					conn.setAutoCommit(true);	

				}
			}catch(Exception e)
			{
				if(conn!=null && !conn.getAutoCommit())
					conn.rollback();
					//e.printStackTrace() ;
					Log.info("更新发货信息失败，美丽说单号【" + orderID + "】，快递公司【" + postCompany.toLowerCase() + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
		
			}
		}
	}
	
	private  static boolean delivery(String jobname,Connection conn,Hashtable<String, String> hto)
	{
		boolean flag = false ;
		//订单号
		String orderCode = hto.get("orderid").trim().toString();
		Log.info("orderCode:"+orderCode);
		//物流公司
		String postCompany = htComCode.get(hto.get("express_code").toString());
		String postCompany_trans = "";
		if(postCompany.indexOf("申")>=0) postCompany_trans = "shentong";
		if(postCompany.indexOf("韵")>=0) postCompany_trans = "yunda";
		if(postCompany.indexOf("顺")>=0) postCompany_trans = "shunfeng";
		if(postCompany.indexOf("顺丰四日达")>=0) postCompany_trans = "shunfengsirida";
		if(postCompany.indexOf("宅")>=0) postCompany_trans = "zhaijisong";
		if(postCompany.indexOf("自")>=0) postCompany_trans = "ziti";
		if(postCompany.indexOf("天")>=0) postCompany_trans = "tiantian";
		if(postCompany.indexOf("圆")>=0) postCompany_trans = "yuantong";
		if(postCompany.indexOf("E")>=0) postCompany_trans = "EMS";
		//String postCompany = hto.get("express_code").toString();
		//Log.info("postCompany: "+postCompany);
		//运单号
		String postNo = hto.get("post_no").trim().toString();
		Log.info("postNo: "+postNo);
		try 
		{
			//方法名
			String apimethod="meilishuo.order.deliver";
			HashMap<String, String> param = new HashMap<String,String>();
			param.put("method", apimethod);
			param.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			param.put("format", "json");
			param.put("app_key", Params.appKey);
			param.put("v", "1.0");
			param.put("sign_method", "MD5");
			param.put("session", Params.token);
			param.put("order_id", orderCode);
			param.put("express_company", postCompany_trans);
			param.put("express_id", postNo);
			
			String responseText = Utils.sendbyget(Params.url,
					param,Params.appsecret);
			 //发送请求
			//Log.info("发给美丽说的postCompany: "+postCompany_trans);
			Log.info("返回数据 ："+responseText);
			//把返回的数据转成json对象
			JSONObject responseObj= new JSONObject(responseText);					
			try
			{
				String errormessage = responseObj.getJSONObject(
						"error_response").getString("message"); // 如果没错整个try都不会执行成功，有错就会执行出错过程
				Log.error("美丽说订单发货作业错误", "美丽说订单发货作业，错误码："+errormessage);
				flag=false;
			}catch(Exception e)
			{
				int successNum = responseObj.getJSONObject("order_deliver_response").getJSONObject("info").getInt("affect");
				if(successNum>=1){
					flag=true;
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			Log.info("更新发货信息失败，美丽说单号【" + orderCode + "】，快递公司【" + postCompany_trans.toLowerCase() + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
			flag=false ;
		}
		return flag;
	}
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
