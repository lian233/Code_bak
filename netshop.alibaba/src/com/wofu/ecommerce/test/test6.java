//package com.wofu.ecommerce.test;
//
//import java.text.DateFormat;
//import java.util.Date;
//import java.util.Hashtable;
//import java.util.LinkedHashMap;
//
//import javax.swing.text.DateFormatter;
//import com.alibaba.openapi.client.AlibabaClient;
//import com.alibaba.openapi.client.Request;
//import com.alibaba.openapi.client.Response;
//import com.alibaba.openapi.client.auth.AuthorizationToken;
//import com.alibaba.openapi.client.policy.ClientPolicy;
//import com.alibaba.openapi.client.policy.Protocol;
//import com.alibaba.openapi.client.policy.RequestPolicy;
//import com.alibaba.openapi.client.policy.RequestPolicy.HttpMethodPolicy;
//import com.wofu.common.json.JSONObject;
//import com.wofu.common.tools.util.Formatter;
//import com.wofu.ecommerce.alibaba.Params;
//import com.wofu.ecommerce.alibaba.api.ApiCallService;
//import com.wofu.ecommerce.alibaba.auth.AuthService;
//import com.wofu.ecommerce.alibaba.util.CommonUtil;
//
//public class test6 {
//	
//	public static void getOrderList()throws Exception{
//		ClientPolicy policy = ClientPolicy.getDefaultChinaAlibabaPolicy();
//		policy = policy.setAppKey(Params.appkey).setSigningKey(Params.secretKey);
//		AlibabaClient client = new AlibabaClient(policy);
//		client.start();
//		AuthorizationToken authorizationToken = client.refreshToken(Params.refresh_token);
//		
//		RequestPolicy basePolicy = new RequestPolicy().setContentCharset("UTF-8").setTimeout(3000);
//		RequestPolicy testpolicy=basePolicy.clone();
//		testpolicy.setHttpMethod(HttpMethodPolicy.POST);
//		testpolicy.setRequestProtocol(Protocol.param2);
//		
//		testpolicy.setResponseProtocol(Protocol.param2);
//		testpolicy.setNeedAuthorization(true).setUseSignture(true);
//		Request apiRequest = new Request(Params.namespace, "trade.order.orderList.get",1);
//		apiRequest.setParam("sellerMemberId", "b2b-1704364314");
//		apiRequest.setParam("modifyStartTime","2013-09-14 00:00:00");
//		apiRequest.setParam("modifyEndTime","2013-10-14 00:00:00");
//		apiRequest.setAccessToken(authorizationToken.getAccess_token());
//		apiRequest.setAuthToken(authorizationToken);
//		Object obj=client.send(apiRequest, Object.class,testpolicy);
//		JSONObject json=new JSONObject(obj);
//		System.out.println(json);
//		System.out.println(obj.toString());
//		if (client != null) client.shutdown();
//		
//	}
//	
//	public static void getAllOrder()throws Exception{
//		Hashtable<String, String> params = new Hashtable<String, String>() ;
////		String url="http://163.com";
////		Hashtable<String, String> params2 = new Hashtable<String, String>() ;
////		params2.put("client_id", Params.appkey);
////	    params2.put("redirect_uri", Params.redirect_uri);
////	    params2.put("client_secret", Params.secretKey);
////	    params2.put("refresh_token", Params.refresh_token);
////	    String returns=AuthService.refreshToken(Params.host, params2);
////	    JSONObject access=new JSONObject(returns);
////    	Params.token=access.getString("access_token");
//    	Params.token="4259c14a-c1da-417a-86b1-8ba1914804b8";
//    	//System.out.println(access.getString("access_token"));
//    	params.put("sellerMemberId", "b2b-1704364314");
//    	params.put("pageSize", "1") ;
//		params.put("page", "1");
//    	params.put("access_token", Params.token);
//		String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"trade.order.list.get",2,Params.requestmodel,Params.appkey);
//		String response =ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
//		System.out.println(response);
//	}
//	
//	
//	public static void getOneOrder()throws Exception{
//		String response="";
//		Hashtable<String, String> params = new Hashtable<String, String>() ;
//		Params.token="4259c14a-c1da-417a-86b1-8ba1914804b8";
//		params.put("id", "452894764244481");
//    	params.put("access_token", Params.token);
//		String urlPath;
//		try {
//			urlPath = CommonUtil.buildInvokeUrlPath(Params.namespace,"trade.order.detail.get",1,Params.requestmodel,Params.appkey);
//			response =ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
//		} catch (RuntimeException e) {
//			String s=e.getMessage().split("response:")[1];
//			JSONObject json=new JSONObject(s);
//			System.out.println(json);
//			//e.printStackTrace();
//		}
//		System.out.println(response);
//	}
//	
//	
//	private static String type="SALE";
//	private static String returnFields="offerId,offerStatus,subject,amount,amountOnSale,saledCount,type,gmtCreate,gmtModified,skuArray,productFeatureList";
//
//	public static void getAllOffer()throws Exception{
//		Hashtable<String, String> params = new Hashtable<String, String>() ;
//		Params.token="4259c14a-c1da-417a-86b1-8ba1914804b8";
//		params.put("type", type) ;
//		params.put("returnFields", returnFields);
//    	params.put("access_token", Params.token);
//		String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.getAllOfferList",1,Params.requestmodel,Params.appkey);
//		String response =ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
//		System.out.println(response);
//	}
//	public static void getOneOffer()throws Exception{
//		Hashtable<String ,String> params = new Hashtable<String,String>();
//		Params.token="4259c14a-c1da-417a-86b1-8ba1914804b8";
//		params.put("offerId", "1257893833");
//		params.put("returnFields","offerId,offerStatus,subject,amount,type,gmtCreate,gmtModified,skuArray,productFeatureList");
//		params.put("access_token", Params.token);
//		String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.get",1,Params.requestmodel,Params.appkey);
//		String response =ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
//		System.out.println(response);
//	}
//	
//	public static void updateStock()throws Exception{
//		Hashtable<String, String> params = new Hashtable<String, String>() ;
//		Params.token="4259c14a-c1da-417a-86b1-8ba1914804b8";
//		params.put("offerId", "452894764244481");
//		params.put("offerAmountChange","1");
//		params.put("skuAmountChange", "{b13609aa99b8c0a59988e629d621fc07:1}");
//		
//    	params.put("access_token", Params.token);
//		String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"trade.order.detail.get",1,Params.requestmodel,Params.appkey);
//		String response =ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
//		System.out.println(response);
//	}
//	public static void main(String[] args)throws Exception {
//		//getOrderList();
//		//getAllOrder();
//		getOneOrder();
//		//getAllOffer();
//		//getOneOffer();
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	public static Date toDateFormat(String s)throws Exception{
//		String m=s.substring(0, 14);
//		String date=m.substring(0, 4)+"-"
//					+m.substring(4,6)+"-"+
//					m.substring(6,8)+" "+
//					m.substring(8,10)+":"+
//					m.substring(10,12)+":"+
//					m.substring(12,14);
//
//		return Formatter.parseDate(date, Formatter.DATE_TIME_FORMAT);
//	}
//}
