package com.wofu.ecommerce.weidian.utils;
import java.io.IOException;
import java.net.URLEncoder;

import java.security.MessageDigest;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.wofu.common.tools.util.JException;
public class Utils
{
	public static String sendbyget(String params) throws ClientProtocolException, IOException, JException
	{
		//System.out.println(params);
		HttpGet httpGet=new HttpGet(params);
		HttpResponse httpResponse=new DefaultHttpClient().execute(httpGet);
		String result=EntityUtils.toString(httpResponse.getEntity());
		//System.out.println(result);
		return result;
	}
	
	public static String md5(String txt)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(txt.getBytes("UTF-8")); // ������Ҫ�������Java���ַ�����unicode���룬����Դ���ļ��ı���Ӱ�죻��PHP�ı����Ǻ�Դ���ļ��ı���һ�£���Դ�����Ӱ�졣
			StringBuffer buf = new StringBuffer();
			for (byte b : md.digest())
			{
				buf.append(String.format("%02x", b & 0xff));
			}
			return buf.toString();
		} catch (Exception e)
		{
			e.printStackTrace();

			return null;
		}
	}
}
