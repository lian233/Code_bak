package com.wofu.ecommerce.wqb.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.HttpClient;
public class Utils {
	
	public static String sendByPost(Map<String, String> appParamMap, String secretKey,String method, String urlStr ) {
		BufferedReader reader=null;
		InputStream inputStream=null;
		HttpClient httpClient=null;
		try {
			httpClient = new DefaultHttpClient();
			
			HttpPost httpPost = new HttpPost(urlStr);
			String sign = Md5Util.md5Signature(appParamMap, secretKey,method);
			appParamMap.put("token", sign);
			Iterator<String> iterator = appParamMap.keySet().iterator();
			//上传二进制流
			//MultipartEntity mEntity = new MultipartEntity();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//StringBuilder sbb = new StringBuilder();
			while (iterator.hasNext()) {
				
				String key = iterator.next();
				params.add(new BasicNameValuePair(key, appParamMap.get(key)));
				
				//sbb.append(key).append("=").append(appParamMap.get(key)).append("&");
				
			}
			//sbb.deleteCharAt(sbb.length()-1);
			/**
			 * 设置二进制流类型
			 * //StringBody sb= new StringBody(sbb.toString());
			   //mEntity.addPart("", sb);
			 */
			
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


}
