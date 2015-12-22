package com.wofu.ecommerce.alibaba;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.auth.AuthService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;


public class OrderDelivery extends Thread {

	private static String jobname = "阿里巴巴订单发货处理作业";

	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				Alibaba.setCurrentDate_Order_delivery(new Date());
			 	Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("client_id", Params.appkey);
			    params.put("redirect_uri", Params.redirect_uri);
			    params.put("client_secret", Params.secretKey);
			    params.put("refresh_token", Params.refresh_token);
			    String returns=AuthService.refreshToken(Params.host, params);
			    JSONObject access=new JSONObject(returns);
		    	Params.token=access.getString("access_token");
		    	
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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	
	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Exception
	{
		String sql = "" ;
		Log.info("本次要发货的数量为: "+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString();
			String postCompany = hto.get("post_company").toString().toUpperCase();
			String postNo = hto.get("post_no").toString();
			String sheetType = String.valueOf(hto.get("sheetType"));

			//更加订单ID获取商品详情里面的订单明细ID
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("orderId", orderID);
			//Log.info("tid: "+orderID);
			params.put("sellerMemberId", Params.sellerMemberId);
			params.put("access_token",Params.token);
			//trade.order.orderList.get
			String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"trade.order.orderList.get",Params.version,Params.requestmodel,Params.appkey);
			String response = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
			Log.info("发货返回的数据 result: "+response);
			JSONObject res=new JSONObject(response);
			boolean resuccess =  res.getJSONObject("result").optBoolean("success");
			if(!resuccess){
				continue;
			}
			JSONArray orderEntries=res.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0).getJSONArray("orderEntries");
			
			
			
			String orderdetailids="";
			for(int j=0; j<orderEntries.length();j++){
				JSONObject o=orderEntries.getJSONObject(j);
				orderdetailids=orderdetailids+o.getLong("id");
				if(j>=0&&j<orderEntries.length()-1){
					orderdetailids=orderdetailids+",";
				}
			}
			
