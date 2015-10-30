package com.wofu.ecommerce.taobao.test;

import java.util.HashMap;
import java.util.Map;

import com.taobao.api.internal.util.WebUtils;

public class test3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		Map<String, String> param = new HashMap<String, String>();
		param.put("grant_type", "authorization_code");
		param.put("code", "lksRS872cE6uvf2YOQvDge2B326672");
		param.put("client_id", "21520535");
		param.put("client_secret", "766bce17fd8ac852ea02a740277f1289");
		param.put("redirect_uri", "http://122.225.94.174:8002/login.html");
		param.put("view", "web");
		param.put("state", "code");

		String responseJson=WebUtils.doPost("https://oauth.taobao.com/token", param, 3000, 3000);

		System.out.println(responseJson);
	}

}
