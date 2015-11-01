package com.wofu.ecommerce.huasheng;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.huasheng.util.*;

public class OrderDelivery extends Thread {

	private static String jobname = "跨境商城(花生API)订单发货处理作业";
	private static String tradecontactid = Params.tradecontactid ;
	private boolean is_exporting = false;
	
	//执行操作
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Date nowtime = new Date();
			if(Params.startTime.getTime() <= nowtime.getTime())
			{//符合或超过指定的启动时间
				Connection connection = null;
				is_exporting = true;
				try {		
					connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.weipinhui.Params.dbname);
					//记录当前执行时间(用于判断是否线程假死)
					huasheng.setCurrentDate_orderDelivery(new Date());
					//查询发货单信息后订单发货
					doDelivery(connection,getDeliveryOrders(connection,3));
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
				Log.info(jobname + "下次执行等待时间:" + Params.waittime + "秒");
				long startwaittime = System.currentTimeMillis();
				while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.weipinhui.Params.waittime * 1000))
					try {
						sleep(1000L);
					} catch (Exception e) {
						Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
					}
			}
			else
			{//等待启动
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
			}
		} while (true);
	}
	
	///////////////////////////////////////////////////////////
	/**
	 * 查询发货单信息(在表it_upnote  sheettype=3,ns_delivery,deliveryref表中查询出来要发货的订单)
	 * @param conn 数据库连接
	 * @param sheettype 发货类型，3为发货，4为修改物流信息
	 * @return 发货单信息表
	 */
	private Vector<Hashtable> getDeliveryOrders(Connection conn,int sheettype)
	{	
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			sql = "select a.notetime,a.sheetid,b.tid, b.companycode,b.outsid,a.sheettype from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock)"
				+ "where a.sheettype = " + sheettype + " and a.sheetid=b.sheetid and a.receiver='"
				+ tradecontactid + "' and b.companycode=c.companycode";
			
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int k=0; k<vt.size();k++)
			{	
				Hashtable<String,String> ht=new Hashtable<String,String>();
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(k);
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString().trim().replaceAll("[?]", ""));
				ht.put("express_code", hto.get("companycode").toString().trim());
				ht.put("transport_no", hto.get("outsid").toString().trim());     //快递单号
				ht.put("sheettype", String.valueOf(hto.get("sheettype")));     //发货类型，3为发货，4为修改物流信息
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
	
	/**
	 * 订单发货(IT_UpNote -> IT_UpNoteBak)
	 * @param conn 数据库连接
	 * @param vdeliveryorder 发货单信息表
	 * @throws Exception
	 */
	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Exception
	{
		String sql = "" ;
		Log.info("待发货总数为:　"+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			//当前发货的信息
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();		//系统内容部订单号
			String orderID = hto.get("orderid").toString();		//订单号
			String postCompany = hto.get("express_code").toString();		//快递公司编号
			String postNo = hto.get("transport_no").toString();		//快递单号
			String sheetType = hto.get("sheettype").toString();		//发货类型，3为发货，4为修改物流信息
			try 
			{
				//更新发货状态
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
			}
			catch (Exception e) 
			{	if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				Log.info("发货失败,【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
			}
		}
	}
	
	/**
	 * 自发货订单--更新发货状态
	 * @param jobname
	 * @param conn
	 * @param hto 当前发货的信息
	 * @return 是否成功
	 * @throws Exception
	 */
	private static boolean delivery(String jobname,Connection conn,Hashtable<String, String> hto) throws Exception
	{
		boolean flag = false ;
		//订单号
		String orderCode = hto.get("orderid").toString();
		//快递公司
		String postCompanyCode = hto.get("express_code").toString();
		try
		{
			//尝试找出对应快递公司名称
			String tmp = Params.htPostCompany.get(postCompanyCode);
			if(!tmp.equals("") && !tmp.equals(null))
				postCompanyCode = tmp;
		}
		catch(Exception err) {}
		//运单号
		String postNo = hto.get("transport_no").toString();
		
		//检查是否可以发货
		//查询客户订单表
		String sql = "select count(*) from customerorder where flag=100 and refsheetid='"+orderCode+"'";
		//String sql = "select count(*) from customerorder0 where flag=100 and refsheetid='"+orderCode+"'";	//测试用.
		int counter = SQLHelper.intSelect(conn, sql);
		if(counter != 1){
			//进行了拆单或者合单的不能发货
			//拆单:无须再拆单,对方已经拆好了传过来了
			//合单:对方接口不允许
			Log.error(jobname,"订单【" + orderCode + "】不符合发货要求:订单进行过拆单或者合单,对方发货接口不允许操作!");
			return false;
		}
		
		//准备发货数据
		String paramStr = "order_id=" + orderCode + "&express_company=" + postCompanyCode + "&express_id=" + postNo;

		//发送请求
		try 
		{	
			Log.info("发送发货请求 ...");
			String result = Utils.doRequest("deliver", paramStr, false);
			if(result.equals(""))
				return false;
			
			JSONObject resultJson = new JSONObject(result);
			if(resultJson.getBoolean("state"))
			{
				Log.info("订单发货成功，单号【" + orderCode + "】，快递公司【" + postCompanyCode + "】，快递单号【" + postNo + "】") ;
				flag = true;
			}
			else
			{
				Log.info("订单发货失败，单号【" + orderCode + "】，快递公司【" + postCompanyCode + "】，快递单号【" + postNo + "】。错误信息：" + resultJson.getString("msg")) ;
				flag=false;
			}
		} catch (Exception e) {
			Log.info("订单发货失败，单号【" + orderCode + "】，快递公司【" + postCompanyCode + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
			flag=false ;
		}
		return flag ;
	}
	

	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
