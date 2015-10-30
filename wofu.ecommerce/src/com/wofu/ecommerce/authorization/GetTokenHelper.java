package com.wofu.ecommerce.authorization;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.alibaba.Params;
import com.wofu.ecommerce.alibaba.auth.AuthService;

import common.Logger;
public class GetTokenHelper {
	private final static String getAccess_url ="https://www.mogujie.com/openapi/api_v1_accesstoken/index";
	private final static String redirect_url ="http://120.26.193.249:30002/login.html";
	//
	public static JSONObject getTaobaoToken(String appkey,String appsecret,String link) throws Exception{
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
		String result = HttpUtil.sendRequest("https://oauth.taobao.com/token", param, null,"utf-8");
		System.out.println("result: "+result);
		JSONObject obj =  new JSONObject(result);
		return obj;
	}
	
	//鑾峰彇铇戣弴琛梩oken
	// 鑾峰彇code閾炬帴锛歨ttps://www.mogujie.com/openapi/api_v1_oauth/index?app_key=7de54e9adf4679339a1512db969a4097&redirect_uri=http://121.196.132.134:30002/login.html&response_type=code
	public static JSONObject getMogujieToken(String appkey, String app_secret,
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
		JSONObject obj = new JSONObject(result);
		return obj;
		
		
		
	}
	//鑾峰彇缇庝附璇磘oken  refreshtoken
	//璇锋眰code閾炬帴锛歨ttp://oauth.open.meilishuo.com/authorize?response_type=code&client_id=MJ273316892165&redirect_uri=http://121.196.132.134:30002/login.html&state=YOUR_CUSTOM_CODE
	//http://121.196.132.134:30002/login.html?code=d4fdf84eb326cbe333b091a8
	public static JSONObject getMeiLiShuoToken(String appkey, String app_secret,
			String getTokenLink) throws Exception {
		String code = "";
		Pattern pattern = Pattern.compile("code=(MOP\\%3A[a-zA-Z0-9]{1,})");
		Matcher matcher = pattern.matcher(getTokenLink);
		if(matcher.find()){
			code = URLDecoder.decode(matcher.group(1),"gbk");
		}
		Map<String, String> param = new HashMap<String, String>();
		//param.put("grant_type", "refresh_token");
		param.put("grant_type", "authorization_code");
		param.put("client_id", appkey);
		param.put("client_secret", app_secret);
		param.put("code", code);
		param.put("redirect_uri", "http://120.26.193.249:30002/login.html");
		//param.put("state", state);//http://oauth.open.meilishuo.com/authorize/token
		String result = HttpUtil.sendRequest("http://oauth.open.meilishuo.com/authorize/token", param, null,"utf-8");
		System.out.println("返回的数据"+result);
		JSONObject obj = new JSONObject(result);
		if(obj.getInt("error_code")!=0) throw new Exception(obj.getString("message"));
		return obj;
	}

	// 获取阿里巴巴token
	//http://121.196.132.134:30002/login.html?code=1bae4c6f-b5cf-47d9-8053-e525e2338961#userconsent#
	public static JSONObject getAlibabaToken(String appkey, String app_secret,
			String getTokenLink)  throws Exception{
		String code = "";
		Pattern pattern = Pattern.compile("code=([a-zA-Z-0-9^-])#");
		Matcher match = pattern.matcher(getTokenLink);
		if(match.find()){
			code = match.group(1);
		}else{
			throw new Exception("授权链接无效无效code");
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("client_id",appkey);
		param.put("client_secret",app_secret);
		param.put("redirect_uri","www.163.com");
		param.put("code", code);
		String result = AuthService.getToken("gw.open.1688.com",param,true);
		//param.put("state", YOUR_CUSTOM_CODE);//http://oauth.open.meilishuo.com/authorize/token
		JSONObject obj = new JSONObject(result);
		return obj;
		
		
	}
	
	//获取京东授权
	public static JSONObject getJingDongToken(String appkey, String app_secret,
			String getTokenLink)  throws Exception{
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
		param.put("redirect_uri", "http://120.26.193.249:30001/login.html");
		//param.put("state", YOUR_CUSTOM_CODE);//http://oauth.open.meilishuo.com/authorize/token
		String result = HttpUtil.sendRequest("https://auth.360buy.com/oauth/token", param, null,"gbk");
		Log.info("result: "+result);
		JSONObject obj = new JSONObject(result);
		if(obj.getInt("code")!=0) throw new Exception(new String(obj.getString("error_description").getBytes("gbk")));
		return obj;
		
		
	}
}
