package com.wofu.ecommerce.icbc;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.icbc.util.CommHelper;
public class OrderDelivery extends Thread {
	private static String jobname = "联想加盟店订单发货处理作业";
	private static String tradecontactid=Params.tradecontactid ;
	private static Hashtable<String, String> htComCode = Params.htComCode ;
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.icbc.Params.dbname);
				doDelivery(connection,getDeliveryOrders(connection));
				modifiedDeliveryInfo(connection,getModifiedDeliveryOrders(connection));	
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.icbc.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	private void modifiedDeliveryInfo(Connection conn,
			Vector<Hashtable> vdeliveryorder) throws Exception{
		String sql = "" ;
		Log.info("修改发货信息订单总数为:　"+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString();
			String postCompany = hto.get("express_code").toString();
			String postNo = hto.get("post_no").toString();
			try 
			{
				
				boolean	success = delivery(jobname, conn, hto) ;
				Log.info("发货成功状态："+success);
				if(success)
				{
					conn.setAutoCommit(false);
	
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
					SQLHelper.executeSQL(conn, sql);
	
					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";
					SQLHelper.executeSQL(conn, sql);
					
					conn.commit();
					conn.setAutoCommit(true);
					Log.info("修改物流发货信息成功，联想加盟店单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】") ;
				}
			}
			catch (Exception e) 
			{	if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				//e.printStackTrace() ;
				Log.info("修改物流信息失败，联想加盟店单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
			}
			
		}
		
	}

	private Vector<Hashtable> getModifiedDeliveryOrders(Connection conn) throws Exception{
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
		
				sql = "select  a.notetime,a.sheetid,b.tid, b.companycode,b.outsid from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock)"
					+ "where a.sheettype=4 and a.sheetid=b.sheetid and a.receiver='"
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
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString();
			String postCompany = hto.get("express_code").toString();
			String postNo = hto.get("post_no").toString();
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
					Log.info("发货成功，联想加盟店单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】") ;
				}
			}
			catch (Exception e) 
			{	if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				//e.printStackTrace() ;
				Log.info("更新发货信息失败，联想加盟店单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
			}
			
		}
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
	

	//自发货订单--更新发货状态
	private  static boolean delivery(String jobname,Connection conn,Hashtable<String, String> hto)
	{
		boolean flag = false ;
		//订单号
		String orderCode = hto.get("orderid").toString();
		//物流公司
		String postCompany = hto.get("express_code").toString();
		Log.info("postCompany: "+postCompany);
		//运单号
		String postNo = hto.get("post_no").toString();
		try 
		{	
			//方法名
			long SendTime = new Date().getTime()/1000L;
			String apimethod="get_delivery_num.php";
			HashMap<String,Object> map = new HashMap<String,Object>();
	        map.put("oid", orderCode);
	        map.put("company_num", postCompany);
	        map.put("apimethod", apimethod);
	        map.put("delivery_num", postNo);
	        map.put("send_time",SendTime);  //php 10位整数为时间值
	        //map.put("key", MD5Util.getMD5Code((Params.vcode+SendTime).getBytes()));
	        //传多一个customerdelive的sheetid
	        map.put("wofu_id",getSheetId(orderCode,conn));
	        //发送请求
			String responseText = CommHelper.doPost(map,Params.url);
			Log.info("返回数据 ："+responseText);
			//把返回的数据转成json对象
			JSONObject responseObj= new JSONObject(responseText);
			int resultCode= responseObj.getInt("status");
			//错误对象 
			if(resultCode!=1){   //发生错误
				Log.error("联想加盟店订单发货作业错误", "联想加盟店订单发货作业，错误码："+resultCode);
				flag=false;
				
			}
			else
			{
					flag=true;
			}
			
		} catch (Exception e) {
			Log.info("更新发货信息失败，联想加盟店单号【" + orderCode + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
			flag=false ;
		}
		
		return flag ;
	}
	//获取customerdelive的sheetid
	private static String getSheetId(String orderId,Connection conn) throws Exception{
		String sql = "select sheetid from customerdelive where CustomerSheetID='"+orderId+"'";
		String sheetid = SQLHelper.strSelect(conn, sql);
		return sheetid;
	}
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
