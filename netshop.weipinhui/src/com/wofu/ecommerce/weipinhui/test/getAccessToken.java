package com.wofu.ecommerce.weipinhui.test;
import java.net.URLEncoder;
import java.util.HashMap;

/*import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;*/
import com.wofu.ecommerce.weipinhui.util.ConnUtil;

public class getAccessToken {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * https://auth.vip.com/oauth2/authorize?client_id=2c439d39&response_type=code&redirect_uri=http://127.0.0.1
		 * ªÒ»°codeµÿ÷∑
		 */
		
		String url = "https://auth.vip.com/oauth2/token";

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("grant_type", "authorization_code");
		map.put("client_id", "2c439d39");
		map.put("client_secret", "09A2D7B7011680885F01395EE02294A9");
		map.put("redirect_uri", "http://127.0.0.1/?");
		map.put("request_client_ip", "127.0.0.1");
		
		map.put("code", "ccb0e93aac624989b2aa83234d2c1620");
		
		try {
			String query_string = "";
			String[] tmpstr = map.keySet().toArray(new String[map.keySet().size()]);
			for (String key : tmpstr) {
				String value = map.get(key);
				
			    if(query_string.isEmpty())
			    {
			    	query_string = key + "=" + URLEncoder.encode(value,"UTF-8");
			    }
			    else
			    {
			    	query_string += "&" + key + "=" + URLEncoder.encode(value,"UTF-8");
			    }
			}
			
			System.out.println(url + "?" + query_string);
			
			String responseText = ConnUtil.sendPost(url, query_string);
			
			System.out.println(responseText);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
