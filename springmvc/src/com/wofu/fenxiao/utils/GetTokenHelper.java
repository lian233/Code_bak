package com.wofu.fenxiao.utils;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.wofu.common.tools.util.log.Log;
import common.Logger;
import net.sf.json.JSONObject;
public class GetTokenHelper {
	private final static String getAccess_url ="https://www.mogujie.com/openapi/api_v1_accesstoken/index";
	private final static String redirect_url ="http://120.26.193.249:30002/login.html";
	private static Logger  logger= Logger.getLogger(GetTokenHelper.class);
	//获取淘宝token
	public static String[] getTaobaoToken(String appkey,String appsecret,String link) throws Exception{
		String[] tokenInfo= new String[2];
		String code = "";
		Pattern pattern = Pattern.compile("code=([a-zA-Z0-9]{1,})&");
		Matcher matcher = pattern.matcher(link);
		if(matcher.find()){
			code = matcher.group(1);
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("grant_type", "authorization_code");
		param.put("code", code);
		param.put("client_id", appkey);
		param.put("client_secret", appsecret);
		param.put("redirect_uri", redirect_url);
		param.put("view", "web");
		param.put("state", "code");
		String result = HttpUtil.sendRequest("https://oauth.taobao.com/token", param, null);
		System.out.println("result: "+result);
		JSONObject obj = JSONObject.fromObject(result);
		if(obj.containsKey("error")) throw new Exception(obj.getString("error_description"));
		tokenInfo[0]=obj.getString("access_token");
		tokenInfo[1]=obj.getString("taobao_user_id");
		return tokenInfo;
	}
	
	//获取蘑菇街token
	// 获取code链接：https://www.mogujie.com/openapi/api_v1_oauth/index?app_key=7de54e9adf4679339a1512db969a4097&redirect_uri=http://121.196.132.134:30002/login.html&response_type=code
	public static String getMogujieToken(String appkey, String app_secret,
			String getTokenLink) throws Exception {
		String code = "";
		Pattern pattern = Pattern.compile("code=([a-zA-Z0-9]{1,})&");
		Matcher matcher = pattern.matcher(getTokenLink);
		if(matcher.find()){
			code = matcher.group(1);
		}
		HashMap<String,String> param = new HashMap<String,String>();
		param.put("app_key", appkey);
		param.put("app_secret", app_secret);
		param.put("grant_type", "authorization_code");
		param.put("code", code);
		param.put("redirect_uri", redirect_url);
		String result = HttpUtil.sendByGet("https://www.mogujie.com/openapi/api_v1_accesstoken/index",param);
		JSONObject obj = JSONObject.fromObject(result);
		JSONObject status = obj.getJSONObject("status");
		if(10001!=status.getInt("code")) throw new Exception(status.getString("msg"));
		return obj.getJSONObject("result").getString("access_token");
		
		
	}
	//获取美丽说token  refreshtoken
	//请求code链接：http://oauth.open.meilishuo.com/authorize?response_type=code&client_id=MJ273316892165&redirect_uri=http://121.196.132.134:30002/login.html&state=YOUR_CUSTOM_CODE
	//http://121.196.132.134:30002/login.html?code=d4fdf84eb326cbe333b091a8
	public static String[] getMeiLiShuoToken(String appkey, String app_secret,
			String getTokenLink) throws Exception {
		String[] tokenInfo= new String[2];
		String code = "";
		Pattern pattern = Pattern.compile("code=(MOP\\%3A[a-zA-Z0-9]{1,})");
		Matcher matcher = pattern.matcher(getTokenLink);
		if(matcher.find()){
			code = URLDecoder.decode(matcher.group(1),"gbk");
		}
		logger.info("code: "+code);
		Map<String, String> param = new HashMap<String, String>();
		//param.put("grant_type", "refresh_token");
		param.put("grant_type", "authorization_code");
		param.put("client_id", appkey);
		param.put("client_secret", app_secret);
		param.put("code", code);
		param.put("redirect_uri", redirect_url);
		//param.put("state", state);//http://oauth.open.meilishuo.com/authorize/token
		String result = HttpUtil.sendRequest("http://oauth.open.meilishuo.com/authorize/token", param, null);
		logger.info("result: "+result);
		JSONObject obj = JSONObject.fromObject(result);
		if(obj.getInt("error_code")!=0) throw new Exception(obj.getString("message"));
		tokenInfo[0]=obj.getJSONObject("data").getString("access_token");
		tokenInfo[1]=obj.getJSONObject("data").getString("refresh_token");
		return tokenInfo;
	}

	// 获取阿里巴巴token
	//http://121.196.132.134:30002/login.html?code=1bae4c6f-b5cf-47d9-8053-e525e2338961#userconsent#
	public static String[] getAlibabaToken(String appkey, String app_secret,
			String getTokenLink)  throws Exception{
		String[] tokenInfo= new String[2];
		String code = "";
		Pattern pattern = Pattern.compile("code=([a-zA-Z-0-9^-])#");
		Matcher match = pattern.matcher(getTokenLink);
		if(match.find()){
			code = match.group(1);
		}else{
			throw new Exception("链接不包含合法的code");
		}
		
		return tokenInfo;
		
		
	}
	//获取京东token
	public static String[] getJingDongToken(String appkey, String app_secret,
			String getTokenLink) throws Exception{
		String[] tokenInfo= new String[2];
		String code = "";
		Pattern pattern = Pattern.compile("code=([a-zA-Z-0-9^-]{1,})&");
		Matcher match = pattern.matcher(getTokenLink);
		if(match.find()){
			code = match.group(1);
		}else{
			throw new Exception("授权链接无效,无效code");
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("grant_type", "authorization_code");
		param.put("client_id", appkey);
		param.put("client_secret", app_secret);
		param.put("code", code);
		param.put("redirect_uri", redirect_url);
		//param.put("state", YOUR_CUSTOM_CODE);//http://oauth.open.meilishuo.com/authorize/token
		String result = HttpUtil.sendRequest("https://auth.360buy.com/oauth/token", param, null,"gbk");
		Log.info("result: "+result);
		JSONObject obj = JSONObject.fromObject(result);
		if(!obj.containsKey("error")) throw new Exception(obj.getString("error_description"));
		tokenInfo[0] = obj.getString("access_token");
		return tokenInfo;
	}
}
