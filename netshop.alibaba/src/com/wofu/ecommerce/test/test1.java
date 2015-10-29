package com.wofu.ecommerce.test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.wofu.business.order.OrderManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.alibaba.Alibaba;
import com.wofu.ecommerce.alibaba.Order;
import com.wofu.ecommerce.alibaba.OrderItem;
import com.wofu.ecommerce.alibaba.Params;
import com.wofu.ecommerce.alibaba.Version;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.auth.AuthService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;
/***
 * 测试：获取订单列表
 * @author Administrator
 *
 */
public class test1 {
	private static String lasttime;
	private static long daymillis=24*60*60*1000L;
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	private static String returnFields="offerId";
	public static Connection getConnection() throws Exception
	{

		String driver="com.microsoft.jdbc.sqlserver.SQLServerDriver";
		String url="jdbc:microsoft:sqlserver://172.20.11.116:1433;DatabaseName=ErpDKBMConnect";
		String user="sa";
		String password="sa";
		
		if (driver != null && !driver.equals("")) {
			DriverManager.registerDriver(
				(Driver) Class.forName(driver).newInstance());
		}
		if (user != null) {
			return DriverManager.getConnection(url, user, password);
		} else {
			return DriverManager.getConnection(url);
		}
			
	}
	
	
	public static void main(String[] args) throws Exception{
		getProductDetail(1223336797L);
	}
	
	/**
	 * 获取商品详情
	 * @param offerId  商品id
	 */
	private static void getProductDetail(Long offerId){
		try{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("offerId",offerId+"");
			params.put("returnFields", "offerId,offerStatus,subject,amount,type,gmtCreate,gmtModified,skuArray,productFeatureList");
			String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.get",Params.version,Params.requestmodel,Params.appkey);
			String response =ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
			System.out.println(response);
			JSONObject res=new JSONObject(response);
			JSONObject jo=res.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0);
		}catch(Exception ex){
			Log.error("查询产品详情出错",ex.getMessage());
		}
		
	}
	
	/**
	 * 获取所有的商品产品id
	 */
	private static void getProductList(){
		try{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("type", "ALL") ;
			params.put("returnFields", returnFields);
			params.put("timeStamp","2014-03-20 00:00:00");
			params.put("page", String.valueOf(1)) ;
			params.put("pageSize", "25") ;
			params.put("access_token", getToken());
			params.put("orderBy", "gmt_modified:asc");
			String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.getAllOfferList",Params.version,Params.requestmodel,Params.appkey);
			String responseText = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
			System.out.printf("取商品资料返回结果: "+responseText);
		}catch(Exception ex){
			Log.error("取商品所有的id出错", ex.getMessage());
		}
		
	} 
	
	
	/**
	 * 取得token
	 * @return
	 */
	private static String getToken(){
		try{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("client_id", Params.appkey);
		    params.put("redirect_uri", Params.redirect_uri);
		    params.put("client_secret", Params.secretKey);
		    params.put("refresh_token", Params.refresh_token);
		    String returns=AuthService.refreshToken(Params.host, params);
		    JSONObject access=new JSONObject(returns);
	    	return access.getString("access_token");
		}catch(Exception ex){
			Log.error("取token出错了!", ex.getMessage());
			return "";
		}
		
	}
}
