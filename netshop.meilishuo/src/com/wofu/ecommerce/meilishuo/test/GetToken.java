package com.wofu.ecommerce.meilishuo.test;
//package com.wofu.ecommerce.meilishuo2.test;
//
//import com.wofu.business.util.PublicUtils;
//import com.wofu.common.json.JSONObject;
//import com.wofu.common.tools.util.Formatter;
//import com.wofu.common.tools.util.log.Log;
//import com.wofu.ecommerce.meilishuo2.util.Utils;
//
//import java.sql.Connection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//public class GetToken 
//{
//	private static String app_key = "";		//app key
//	private static String app_secret = "";	//app_secret
//	private static String code = "";		//授权码
//	private static String tokenurl = "http://oauth.open.meilishuo.com/authorize/token";
//	private static String ConfigKeyName = "美丽说Token信息";
//	private static Connection conn;
//	
//	public GetToken(String appKey, String appSecret, String Code,Connection connection)
//	{
//		app_key = appKey;
//		app_secret = appSecret;
//		code = Code;
//		conn = connection;
//	}
//	
//	//是否过期
//	@SuppressWarnings("deprecation")
//	public static boolean judgementTime(JSONObject TokenInfo)
//	{
//		if(TokenInfo != null)
//		{
//			try
//			{
//				Date TokenTime = new Date(Formatter.format(TokenInfo.getString("TokenTime"), Formatter.DATE_TIME_FORMAT));
//				Date expires_in = new Date(Formatter.format(TokenInfo.getString("expires_in"), Formatter.DATE_TIME_FORMAT));
//				Date refresh_token = new Date(Formatter.format(TokenInfo.getString("refresh_token"), Formatter.DATE_TIME_FORMAT));
//				Date NowTime = new Date(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
//				if((TokenTime.getTime() + expires_in.getTime()) <= NowTime.getTime()  &&
//					(TokenTime.getTime() + refresh_token.getTime()) <= NowTime.getTime())
//				{
//					return false;
//				}
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//		return true;
//	}
//	
//	//延长token
//	@SuppressWarnings("static-access")
//	public static JSONObject RefToken()
//	{
//		String paramsStr, result = "";
//		JSONObject TokenJson = GetTokenInfo();
//		JSONObject RefTokenJson = null;
//		if(TokenJson != null)
//		{
//			try
//			{
//				String refresh_token = TokenJson.get("refresh_token").toString();
//				Map<String, Object> paramslist = new HashMap<String, Object>();
//				paramslist.put("client_id", app_key);
//				paramslist.put("client_secret", app_secret);
//				paramslist.put("grant_type", "refresh_token");
//				paramslist.put("refresh_token", refresh_token);
//				paramsStr = Utils.MakeParamsString(paramslist);
//				result = Utils.sendByPost(tokenurl,paramsStr);
//				Log.info("更新token返回结果：", result);
//				RefTokenJson = new JSONObject(result);
//				//判断是否有错误,有错误则返回null
//				String Errcode = RefTokenJson.get("error_code").toString();
//				if(Errcode.equals("0"))
//				{//添加时间戳,并且同步Token到数据库
//					RefTokenJson.put("TokenTime", new Formatter().format(new Date(), Formatter.DATE_TIME_FORMAT).toString());
//					PublicUtils.setConfig(conn, ConfigKeyName, RefTokenJson.toString());
//				}
//				else
//				{
//					Log.error("获取美丽说Token失败!", "返回的Json字符串:" + result);
//					return null;
//				}
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//				Log.error("获取美丽说Token失败!", "错误信息:" + e.getMessage());
//			}
//		}
//		return RefTokenJson;
//	}
//	
//	//获取token
//	public static JSONObject GetTokenInfo()
//	{//error_code
//		JSONObject TokenJson = null;
//		
//		//获取检查数据库中是否有值
//		try
//		{
//			String DBTokeninfo = PublicUtils.getConfig(conn,ConfigKeyName,"");
//			if(DBTokeninfo.equals("")||DBTokeninfo.isEmpty())
//			{//数据库中没Token信息,根据授权码获取新的Token信息
//				TokenJson = GetNewToken();
//				if(TokenJson != null)
//				{//写入数据库
//					PublicUtils.setConfig(conn, ConfigKeyName, TokenJson.toString());
//					return TokenJson;
//				}
//			}
//			else
//			{//有内容则返回
//				Log.info("GetTokenInfo返回字符串：",DBTokeninfo);
//				return new JSONObject(DBTokeninfo);
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			Log.error("获取美丽说Token失败!", "错误信息:" + e.getMessage());
//		}
//		return null;
//	}
//	
//	//获取新Token并保存(内部)
//	private static JSONObject GetNewToken()
//	{
//		String paramsStr, result = "";
//		JSONObject jsondata = null;
//		try
//		{
//			Map<String, Object> paramslist = new HashMap<String, Object>();
//			paramslist.put("client_id", app_key);
//			paramslist.put("client_secret", app_secret);
//			paramslist.put("grant_type", "authorization_code");
//			paramslist.put("code", code);
//			paramslist.put("redirect_uri", "urn:ietf:wg:oauth:2.0:oob");
//			paramsStr = Utils.MakeParamsString(paramslist);
//			result = Utils.sendByPost(tokenurl,paramsStr);
//			jsondata = new JSONObject(result);
//			//判断是否有错误,有错误则返回null
//			String Errcode = jsondata.get("error_code").toString();
//			if(Errcode.equals("0"))
//			{
//				jsondata.put("TokenTime", new Formatter().format(new Date(), Formatter.DATE_TIME_FORMAT).toString());
//			}
//			else
//			{
//				Log.error("获取美丽说Token失败!", "返回的Json字符串:" + result);
//				return null;
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			Log.error("获取美丽说Token失败!", "错误信息:" + e.getMessage());
//		}
//		return jsondata;
//	}
//}