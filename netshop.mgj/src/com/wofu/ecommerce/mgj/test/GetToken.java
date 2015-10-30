package com.wofu.ecommerce.mgj.test;

public class GetToken {
	private static String app_key="7de54e9adf4679339a1512db969a4097";
	private static String app_secret="789b7d0b4dbcd696ee31d1f38c068392";
	private static String getCode_url="https://www.mogujie.com/openapi/api_v1_oauth/index";
	private static String redirect_url="http://121.196.132.134:30002/login.html";
	private static String getAccess_url="https://www.mogujie.com/openapi/api_v1_accesstoken/index";
	public static void main(String[] args){
		System.out.println("hello");
		System.out.println(getCode());
		System.out.println(getToken("aec468d2e6ff9f2f68b9a37f2e4e617b"));
	}
	
	//第一步取得code
	private static String getCode(){
		String url = getCode_url+"?app_key="+app_key+"&redirect_uri="+redirect_url+"&response_type=code";
		return "请输入以下地址进行授权:"+url;//41c5227c5919a1ca9e9cd863c8d7b4a3
	}
	
	
	//第二步：用code取得accesstoken
	private static String getToken(String code){
		return getAccess_url+"?app_key="+app_key+"&app_secret="+app_secret+"&grant_type=authorization_code&code="+code+"&redirect_uri="+redirect_url;
	}
}
