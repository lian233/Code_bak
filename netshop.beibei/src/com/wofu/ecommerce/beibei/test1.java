package com.wofu.ecommerce.beibei;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.ecommerce.beibei.utils.Utils;
import com.wofu.ecommerce.beibei.Params;

public class test1 {


	public static String url = "http://openapi.yhd.com/app/api/rest/router";
	
	public static String encoding="UTF-8";

	public static String appKey = "10210014041400001508";
	public static String app_secret = "dddedc1d1854ef55443c2f82dee4ec2e";

	public static String checkcode  = "73-66-5256122-71-61-513427-17-80-35-63-128115";
	
	public static String secretkey="XcdFt5934LkoPDTRhGQ9";
	
	public static String erp="self";
	
	public static String erpver="1.0";
	
	public static String format="xml";
	
	public static String ver="1.0";
	
	public static String method="";
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//getProduct();
		getLogisticsCompany();
		//getStock(9335307);
		//getRefund();
		//getOrderDetail();
	}
	
	public static void getOrderDetail() throws Exception 
	{  	
		GetOrders o = new GetOrders();
		Map<String, String> orderlistparams = new HashMap<String, String>();
        //系统级参数设置
        orderlistparams.put("method", "beibei.outer.trade.logistics.get");
		orderlistparams.put("app_id", Params.appid);
        orderlistparams.put("session", Params.session);
        orderlistparams.put("timestamp", o.time());
        orderlistparams.put("version", Params.ver);
        orderlistparams.put("time_range", "modified_time");
        orderlistparams.put("page_size", "20");
        System.out.println(Params.appid);
        System.out.println(Params.session);
        //把系统级参数和应用级参数合起来,post过去
		String responseOrderListData = Utils.sendByPost(orderlistparams, Params.secret, Params.url);
		System.out.println(responseOrderListData);

		
	}
	
	public static void getLogisticsCompany() throws Exception
	{
		GetOrders o = new GetOrders();
		Map<String, String> orderlistparams = new HashMap<String, String>();
        //系统级参数设置
        orderlistparams.put("method", "beibei.outer.trade.logistics.get");
		orderlistparams.put("app_id", Params.appid);
        orderlistparams.put("session", Params.session);
        orderlistparams.put("timestamp", o.time());
        orderlistparams.put("time_range", "modified_time");

        System.out.println(Params.appid);
        System.out.println(Params.session);
        //把系统级参数和应用级参数合起来,post过去
		String responseOrderListData = Utils.sendByPost(orderlistparams, Params.secret, Params.url);
		System.out.println(responseOrderListData);

		
	}
	
	public static void getRefund() throws Exception
	{
		method="yhd.refund.get";
		
		Map<String, String> paramMap = new HashMap<String, String>();
        //系统级参数设置
        paramMap.put("checkCode", checkcode);
        paramMap.put("merchantId", "");
        paramMap.put("erp", erp);
        paramMap.put("erpVer", erpver);
        paramMap.put("format", format);
        paramMap.put("method", method);
        paramMap.put("ver", ver);
        paramMap.put("startTime", "2013-09-10 00:00:00");
        paramMap.put("endTime", "2013-09-17 00:00:00");
        
        String responseData =Utils.sendByPost(paramMap, Params.app_secret, Params.url);
		
		System.out.println(responseData);

		
	}

	
	public static void getProduct() throws Exception
	{
		//method="yhd.serial.products.search";
		
		method="yhd.serial.products.search";
		Map<String, String> paramMap = new HashMap<String, String>();
        //系统级参数设置
        paramMap.put("checkCode", checkcode);
        paramMap.put("merchantId", "L");
        paramMap.put("erp", erp);
        paramMap.put("erpVer", erpver);
        paramMap.put("format", format);
        paramMap.put("method", method);
        paramMap.put("ver", ver);
       
       /* paramMap.put("canShow", "1");
        paramMap.put("canSale", "1");
        paramMap.put("curPage", "1");
        paramMap.put("pageRows", "10");
        paramMap.put("verifyFlg", "2");*/
        paramMap.put("productCname", "宝宝专用抗菌洗衣皂3块装9009");
        
        //paramMap.put("outerIdList","N43523XD2,N43523XD4");
        
		String responseData =Utils.sendByPost(paramMap, Params.app_secret, Params.url);
		
		System.out.println(responseData);

		JSONObject response=new JSONObject(responseData);
		
		JSONArray productlist=response.getJSONObject("response").getJSONObject("serialProductList").getJSONArray("serialProduct");
		
		for(int i=0;i<productlist.length();i++)
		{
			JSONObject product=productlist.getJSONObject(i);
		
			int productId=product.optInt("productId");
			String productCode=product.optString("productCode");
			String outerId=product.optString("outerId");
			
			System.out.println(outerId);
			
			//getStock(productId);
		}
		
	}

	public static void getStock(int productid) throws Exception
	{
		method="yhd.serial.product.get";
		
		Map<String, String> paramMap = new HashMap<String, String>();
        //系统级参数设置
        paramMap.put("checkCode", checkcode);
        paramMap.put("merchantId", "");
        paramMap.put("erp", erp);
        paramMap.put("erpVer", erpver);
        paramMap.put("format", format);
        paramMap.put("method", method);
        paramMap.put("ver", ver);
        paramMap.put("productId", String.valueOf(productid));
        
        //paramMap.put("outerIdList","N43523XD2,N43523XD4");
        
        String responseData =Utils.sendByPost(paramMap, Params.app_secret, Params.url);
		
		System.out.println(responseData);
		
		JSONObject response=new JSONObject(responseData);
		
		JSONArray childseriallist=response.getJSONObject("response").getJSONObject("serialChildProdList").getJSONArray("serialChildProd");
		
		for(int i=0;i<childseriallist.length();i++)
		{
			JSONObject childserial=childseriallist.optJSONObject(i);
			
			String outerId=childserial.optString("outerId");
			String productId=childserial.optString("productId");
			
			System.out.println(productId);
			System.out.println(outerId);
			
			JSONArray stocklist=childserial.getJSONObject("allWareHouseStocList").getJSONArray("pmStockInfo");
			
			for (int j=0;j<stocklist.length();j++)
			{
				JSONObject stock=stocklist.optJSONObject(j);
				System.out.println(stock.optString("warehouseId")+"  "+outerId+"  "+stock.optInt("vs"));
			}
		}
		
	}

	/*
	public static void getStock() throws Exception
	{
		method="yhd.serial.childproducts.get";
		
		Map<String, String> paramMap = new HashMap<String, String>();
        //系统级参数设置
        paramMap.put("checkCode", checkcode);
        paramMap.put("merchantId", merchantid);
        paramMap.put("erp", erp);
        paramMap.put("erpVer", erpver);
        paramMap.put("format", format);
        paramMap.put("method", method);
        paramMap.put("ver", ver);
       // paramMap.put("outerId", "BB126");
        
        paramMap.put("outerIdList","BB12601073");
        
		String responseData = PostClient.sendByPost(url, paramMap, secretkey);
		
		System.out.println(responseData);
		
		JSONObject response=new JSONObject(responseData);
		
		JSONArray childseriallist=response.getJSONObject("response").getJSONObject("serialChildProdList").getJSONArray("serialChildProdInfo");
		
		for(int i=0;i<childseriallist.length();i++)
		{
			JSONObject childserial=childseriallist.optJSONObject(i);
			
			String outerId=childserial.optString("outerId");
			String productId=childserial.optString("productId");
			
			System.out.println(productId);
			System.out.println(outerId);
			
			/*
			JSONArray stocklist=childserial.getJSONObject("allWareHouseStocList").getJSONArray("pmStockInfo");
			
			for (int j=0;j<stocklist.length();j++)
			{
				JSONObject stock=stocklist.optJSONObject(j);
				System.out.println(stock.optString("warehouseId")+"  "+outerId+"  "+stock.optInt("vs"));
			}
			
		}
	
		
	}
	*/
	
	
	public static void getStock() throws Exception
	{
		method="yhd.products.stock.get";
		
		Map<String, String> paramMap = new HashMap<String, String>();
        //系统级参数设置
        paramMap.put("checkCode", checkcode);
        paramMap.put("merchantId", "");
        paramMap.put("erp", erp);
        paramMap.put("erpVer", erpver);
        paramMap.put("format", format);
        paramMap.put("method", method);
        paramMap.put("ver", ver);
        
        paramMap.put("outerIdList","BB12601066");
        
		String responseData =Utils.sendByPost(paramMap, Params.app_secret, Params.url);
		
		System.out.println(responseData);
		
		JSONObject response=new JSONObject(responseData);
		
		JSONArray childseriallist=response.getJSONObject("response").getJSONObject("pmStockList").getJSONArray("pmStock");
		
		for(int i=0;i<childseriallist.length();i++)
		{
			JSONObject childserial=childseriallist.optJSONObject(i);
			
			String outerId=childserial.optString("outerId");
			String productId=childserial.optString("productId");
			
			int quantity=childserial.optInt("vs");
			long warehouseId=childserial.optLong("warehouseId");
			
			System.out.println(productId);
			System.out.println(outerId);
			System.out.println(quantity);
			System.out.println(warehouseId);

		}
	
		
	}
	

}
