package com.wofu.ecommerce.jingdong.test;
import java.util.ArrayList;
import java.util.List;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.request.Field;
import com.jd.open.api.sdk.request.after.AfterSearchRequest;
import com.jd.open.api.sdk.request.delivery.DeliveryLogisticsGetRequest;
import com.jd.open.api.sdk.request.delivery.EtmsWaybillcodeGetRequest;
import com.jd.open.api.sdk.request.order.OrderGetRequest;
import com.jd.open.api.sdk.request.order.OrderLbpPrintDataGetRequest;
import com.jd.open.api.sdk.response.after.AfterSearchResponse;
import com.jd.open.api.sdk.response.delivery.DeliveryLogisticsGetResponse;
import com.jd.open.api.sdk.response.delivery.EtmsWaybillcodeGetResponse;
import com.jd.open.api.sdk.response.order.OrderGetResponse;
import com.jd.open.api.sdk.response.order.OrderLbpPrintDataGetResponse;
public class test1 {


	private static String SERVER_URL = "http://gw.shop.360buy.com/routerjson" ;
	private static String token = "ef413ef5-9d1c-4ff4-8421-9272e0d8177a" ;
	private static String appKey = "28585580C280B3A93EF4BCAF3630FDEA" ;
	private static String appSecret = "9bb7808f7dcd405e8a9002f7dbb7b9c5" ;
	
	public static void main(String args[]) throws Exception
	{
		getDeliveryCompanyName();
		//getDeliveryCompanyName();
		//getOrderLBPPrintData();
		//getRefund();
		//getFullTrade();
	}
	
	private static void getFullTrade() throws Exception
	{
		System.out.println("sss");
		DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		OrderGetRequest request = new OrderGetRequest();
		request.setOrderId("807315842");
		OrderGetResponse response=client.execute(request);
		System.out.println(response.getMsg());
	}
	
	private static void getJDPostNo() throws Exception
	{
		System.out.println("ddd");
		JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		EtmsWaybillcodeGetRequest  request = new EtmsWaybillcodeGetRequest ();
		request.setPreNum("1");
		request.setCustomerCode("020K1181");
		EtmsWaybillcodeGetResponse response = client.execute(request);
		System.out.println(response.getMsg());
		
	}
	
	//获取快递公司名称
	private static void getDeliveryCompanyName()
	{

		try 
		{
			System.out.println("ehllo");
			com.jd.open.api.sdk.DefaultJdClient client = new com.jd.open.api.sdk.DefaultJdClient(SERVER_URL,token,appKey,appSecret);
			DeliveryLogisticsGetRequest request = new DeliveryLogisticsGetRequest();
			
			DeliveryLogisticsGetResponse response = client.execute(request);
					 
			System.out.println(response.getMsg()) ;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//获取LBP面单打印
	private static void getOrderLBPPrintData()
	{

		try 
		{
			
			
			DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
			
			
			OrderLbpPrintDataGetRequest request = new OrderLbpPrintDataGetRequest();
			request.setOrderId("525980364");
		
			OrderLbpPrintDataGetResponse response = client.execute(request);
					 
			System.out.println(response.getMsg()) ;
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private static void getRefund() throws Exception
	{
		DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		AfterSearchRequest request = new AfterSearchRequest();
		String selectFields = "return_id,vender_id,send_type,receive_state,linkman,phone,return_address,consignee,consignor,send_time,receive_time,modifid_time,return_item_list" ;
		Field time_type = new Field("time_type", "MODIFIEDTIME");
		Field start_time = new Field("start_time", "2013-09-15 00:00:00");
		Field end_time = new Field("end_time", "2013-09-17 00:00:00");
		Field receive_state = new Field("receive_state", "WAITING");
		List<Field> queryFields = new ArrayList<Field>();
		queryFields.add(time_type);
		queryFields.add(start_time);
		queryFields.add(end_time);
		queryFields.add(receive_state);
		
		request.setQueryFields(queryFields);
		request.setSelectFields(selectFields) ;
		request.setPageSize("10");
		request.setPage("1");
		AfterSearchResponse response = client.execute(request);
		
		System.out.println(response.getMsg());
	}
		
	
}
