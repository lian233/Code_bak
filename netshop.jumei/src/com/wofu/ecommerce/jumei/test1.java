package com.wofu.ecommerce.jumei;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;

public class test1 {


	public static String url = "http://openapi.ext.jumei.com/";
	
	public static String encoding="UTF-8";

	public static String clientid = "2536";

	public static String clientkey  = "1b0ed105d1ec34403ea6fa1c86128004";
	
	public static String signkey="6f061aa6da8af7f02c1c0da1a5564fdfd8335b46";
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//getOrders();
		getLogistics();
		//getOrderInfoByID();
		
		//String s="11,22,33,";
		//System.out.println(s.substring(0, s.length()-1));
		
		//Date d=new Date(1379645870*1000L);
		
		//System.out.println(Formatter.format(d, Formatter.DATE_TIME_FORMAT));
	}
	

	public static void getOrders() throws Exception
	{
		String method="Order/GetOrder";
		
		Map<String, String> paramMap = new HashMap<String, String>();
        //系统级参数设置
        paramMap.put("client_id", clientid);
        paramMap.put("client_key", clientkey);
        paramMap.put("start_date", "2013-09-15 00:00:00");
        paramMap.put("end_date", "2013-09-21 00:00:00");
        paramMap.put("status", "7");
        paramMap.put("page", "1");
        paramMap.put("page_size", "50");
       
        String sign=JuMeiUtils.getSign(paramMap, signkey, encoding);
        
        paramMap.put("sign", sign);
        
        String responseData=CommHelper.sendRequest(url+method, paramMap, "", encoding);
		
		System.out.println(responseData);

	}
	
	public static void getLogistics() throws Exception
	{
		String method="Order/GetLogistics";
		
		Map<String, String> paramMap = new HashMap<String, String>();
        //系统级参数设置
        paramMap.put("client_id", clientid);
        paramMap.put("client_key", clientkey);
       
        String sign=JuMeiUtils.getSign(paramMap, signkey, encoding);
        
        paramMap.put("sign", sign);
        
        String responseData=CommHelper.sendRequest(url+method, paramMap, "", encoding);
		
		//System.out.println(responseData);
        
        JSONObject responseresult=new JSONObject(responseData);
        
        JSONArray logisticslist=responseresult.getJSONArray("result");
        
        for (int i=0;i<logisticslist.length();i++)
        {
        	JSONObject logistics=logisticslist.getJSONObject(i);
        	
        	System.out.println(logistics.getString("id")+"  "+logistics.getString("name"));
        }

	}

	public static void getOrderInfoByID() throws Exception
	{
		String method="Order/GetOrderById";
		
		Map<String, String> paramMap = new HashMap<String, String>();
        //系统级参数设置
        paramMap.put("client_id", clientid);
        paramMap.put("client_key", clientkey);
        paramMap.put("order_id", "65499470");
       
        String sign=JuMeiUtils.getSign(paramMap, signkey, encoding);
        
        paramMap.put("sign", sign);
        
        String responseData=CommHelper.sendRequest(url+method, paramMap, "", encoding);
		
		System.out.println(responseData);

	}
	
}
