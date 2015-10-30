package com.wofu.ecommerce.mgj.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.HttpClient;

import com.wofu.common.tools.util.log.Log;

public class Utils {
	
	public static String sendByPost(Map<String, String> appParamMap, String secretKey, String urlStr ) {
		BufferedReader reader=null;
		InputStream inputStream=null;
		HttpClient httpClient=null;
		try {
			httpClient = new DefaultHttpClient();
			
			HttpPost httpPost = new HttpPost(urlStr);

			Iterator<String> iterator = appParamMap.keySet().iterator();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			while (iterator.hasNext()) {
				String key = iterator.next();
				params.add(new BasicNameValuePair(key, appParamMap.get(key)));
				//Log.info(key+" "+appParamMap.get(key));
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
	
	public static String sendByGet(Map params,String url) throws Exception{
		InputStream in=null;
		BufferedReader sr=null;
		String line =null;
		StringBuilder result =new StringBuilder();
		HttpClient client=null;
		try{
			client = new DefaultHttpClient();
			StringBuilder sender = new StringBuilder(url+"?");
			Iterator it = params.keySet().iterator();
			for(;it.hasNext();){
				String name = (String)it.next();
				String value = URLEncoder.encode((String)params.get(name),"utf-8");
				sender.append(name).append("=").append(value).append("&");
			}
			Log.info("s: "+sender.toString());
			HttpGet get = new HttpGet(sender.toString().substring(0,sender.toString().length()-1));
			HttpResponse response = client.execute(get);
			in = response.getEntity().getContent();
			sr = new BufferedReader(new InputStreamReader(in));
			for(line=sr.readLine();line!=null;result.append(line),line=sr.readLine());
			client.getConnectionManager().shutdown();
			return result.toString();
		}catch(Exception e){
			Log.error("发送http请求出错", e.getMessage());
		}finally{
			if(sr!=null) sr.close();
			if(in!=null) in.close();
			client.getConnectionManager().shutdown();
			client=null;
			
		}
		return result.toString();
		
		
	}


}
