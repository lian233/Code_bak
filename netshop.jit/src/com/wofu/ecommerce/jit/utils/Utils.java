package com.wofu.ecommerce.jit.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;

import com.wofu.common.tools.util.log.Log;

public class Utils {
	
	public static String sendByPost(Map<String, String> appParamMap, String request, String urlStr,
			String secretKey ) throws Exception{
		BufferedReader reader=null;
		InputStream inputStream=null;
		HttpClient httpClient=null;
		try {
			httpClient = new DefaultHttpClient();
			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			if (appParamMap != null) {
				treeMap.putAll(appParamMap);
			}
			String sign = Md5Util.createRequestSign(treeMap, request,secretKey);
			treeMap.put("sign", sign);
			Iterator<String> iterator = treeMap.keySet().iterator();
			urlStr+="?";
			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = treeMap.get(key);
				urlStr+=key+"="+URLEncoder.encode(value,"utf-8")+"&";
			}
			//System.out.println(urlStr.substring(0,urlStr.length()-1));
			HttpPost httpPost = new HttpPost(urlStr.substring(0,urlStr.length()-1));
			httpPost.setHeader("Content-Type", "application/json;utf-8");
			StringEntity entity = new StringEntity(request,"UTF-8");
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity httpEntity = response.getEntity();
			inputStream = httpEntity.getContent();
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


}
