package com.wofu.ecommerce.suning;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.suning.util.CommHelper;
public class OrderDelivery extends Thread {

	private static String jobname = "苏宁订单发货处理作业";
	private static String tradecontactid=Params.tradecontactid ;
	private static Hashtable<String, String> htComCode = Params.htComCode ;
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.suning.Params.dbname);
				SuNing.setCurrentDate_DevOrder(new Date());
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.suning.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
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
			String postCompany = hto.get("post_company").toString();
			String postNo = hto.get("post_no").toString();
			System.out.println(postCompany);
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
			}
			catch (Exception e) 
			{	if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				//e.printStackTrace() ;
				Log.info("更新发货信息失败，苏宁单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
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
				ht.put("post_company", hto.get("companycode").toString());
				ht.put("post_no", hto.get("outsid").toString());     //快递单号
				ht.put("notetime", Formatter.format(hto.get("notetime"), Formatter.DATE_TIME_FORMAT));  //发货时间
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
		String postCompany = htComCode.get(hto.get("post_company").toString());
		//运单号
		String postNo = hto.get("post_no").toString();
		//产品编号
		String prods = getStrList(conn,orderCode);
		//发货时间
		String deveryTime= hto.get("notetime").toString();
		try 
		{	
			//方法名
			String apimethod="suning.custom.orderdelivery.add";
			HashMap<String,String> reqMap = new HashMap<String,String>();
	        reqMap.put("orderCode", orderCode);
	        reqMap.put("expressNo", postNo);
	        reqMap.put("deliveryTime",deveryTime);
	        reqMap.put("expressCompanyCode", postCompany);
	        reqMap.put("sendDetail", prods);
	      //  reqMap.put("orderLineNumbers", "{'orderLineNumber':[]}");
	        reqMap.put("orderLineNumbers", "{}");
	        String ReqParams = CommHelper.getJsonStr(reqMap, "orderDelivery");
	        ReqParams=ReqParams.replace("sendDetail\":\"{","sendDetail\":{");
	        ReqParams = ReqParams.replace("}\"", "}");
	        ReqParams=ReqParams.replace("]}\",\"expressCompanyCode","]},\"expressCompanyCode");
	       // ReqParams=ReqParams.replace("\"{'orderLineNumber':[]}\"","{\"orderLineNumber\":['']}");
	        ReqParams=ReqParams.replace("\"{}\"", "{}");
	        ReqParams=ReqParams.replace("orderLineNumbers\":\"{", "orderLineNumbers\":{");
	        Log.info("请求字符串: "+ReqParams);
	        HashMap<String,Object> map = new HashMap<String,Object>();
	        map.put("appSecret", Params.appsecret);
	        map.put("appMethod", apimethod);
	        map.put("format", Params.format);
	        map.put("versionNo", "v1.2");
	        map.put("appRequestTime", CommHelper.getNowTime());
	        map.put("appKey", Params.appKey);
	        map.put("resparams", ReqParams);
	        //发送请求
			String responseText = CommHelper.doRequest(map,Params.url);
			Log.info("返回数据 ："+responseText);
			//把返回的数据转成json对象
			JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
			
			//错误对象 
			if(responseText.indexOf("sn_error")!=-1){   //发生错误
				String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
				Log.error("苏宁订单发货作业错误", "苏宁订单发货作业，错误码："+operCode);
				if("biz.orderDelivery.order-status:error".equals(operCode)){
					flag=true;
					Log.info("订单已经发货了,订单号: "+orderCode);
				}else{
					flag=false;
				}
				
				
			}
			else
			{
			
				JSONArray sendDetails= responseObj.getJSONObject("sn_body").getJSONObject("orderDelivery").getJSONArray("sendDetail");
				for(int j = 0 ; j < sendDetails.length() ; j++)
				{
					JSONObject orderInfo =sendDetails.getJSONObject(j) ;
					String result = orderInfo.getString("sendresult");
	
					//如果返回状态码为0，且返回单号与提交单号相同，则本次更新成功
					if("Y".equals(result))
					{
						flag = true ;
						Log.info("更新发货信息成功，苏宁单号【" + orderCode + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】,产品编码【"+orderInfo.getString("productCode")+"】") ;
					}

					else
					{
						flag = false ;
						Log.error(jobname,"更新发货信息失败，苏宁单号【" + orderCode + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】,产品编码【"+orderInfo.getString("productCode")+"】") ;
					}
				}
			}
			
		} catch (Exception e) {
			Log.info("更新发货信息失败，苏宁单号【" + orderCode + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
			flag=false ;
		}
		
		return flag ;
	}
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	

	
	//根据苏宁订单号查询这个订单的所有产品 生成商品列表字符串
	public static String getStrList(Connection conn,String tid){
		//苏宁所有的商品的苏宁商家编码都写到了ns_orderitem表的iid字段
		List<String> listsResult=null;
		try{
			listsResult = new ArrayList<String>();
			String sql = new StringBuilder().append("select distinct a.iid from ns_orderitem a, ns_customerorder b  where b.tid='").append(tid).append("' and a.SheetID=b.sheetid").toString();
			listsResult=SQLHelper.multiRowListSelect(conn, sql);
		}catch(Exception ex){
			Log.info("苏宁查询订单对应的苏宁商品编码出错了!");
			return "";
		}
		
		System.out.println("商品编码1"+listsResult);
		
		//生成list形式字符串
		StringBuilder sendDetails= new StringBuilder().append("{'productCode': [");
		for(int i=0;i<listsResult.size();i++){
			sendDetails.append("'").append(listsResult.get(i)).append("',");
		}

		return sendDetails.deleteCharAt(sendDetails.length()-1).append("]}").toString();
	}
}
