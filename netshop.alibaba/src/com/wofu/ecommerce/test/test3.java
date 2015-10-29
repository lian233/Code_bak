package com.wofu.ecommerce.test;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.wofu.business.order.OrderManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.alibaba.Alibaba;
import com.wofu.ecommerce.alibaba.Children;
import com.wofu.ecommerce.alibaba.Goods;
import com.wofu.ecommerce.alibaba.GoodsSKU;
import com.wofu.ecommerce.alibaba.Order;
import com.wofu.ecommerce.alibaba.OrderItem;
import com.wofu.ecommerce.alibaba.Params;
import com.wofu.ecommerce.alibaba.ProductFeatureList;
import com.wofu.ecommerce.alibaba.Version;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.auth.AuthService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;
/***
 * 测试：获取所有商品列表
 * @author Administrator
 *
 */
public class test3 {
	private static String lasttime;
	private static long daymillis=24*60*60*1000L;
	private static String access_token=null;
	private static String type="SALE";
	
	private static String returnFields="offerId,offerStatus,subject,amount,amountOnSale,saledCount,type,gmtCreate,gmtModified,skuArray,productFeatureList";
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
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
		Hashtable<String, String> params2 = new Hashtable<String, String>() ;
		params2.put("client_id", Params.appkey);
	    params2.put("redirect_uri", Params.redirect_uri);
	    params2.put("client_secret", Params.secretKey);
	    params2.put("refresh_token", Params.refresh_token);
	    String returns=AuthService.refreshToken(Params.host, params2);
	    JSONObject access=new JSONObject(returns);
    	Params.token=access.getString("access_token");
    	
    	
		Hashtable<String, String> params = new Hashtable<String, String>() ;
		params.put("type", type) ;
		Connection conn = getConnection();
		lasttime=PublicUtils.getConfig(conn,lasttimeconfvalue,"2013-10-22 00:00:00");
		Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
		System.out.println("开始时间:"+Formatter.format(startdate,"yyyyMMddHHmmss+0800"));
		params.put("returnFields", returnFields);
		params.put("timeStamp",Formatter.format(startdate,Formatter.DATE_TIME_FORMAT)) ;
		params.put("page", "1") ;
		params.put("pageSize", "25") ;
		params.put("returnFields", returnFields);
		String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.getAllOfferList",Params.version,Params.requestmodel,Params.appkey);
		System.out.println(urlPath);
	    params.put("access_token", Params.token);
		
		String responseText = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
		System.out.println("responseText："+responseText);
		
		//返回结果集
		JSONObject jresp=new JSONObject(responseText);
		
		JSONObject jres=(JSONObject) jresp.getJSONObject("result");
		//返回的商品列表资料
		JSONArray jresult=jres.getJSONArray("toReturn");
		System.out.println(jresult);
		
		for(int m=0;m<jresult.length();m++){
			JSONObject j1=jresult.getJSONObject(m);
			Goods gd=new Goods();
			gd.setObjValue(gd, j1);
			//System.out.println("offerid:"+gd.getOfferId()+"  modefytime:"+gd.getGmtModified());
			
			Hashtable<String, String> params1 = new Hashtable<String, String>() ;
			params1.put("offerId", String.valueOf(gd.getOfferId()));
			params1.put("returnFields", "offerId,productFeatureList,offerStatus,subject,amount,amountOnSale,saledCount,type,gmtCreate,gmtModified,skuArray");
			
			String urlPath1=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.get",Params.version,Params.requestmodel,Params.appkey);
			String response = ApiCallService.callApiTest(Params.url, urlPath1, Params.secretKey, params1);
			
			//System.out.println("response:"+response);
			JSONObject res=new JSONObject(response);
			
			JSONArray jarray=res.getJSONObject("result").getJSONArray("toReturn");
			Goods oo=new Goods();
			oo.setObjValue(oo,jarray.getJSONObject(0));
			//ProductFeatureList pl=(ProductFeatureList)oo.getProductFeatureList().getRelationData().get(8);
			
			
			
			
//			Integer huohao=null;
//			for(Iterator o =oo.getProductFeatureList().getRelationData().iterator();o.hasNext();){
//				ProductFeatureList pfl=(ProductFeatureList)o.next();
//				if(pfl.getName().equals("货号")){
//					huohao=Integer.parseInt(pfl.getValue());
//				}
//				
//			}
//			System.out.println("huo:"+huohao);
//			System.out.println(oo.getAmountOnSale());
		}
		
	}
}