			try 
			{
				boolean success = false ;
				//发货
				if("3".equals(sheetType))
				{
					success = delivery(jobname, orderID, postCompany, postNo,orderdetailids, Params.url, Params.token, Params.appkey, Params.secretKey) ;
				}
				else if("4".equals(sheetType))
					success = ModifyExpressInfo(jobname, orderID, postCompany, postNo,orderdetailids, Params.url, Params.token, Params.appkey, Params.secretKey) ;
				else
				{
					Log.error(jobname, "未知单据类型:"+sheetType) ;
					continue ;
				}
				
				if(success)
				{
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID = '"+ sheetid+ "' and SheetType = "+sheetType;
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype="+sheetType;

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);	
					Log.info("更新发货信息成功，阿里巴巴单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。") ;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace() ;
				Log.info("更新发货信息失败，阿里巴巴单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误代码：" + e.getMessage()) ;
			}
			
		}
	}
	
	private Vector<Hashtable> getDeliveryOrders(Connection conn) throws Exception
	{	

		
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			
			sql = "select  a.sheetid,b.tid, b.companycode,b.outsid,c.defaultarrivedays,a.sheettype from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock) "
				+ "where (a.sheettype=3 or a.sheettype=4) and a.sheetid=b.sheetid and a.receiver='"
				+ Params.tradecontactid + "' and b.companycode=c.companycode";

			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0; i<vt.size();i++)
			{
				
				
				Hashtable<String,String> ht=new Hashtable<String,String>();
			
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(i);
				
				
				
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString().trim());
				String companyid=getCompnayID(hto.get("companycode").toString().trim());
				
				if (companyid.equals("")) 
				{
					Log.info("未配置物流公司代号:"+hto.get("companycode").toString());
					continue;
				}
				
				ht.put("post_company", companyid);
				
				String postno=hto.get("outsid").toString().trim();
				
				ht.put("post_no", postno);
				ht.put("sheetType", String.valueOf(hto.get("sheettype"))) ;
				vtorders.add(ht);
			}
		}
		catch(SQLException sqle)
		{
			Log.error(jobname, "查询发货单信息出错:"+sqle.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
		return vtorders;
	}
	
	private String getCompnayID(String companycode)
	{
		String companyid="";
		
	
		String com[] = Params.company.split(";") ;
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			if(s[0].equals(companycode))
			{
				companyid=s[1];
				break;
			}
		}
		
		return companyid;
		
	}
	

	//发货  --自己联系发货（线下物流）
	private boolean delivery(String jobname,String orderId,String logisticsId,String waybill,String orderdetailids,String SERVER_URL,String token,String appKey,String appSecret)
	{
		boolean flag = false ;
		try 
		{

			String timenow=Formatter.format(new Date(), Formatter.DATE_TIME_MS_FORMAT);
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("memberId",Params.sellerMemberId);
			params.put("orderId", orderId);
			params.put("orderEntryIds", orderdetailids);				//订单明细IDs
			params.put("tradeSourceType", "cbu-trade");
			params.put("logisticsCompanyId", logisticsId);			//物流公司ID
																	//其他物流公司名称
			params.put("logisticsBillNo", waybill);					//运货单号
			params.put("gmtSystemSend", timenow);						//系统发货时间
			params.put("gmtLogisticsCompanySend", timenow);				//卖家发货时间
			params.put("gmtLogisticsCom", timenow);				//卖家发货时间
			params.put("access_token", token);
			String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"e56.logistics.offline.send",Params.version,Params.requestmodel,Params.appkey);
			String response = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
			
			JSONObject res=new JSONObject(response);
			//状态码
			boolean code = res.getBoolean("success") ;
			
			if(code)
			{
				flag = true ;
				Log.info("更新发货信息成功，阿里巴巴单号【" + orderId + "】，快递公司【" + logisticsId + "】，快递单号【" + waybill + "】") ;
			}
			else
			{
				if("35".equals(code) || "61".equals(code))
					flag = true ;
				Log.info("更新发货信息失败，阿里巴巴单号【" + orderId + "】，快递公司【" + logisticsId + "】，快递单号【" + waybill + "】。错误信息：" + res.getString("resultMsg") + "," + res.getString("resultCode")) ;
			}
		} catch (Exception e) {
			flag = false ;
			Log.info("更新发货信息失败，阿里巴巴单号【" + orderId + "】，快递公司【" + logisticsId + "】，快递单号【" + waybill + "】。错误信息：" + e.getMessage()) ;
		}
		return flag ;
	}


	//修改快递信息 V2
	private boolean ModifyExpressInfo(String jobname,String orderId,String logisticsId,String waybill,String orderdetailids,String SERVER_URL,String token,String appKey,String appSecret)
	{
		boolean flag = false ;
		try 
		{
			String timenow=Formatter.format(new Date(), Formatter.DATE_TIME_MS_FORMAT);
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("memberId",Params.sellerMemberId);
			params.put("orderId", orderId);
			params.put("orderEntryIds", orderdetailids);				//订单明细IDs
			params.put("tradeSourceType", "cbu-trade");
			params.put("logisticsCompanyId", logisticsId);			//物流公司ID
																//其他物流公司名称
			params.put("logisticsBillNo", waybill);					//运货单号
			params.put("gmtSystemSend", timenow);						//系统发货时间
			params.put("gmtLogisticsCompanySend", timenow);				//卖家发货时间
			
			params.put("access_token", token);
			String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"e56.logistics.offline.send",Params.version,Params.requestmodel,Params.appkey);
			String response = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
			
			JSONObject res=new JSONObject(response);
			//状态码
			boolean code = res.getBoolean("success") ;
			if(code)
			{
				flag = true ;
				Log.info("快递转件成功,订单号【"+ orderId +"】,快递公司【"+ logisticsId +"】,快递单号【" + waybill +"】") ;
			}
			else
			{
				flag = false ;
				Log.error(jobname, "快递转件失败,订单号【"+ orderId +"】,快递公司【"+ logisticsId +"】,快递单号【" + waybill + "】,错误信息:"+res.getString("resultMsg") + "," + res.getString("resultCode")) ;
			}
			 
		} catch (Exception e) {
			flag = false ;
			Log.error(jobname, "快递转件失败,订单号【"+ orderId +"】,快递公司【"+ logisticsId +"】,快递单号【" + waybill + "】,错误信息:"+e.getMessage()) ;
		}
		return flag ;
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
