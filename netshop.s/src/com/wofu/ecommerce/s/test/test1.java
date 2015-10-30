package com.wofu.ecommerce.s.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.ecommerce.s.utils.Md5Util;
import com.wofu.ecommerce.s.utils.Utils;

import java.net.URLEncoder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.HttpClient;

public class test1 
{
	public static String url="http://gxtest.s.cn/api/fx";
	public static String encoding="UTF-8";

	public static String appKey = "gytest";
	public static String app_secret = "865";
	public static void main(String args[])
	{
		try {
			getOrders();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void getOrders() throws JSONException
	{
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("app_key", "gytest");
		paramMap.put("appsecret", "865");
		String app_key="gytest";
		String app_secret="865";
		String format="json";
		String method="scn.servertime.get";
		String ver="v2.0";
		//生成签名和
		/* 生成data*/
		JSONObject data=new JSONObject();
		data.put("VendorSkuId","121552");
		data.put("Qty","3");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
		String postdata=app_key
					+"app_key"+getUTF8String(app_secret)
					+"data"+getUTF8String(data.toString())
					+"format"+getUTF8String(format)
					+"method"+getUTF8String(method)
					+"timestamp"+df.format(new Date())
					+ver;
		System.out.println(postdata);
		String postdata_finished=MD5Util.getMD5Code(postdata.getBytes()); //数据变成MD5
		System.out.println(postdata_finished);
		System.out.println(postdata_finished=postdata_finished.toUpperCase()); //转变成了大写MD5
		Map<String, String> asd=new HashMap<String, String>();
		//asd.put("app_key", getUTF8XMLString(app_secret));
		asd.put("data", getUTF8String(data.toString()));
		asd.put("format", getUTF8String(format));
		asd.put("method", getUTF8String(method));
		asd.put("timestamp", getUTF8String(df.format(new Date())));
		asd.put("ver", ver);
		String result=Utils.sendByPost(url, postdata_finished);
		System.out.println(result);
	}
	
	public static String sendByPost(Map<String, String> appParamMap, String secretKey, String urlStr ) 
	{
		BufferedReader reader=null;
		InputStream inputStream=null;
		HttpClient httpClient=null;
		try {
			httpClient = new DefaultHttpClient();
			
			HttpPost httpPost = new HttpPost(urlStr);

			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			if (appParamMap != null) {
				treeMap.putAll(appParamMap);
			}

			String sign = Md5Util.md5Signature(treeMap, secretKey);   //MD5签名例子
			System.out.println(sign);
			treeMap.put("sign", sign);
			Iterator<String> iterator = treeMap.keySet().iterator();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			while (iterator.hasNext()) {
				String key = iterator.next();
				params.add(new BasicNameValuePair(key, treeMap.get(key)));
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
	
	/**
	 * GetString of utf-8
	 * 
	 * @return XML-Formed string
	 */
	public static String getUTF8String(String xml) {
		// A StringBuffer Object
		StringBuffer sb = new StringBuffer();
		sb.append(xml);
		String xmString = "";
		String xmlUTF8 = "";
		try {
			xmString = new String(sb.toString().getBytes("UTF-8"));
			xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
			//System.out.println("utf-8 编码：" + xmlUTF8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return to String Formed
		return xmlUTF8;
	}  	
	
}
