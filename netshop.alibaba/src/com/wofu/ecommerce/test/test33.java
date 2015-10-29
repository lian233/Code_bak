package com.wofu.ecommerce.test;

import java.util.HashMap;
import java.util.Map;

import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;

/***
 * 测试：更具长久令牌获取授权令牌
 * @author Administrator
 *
 */
public class test33 {
	 public static String refreshToken(String host, Map<String, String> params)throws Exception{
	        String urlHead = "https://" + host + "/openapi/";
	        String namespace = "system.oauth2";
	        String name = "getToken";
	        int version = 1;
	        String protocol = "param2";
	        if(params != null){
	            if(params.get("client_id") == null || params.get("client_secret") == null
	                    || params.get("redirect_uri") == null || params.get("refresh_token") == null){
	                System.out.println("params is invalid, lack neccessary key!");
	                return null;
	            }
	            params.put("grant_type", "refresh_token");
	            String appKey = params.get("client_id");
	            String urlPath = CommonUtil.buildInvokeUrlPath(namespace, name, version, protocol, appKey);
	            String result = ApiCallService.callApiTest(urlHead, urlPath, null, params);
	            return result;
	        }
	        return "";
	    }
	public static void main(String[] args) {
      String host = "gw.open.1688.com";//国际交易请用"gw.api.alibaba.com"
	  String client_id = "1008729";
	  String appSecret = "umCA1lRb0Bw";
	  String redirect_uri = "http://163.com";
		
		
      String refreshToken = "631bd380-5ea0-46ae-a36d-cccf8091508f";
      Map<String, String> params = new HashMap<String, String>();
      params.put("client_id", client_id);
      params.put("redirect_uri", redirect_uri);
      params.put("client_secret", appSecret);
      params.put("refresh_token", refreshToken);
     
      try {
    	 String refreshTokenResult = refreshToken(host, params);
         System.out.println("用长时令牌换取授权令牌的返回结果：" + refreshTokenResult);
		 JSONObject access=new JSONObject(refreshTokenResult);
		 System.out.println(access.getString("access_token"));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     
//      JSONObject jsonObject1;
//		try {
//			jsonObject1 = new JSONObject(refreshTokenResult);
//			 System.out.println("accessToken:" + jsonObject1.get("access_token"));
//		     //test call api
//		     String urlPath = "param2/2/system/currentTime/" + client_id;
//		     String urlHead = "http://" + host + "/openapi/";
//		     Map<String, String> param = new HashMap<String, String>();
//		     param.put("access_token", (String)jsonObject1.get("access_token"));
//		     String result = ApiCallService.callApiTest(urlHead, urlPath, appSecret, param);
//		     System.out.println(result);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
