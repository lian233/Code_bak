package com.wofu.ecommerce.ylzx.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.HttpClient;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.wofu.common.tools.config.Resource;
import com.wofu.common.tools.util.log.Log;

public class Utils {
	private static  HashMap<String,String> requestAuthParams = new HashMap<String,String>();
	static {
		requestAuthParams.put(Content.OAUTH_TOKEN, null);
		requestAuthParams.put(Content.OAUTH_CONSUMER_KEY, null);
		requestAuthParams.put(Content.OAUTH_SIGNATURE_METHOD, null);
		requestAuthParams.put(Content.OAUTH_TIMESTAMP, null);
		requestAuthParams.put(Content.OAUTH_NONCE, null);
		requestAuthParams.put(Content.OAUTH_VERSION, null);
	}
	/**
	 * String result = Utils.sendByPost(Content.getRealToken_url,
				map,"post",oauth_consumer_secert,token);
	 * @param appParamMap
	 * @param secretKey
	 * @param urlStr
	 * @return
	 */
	
	
	
	//String urlStr,Map<String, String> appParamMap,String method, String secretKey, String token
	public static String sendRequest(String url, Map params, String method, String secretKey, String token)
	throws Exception
{
	String result="";
	try{
		
		org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
		httpclient.getParams().setIntParameter("http.socket.timeout",30000);
		PostMethod postmethod = new PostMethod(url);
		postmethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,30000); 
		String sign  = getSign(params,method,url,secretKey,token);
		params.put("oauth_signature",sign);
		for (Iterator iterator = params.keySet().iterator(); iterator.hasNext(); )
		{
			String paramname = (String)iterator.next();
			String paramvalue = (String)params.get(paramname);
			postmethod.addParameter(paramname, paramvalue);
		}
		
		postmethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		int i = httpclient.executeMethod(postmethod);
		//if (i == 200)
		//{
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(postmethod.getResponseBodyAsStream(), "UTF-8"));
			for (String line = null; (line = bufferedreader.readLine()) != null;)
				result = result.concat(line);

		//}
		
		result = URLDecoder.decode(result, "UTF-8");
		
	}catch(Exception ex){
		ex.printStackTrace();
	}
	return result;
	
}
	
	public static String sendByPost(String urlStr,Map<String, String> appParamMap,String method, String secretKey, String token) {
		BufferedReader reader=null;
		InputStream inputStream=null;
		HttpClient httpClient=null;
		try {
			httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(urlStr);
			String sign  = getSign(appParamMap,method,urlStr,secretKey,token);
			appParamMap.put("oauth_signature",sign);
			
			Iterator<String> iterator = appParamMap.keySet().iterator();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			while (iterator.hasNext()) {
				String key = iterator.next();
				//Log.info(key+" "+appParamMap.get(key));
				params.add(new BasicNameValuePair(key, appParamMap.get(key)));
			}

			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(uefEntity);

			HttpResponse response = httpClient.execute(httpPost);
			
			HttpEntity httpEntity = response.getEntity();
			inputStream = httpEntity.getContent();
			//获取返回的数据信息
			StringBuffer postResult = new StringBuffer();
			String readLine = null ;
			reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((readLine = reader.readLine()) != null) {
				postResult.append(readLine);
			}

			httpClient.getConnectionManager().shutdown();
			return postResult.toString();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
				}
				httpClient.getConnectionManager().shutdown();
			}
			return null;
	}
	
	public static String sendByGet(String url,Map<String,String> map,String method,String secretKey,String token) throws Exception{
		HttpClient client = new DefaultHttpClient();
		StringBuilder sb = new StringBuilder(url).append("?");
		String sign  = getSign(map,method,url,secretKey,token);
		map.put("oauth_signature",sign);
		for(Iterator it = map.keySet().iterator();it.hasNext();){
			String name = (String)it.next();
			String value = (String)map.get(name);
			//Log.info(name+": "+value);
			sb.append(name).append("=").append(URLEncoder.encode(value,"utf-8")).append("&");
		}
		//Log.info("url: "+sb.toString().substring(0,sb.length()-1));
		HttpGet get = new HttpGet(sb.toString().substring(0,sb.length()-1));
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity, "utf-8");
		return result;
	}
	/**
	 * 生成签名
	 * 一：先对参数进行hmac加密
	 * 二：再对参数进行Base64Encode()加密 
	 * 三：在这些操作前都要对参数进行urlencode处理
	 * @return
	 * @throws Exception
	 * getSign(map,method,url,secretKey,token);
	 */
	
	private synchronized static String getSign(Map<String,String> map ,String requestMethod,String url,String secretKey,String access_token) throws Exception{
		String baseString = getBaseString(map,requestMethod,url);
		//Log.info("baseString: "+baseString);
		SecretKey key = null;
			if(key==null){
				String keyString=null;
				if(null==access_token)
					keyString = secretKey+"&";
				else 
					keyString = secretKey +"&"+access_token;
				//Log.info("keyString: "+keyString);
				key = new SecretKeySpec(keyString.getBytes("utf-8"),Content.MAC_NAME);
			}
		Mac mac = Mac.getInstance(Content.MAC_NAME);
		mac.init(key);
		String result = Base64.encode(mac.doFinal(baseString.getBytes(Content.ENCODE)));
		//Log.info("sign: "+result);
		return result;
		
		
		
	}
	//拼装要签名的字符串
	private static String getBaseString(Map<String,String> map ,String requestMethod,String url) throws Exception{
	StringBuilder sb = new StringBuilder(requestMethod+"&"+URLEncoder.encode(url,Content.ENCODE));
	TreeMap<String,String> treemap  = new TreeMap<String,String>();
	treemap.putAll(map);
	boolean first=true;
	for(Entry<String,String> e:treemap.entrySet()){
		String name = e.getKey();
		String value = e.getValue();
		if(requestAuthParams.containsKey(name)){
			if(first){
				sb.append("&").append(name).append("%3D").append(URLEncoder.encode(value,Content.ENCODE));
				first=false;
			}else
			sb.append("%26").append(name).append("%3D").append(URLEncoder.encode(value,Content.ENCODE));
		}
	}
	//Log.info("test: "+sb.toString());
	return sb.toString();
	}
	


}
