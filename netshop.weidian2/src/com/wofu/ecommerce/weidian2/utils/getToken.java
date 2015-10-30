//package com.wofu.ecommerce.weidian2.utils;
//
//import java.io.IOException;
//
//import org.apache.http.client.ClientProtocolException;
//
//import com.wofu.common.json.JSONException;
//import com.wofu.common.json.JSONObject;
//import com.wofu.common.tools.util.JException;
//import com.wofu.ecommerce.weidian2.Params;
//
//public class getToken
//{
//	/**自用型应用获取Token
//	 * @throws JException **/
//	public String getToken_zy() throws ClientProtocolException, IOException, JException
//	{
//		String output = Params.url + "token?grant_type=client_credential" +
//						"&appkey="+Params.app_key+
//						"&secret="+Params.app_Secret;
//		String result = Utils.sendbypost(output);
//		/*{	
//			“status”:{”status_code”:0,”status_reason”:””},
//			“result”: {“access_token”:”ACCESS_TOKEN”,”expires_in”:7200}
//			}*/
//		JSONObject rsp_Object = null;
//		try
//		{
//			rsp_Object = new JSONObject(result);
//			return rsp_Object.getJSONObject("result").getString("access_token");
//		}catch(JSONException e)
//		{
//			try
//			{
//				return rsp_Object.getJSONObject("status").getString("status_code") + "   " + rsp_Object.getJSONObject("status").getString("status_reason");
//			} catch (JSONException e1)
//			{
//				return "fail to get access token";
//			}
//		}
//	}
//	
//	/**服务型应用获取Token**/
//	public String getToken_yy() throws ClientProtocolException, IOException, JException, JSONException
//	{
//		String output = Params.url + "token?grant_type=client_credential" +
//						"&appkey="+Params.app_key+
//						"&secret="+Params.app_Secret;
//		String result = Utils.sendbypost(output);
//		/*{	
//			“status”:{”status_code”:0,”status_reason”:””},
//			“result”: {“access_token”:”ACCESS_TOKEN”,”expires_in”:7200}
//			}*/
//		JSONObject rsp_Object = new JSONObject(result);
//		return rsp_Object.getJSONObject("result").getString("access_token");
//	}
//}
