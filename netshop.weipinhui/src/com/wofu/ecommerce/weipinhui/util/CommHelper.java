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
 *������
 */
public class CommHelper {
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	//api�ӿڲ���
	private static String apiurl = Params.apiurl;
	private static String appSecret = Params.appSecret;	//����hmac-md5����
	private static String appkey = Params.appkey;
	private static String accessToken = Params.accessToken;
	/**
	 * ����ǩ��
	 * @param map ϵͳ������
	 * @param jsonParam Ӧ�ü�����
	 * @param appSecret ��Կ
	 * @return ǩ��
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
	 * ��ȡ�����ַ���
	 * @param params ϵͳ������
	 * @return �����ַ���
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
	 * ��������
	 * @param serviceName ������
	 * @param methodName ������
	 * @param methodParam Ӧ�ü�����
	 * @return ���󷵻�ֵ
	 */
	public static String doRequest(String serviceName,String methodName,String methodParam)
	{
		//ϵͳ������
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
			Log.error("��������ʱ����", "accessToken��Ч!");
		//����ǩ��
		String sign = madeSign(map,methodParam,appSecret);
		//System.out.println("ǩ�����ֵ��"+sign);
		map.put("sign", sign);
		try {
			//����HTTP Post�ύ
			HttpClient client = new DefaultHttpClient();  //ΨƷ���ṩ�Ĵ���ʾ���õ���,������Ի�һ����������: HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(apiurl + "?" + getQueryString(map));
			Log.info("�����ַ:" + post.getURI().toString());
			//�°汾���÷���,���ﲻ�ʺ�:  post.setEntity(new StringEntity(methodParam,ContentType.create("application/json", "UTF-8")));	//ContentType.create("application/xml", "UTF-8")
			StringEntity strentity = new StringEntity(methodParam,HTTP.UTF_8);	//�����ʱ��Ҫ����HTTP.UTF_8,���������������
			strentity.setContentType("application/json");
			strentity.setContentEncoding("UTF-8");
			post.setEntity(strentity);
			//Log.info(EntityUtils.toString(strentity,"utf8"));
			Log.info("�������:" + InputStream2String(post.getEntity().getContent()));
			HttpResponse res = client.execute(post);
			HttpEntity entity = res.getEntity();
			//���ؽ��
			String result = InputStream2String(entity.getContent());
			Log.info("���ؽ��:" + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * InputStream ת �ַ���
	 * @param is
	 * @return
	 */
	public static String InputStream2String(InputStream is)  {  
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf8"));		//Դ�ַ���Ϊutf8
			String temp = null;
			while ((temp = br.readLine()) != null) {
				result += temp + "\n";
			}
			result = new String(result.getBytes(),"gbk");	//���Ϊgbk
		}catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}
}
