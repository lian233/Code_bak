package com.wofu.ecommerce.qqbuy.oauth;

import java.util.HashMap;

public class TestApi {

	public final static String encoding = "utf-8" ; 
	public static String charset = "utf-8" ;
	public static String format = "xml" ;
	public static String cooperatorId = "855010773" ;
	public static String host = "http://api.buy.qq.com" ;
	public static String appOAuthID = "700043070" ;
	public static String secretOAuthKey = "pEOO6eUeNeU926qK" ;
	public static String accessToken = "7faff45d7bd43cae61c72f3101c0572b" ;
	public static long uin = 855010773L ;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		test2() ;
		//test1() ;
		
	}
	public static void test2()
	{
		String uri = "/item/getSKUListByTime.xhtml" ;
		String subAccountId = "3672243307762685291" ;
		String startTime = "2012-08-01 00:00:00" ;
		String endTime = "2012-08-15 00:00:00" ;
		
		PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, uin);
		sdk.setCharset(charset) ;
		HashMap<String, Object> params = sdk.getParams(uri);
		params.put("charset", "gbk") ;
		params.put("format", format) ;
		params.put("cooperatorId", cooperatorId) ;
		params.put("subAccountId", subAccountId) ;
		params.put("startTime", startTime) ;
		params.put("endTime", endTime) ;
		params.put("pageIndex", "1") ;
		params.put("pageSize", "20") ;
		try {
			String responseText = sdk.invoke();
			System.out.println("responseText="+responseText) ;
			
		} catch (OpenApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void test1()
	{
		PaiPaiOpenApiOauth sdk = null;
		sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, uin);
		sdk.setCharset(charset);
		HashMap<String, Object> params = sdk.getParams("/user/getCooperatorBaseInfo.xhtml");
		params.put("charset", charset) ;
		params.put("format", format) ;
		params.put("cooperatorId", cooperatorId) ;
		try {
			String responseText = sdk.invoke();
			System.out.println("responseText="+responseText) ;
			
		} catch (OpenApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
