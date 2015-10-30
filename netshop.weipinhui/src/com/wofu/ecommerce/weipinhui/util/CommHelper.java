package com.wofu.ecommerce.weipinhui.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weipinhui.Params;

/**
 *帮助类
 */
public class CommHelper {
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	//api接口参数
	private static String apiurl = Params.apiurl;
	private static String appSecret = Params.appSecret;	//用于hmac-md5加密
	private static String appkey = Params.appkey;
	private static String accessToken = Params.accessToken;
	/**
	 * 制作签名
	 * @param map 系统级参数
	 * @param jsonParam 应用级参数
	 * @param appSecret 密钥
	 * @return 签名
	 */
	private static String madeSign(Map<String,String> map,String jsonParam,String appSecret){
		try
		{
			String QueryString = getQueryString(map).replaceAll("\\=|\\&", "");
			QueryString += jsonParam;
			return HmacUtils.byte2hex(HmacUtils.encryptHMAC(QueryString, appSecret));
		}
		catch(Exception e)
		{ System.out.println("err:" + e.getMessage()); }
		return "";
	}
	
	/**
	 * 获取参数字符串
	 * @param params 系统级参数
	 * @return 参数字符串
	 */
	private static String getQueryString(Map<String, String> params)  {
		String query_string = "";
		try
		{
			String[] key_arr = params.keySet().toArray(new String[params.keySet().size()]);
			Arrays.sort(key_arr);
			for  (String key : key_arr) {   
			    String value = params.get(key);
			    if(!value.equals(""))
			    {
			    	query_string += (query_string.length() <= 0 ? "" : "&") + key + "=" + URLEncoder.encode(value,"UTF-8");
			    }
			}
		}
		catch(Exception e)
		{ System.out.println(e.getMessage()); }
		return query_string;
	}
	
	/**
	 * 发送请求
	 * @param serviceName 服务名
	 * @param methodName 方法名
	 * @param methodParam 应用级参数
	 * @return 请求返回值
	 */
	public static String doRequest(String serviceName,String methodName,String methodParam)
	{
		//系统级参数
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("version", "1.0.0");
		map.put("format", "JSON");
		map.put("appKey", appkey);
		map.put("service", serviceName);
		map.put("method", methodName);
		map.put("timestamp", Long.toString(System.currentTimeMillis()/1000));
		if(!accessToken.equals(""))
			map.put("accessToken", accessToken);
		else
			Log.error("发送请求时错误", "accessToken无效!");
		//进行签名
		String sign = madeSign(map,methodParam,appSecret);
		//System.out.println("签名结果值："+sign);
		map.put("sign", sign);
		try {
			//进行HTTP Post提交
			HttpClient client = new DefaultHttpClient();  //唯品会提供的代码示例用到的,这里可以换一个不用它的: HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(apiurl + "?" + getQueryString(map));
			Log.info("请求地址:" + post.getURI().toString());
			//新版本调用方法,这里不适合:  post.setEntity(new StringEntity(methodParam,ContentType.create("application/json", "UTF-8")));	//ContentType.create("application/xml", "UTF-8")
			StringEntity strentity = new StringEntity(methodParam,HTTP.UTF_8);	//构造的时候要加上HTTP.UTF_8,解决中文乱码问题
			strentity.setContentType("application/json");
			strentity.setContentEncoding("UTF-8");
			post.setEntity(strentity);
			//Log.info(EntityUtils.toString(strentity,"utf8"));
			Log.info("请求参数:" + InputStream2String(post.getEntity().getContent()));
			HttpResponse res = client.execute(post);
			HttpEntity entity = res.getEntity();
			//返回结果
			String result = InputStream2String(entity.getContent());
			Log.info("返回结果:" + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * InputStream 转 字符串
	 * @param is
	 * @return
	 */
	public static String InputStream2String(InputStream is)  {  
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf8"));		//源字符串为utf8
			String temp = null;
			while ((temp = br.readLine()) != null) {
				result += temp + "\n";
			}
			result = new String(result.getBytes(),"gbk");	//输出为gbk
		}catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}
}
