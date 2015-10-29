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
public class test2 {


	private static String SERVER_URL = "http://gw.shop.360buy.com/routerjson" ;
	private static String token = "e2a22be4-15b1-4782-8e50-4b20053bff10" ;
	private static String appKey = "2AC7B8D76AA4A6F9A95C6919212E3A15" ;
	private static String appSecret = "11476a7291d34df1954a386e08dc375a" ;
	
	public static void main(String args[]) throws Exception
	{
		//getJDPostNo();
		getDeliveryCompanyName();
		//getOrderLBPPrintData();
		//getRefund();
		//getFullTrade();
	}
	
	private static void getFullTrade() throws Exception
	{
		DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		OrderGetRequest request = new OrderGetRequest();
		request.setOrderId("807315842");
		OrderGetResponse response=client.execute(request);
		System.out.println(response.getMsg());
	}
	
	private static void getJDPostNo() throws Exception
	{
		JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		EtmsWaybillcodeGetRequest  request = new EtmsWaybillcodeGetRequest ();
		request.setPreNum("1");
		request.setCustomerCode("020K0412");
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
