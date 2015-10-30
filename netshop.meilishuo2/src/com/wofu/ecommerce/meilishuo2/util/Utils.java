package com.wofu.ecommerce.meilishuo2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.ecommerce.meilishuo2.Params;

public class Utils
{

	public static String sendByPost2(Map<String, String> appParamMap,
			String urlStr)
	{
		BufferedReader reader = null;
		InputStream inputStream = null;
		HttpClient httpClient = null;
		try
		{
			httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(urlStr);

			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			if (appParamMap != null)
			{
				treeMap.putAll(appParamMap);
			}

			// String sign = Md5Util.md5Signature(treeMap, secretKey); //MD5签名例子
			// treeMap.put("sign", sign);
			Iterator<String> iterator = treeMap.keySet().iterator();

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			while (iterator.hasNext())
			{
				String key = iterator.next();
				params.add(new BasicNameValuePair(key, treeMap.get(key)));
			}

			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(params,
					"UTF-8");
			httpPost.setEntity(uefEntity);

			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			inputStream = httpEntity.getContent();
			// 获取返回的数据信息
			StringBuffer postResult = new StringBuffer();
			String readLine = null;
			reader = new BufferedReader(new InputStreamReader(inputStream,
					"UTF-8"));
			while ((readLine = reader.readLine()) != null)
			{
				postResult.append(readLine);
			}

			httpClient.getConnectionManager().shutdown();
			return postResult.toString();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
				if (inputStream != null)
				{
					inputStream.close();
				}
			} catch (IOException e)
			{
			}
			httpClient.getConnectionManager().shutdown();
		}
		return null;
	}

	public static String md5(String txt)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(txt.getBytes("UTF-8")); // 问题主要出在这里，Java的字符串是unicode编码，不受源码文件的编码影响；而PHP的编码是和源码文件的编码一致，受源码编码影响。
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

	public static String http_get(String url)
	{
		String result = null;
		HttpGet request = new HttpGet(url);

		try
		{

			HttpResponse response = new DefaultHttpClient().execute(request);

			if (response.getStatusLine().getStatusCode() == 200)
			{

				result = EntityUtils.toString(response.getEntity());

			}
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		return result;
	}
	
	public static String MakeParamsString(Map<String, Object> map)
	{
		StringBuffer strBuff = new StringBuffer();
        for(Iterator iterator = map.keySet().iterator(); iterator.hasNext();)
        {
        	String paramname = (String)iterator.next();
        	String paramvalue =(String)map.get(paramname);
        	if(strBuff.length() > 0)
        	{
        		strBuff.append("&").append(paramname).append("=").append(paramvalue);
        	}else{
        		strBuff.append(paramname).append("=").append(paramvalue);
        	}
        }
        return strBuff.toString();
	}
	
	/**取商品、取库存用**/
	public static String sendbyget(String url,String app_key,String app_sercert,String method,String session,Date date,String twitter_id) throws ClientProtocolException, IOException, JException
	{
		StringBuffer command_spell=new StringBuffer();
		/**拼接大法好**/
		command_spell.append("http://");
		command_spell.append(url);
		command_spell.append("/router/rest?");
		command_spell.append("app_key=");
		command_spell.append(app_key);
		command_spell.append("&fields=&format=&");
		command_spell.append("method=");
		command_spell.append(method);
		command_spell.append("&");
		command_spell.append("session=");
		command_spell.append(session);
		command_spell.append("&");
		command_spell.append("sign_method=md5");
		command_spell.append("&");
		command_spell.append("timestamp=");
		command_spell.append(URLEncoder.encode(Formatter.format(date, Formatter.DATE_TIME_FORMAT),"UTF-8"));
		command_spell.append("&");
		command_spell.append("v=1.0");
		if(twitter_id!=null || !twitter_id.equals(""))
		{
			command_spell.append("&");
			command_spell.append("twitter_id=");
			command_spell.append(twitter_id);
		}
		command_spell.append("&");
		command_spell.append("sign=");
		String before_sign=null;
		if(twitter_id!=null || !twitter_id.equals(""))
		{
			before_sign=app_sercert + "app_key" + app_key + "fieldsformat" + "method" + method + "session" + session + "sign_method" + "md5" + "timestamp" + Formatter.format(date, Formatter.DATE_TIME_FORMAT) 
            + "twitter_id" + twitter_id + "v1.0" + app_sercert; //这里比单纯的获取商品多了一个twitter_id的列，所以要注意加上去做签名
		}
		else
		{
			before_sign=app_sercert + "app_key" + app_key + "fieldsformat" + "method" + method + "session" + session + "sign_method" + "md5" + "timestamp" + Formatter.format(date, Formatter.DATE_TIME_FORMAT) 
            + "v1.0" + app_sercert; //这里比单纯的获取商品多了一个twitter_id的列，所以要注意加上去做签名
		}
		command_spell.append(Utils.md5(before_sign).toUpperCase());
		/**拼接大法好**/
		String params=command_spell.toString();
		HttpGet httpGet=new HttpGet(params);
		HttpResponse httpResponse=new DefaultHttpClient().execute(httpGet);
		String result=EntityUtils.toString(httpResponse.getEntity());
		System.out.println(result);
		return result;
	}
	/**更新库存用**/
	public static String sendbyget(String url,String app_key,String app_sercert,String method,String session,Date date,String twitter_id,String sku_id,String modify_type,String modify_value) throws ClientProtocolException, IOException, JException
	{
		StringBuffer command_spell=new StringBuffer();
		/**拼接大法好**/
		command_spell.append("http://");
		command_spell.append(url);
		command_spell.append("/router/rest?");
		command_spell.append("app_key=");
		command_spell.append(app_key);
		command_spell.append("&fields=&format=&");
		command_spell.append("method=");
		command_spell.append(method);
		command_spell.append("&");
		command_spell.append("session=");
		command_spell.append(session);
		command_spell.append("&");
		command_spell.append("sign_method=md5");
		command_spell.append("&");
		command_spell.append("timestamp=");
		command_spell.append(URLEncoder.encode(Formatter.format(date, Formatter.DATE_TIME_FORMAT),"UTF-8"));
		command_spell.append("&");
		command_spell.append("v=1.0");
		if(twitter_id!=null || !twitter_id.equals(""))
		{
			command_spell.append("&");
			command_spell.append("twitter_id=");
			command_spell.append(twitter_id);
		}
		command_spell.append("&1st=&2rd=&goods_code=&");
		command_spell.append("sku_id=");
		command_spell.append(sku_id);
		command_spell.append("&");
		command_spell.append("modify_type=");
		command_spell.append(modify_type.equals("")?"set":modify_type);
		command_spell.append("&");
		command_spell.append("modify_value=");
		command_spell.append(modify_value.equals("")?"0":modify_value);
		command_spell.append("&");
		command_spell.append("sign=");
		String before_sign=null;
		before_sign=app_sercert + 
					"1st2rd" + 
					"app_key" + app_key + 
					"fieldsformat" +
					"goods_code"   +
					"method" + method + 
					"modify_type" + modify_type +
					"modify_value" + modify_value + 
					"session" + session + 
					"sign_method" + "md5" + 
					"sku_id" + sku_id +
					"timestamp" + Formatter.format(date, Formatter.DATE_TIME_FORMAT) +
					"twitter_id" + twitter_id +
					"v1.0" + 
					app_sercert; //这里比单纯的获取商品多了一个twitter_id的列，所以要注意加上去做签名
		command_spell.append(Utils.md5(before_sign).toUpperCase());
		/**拼接大法好**/
		String params=command_spell.toString();
		//System.out.println(before_sign);
		HttpGet httpGet=new HttpGet(params);
		HttpResponse httpResponse=new DefaultHttpClient().execute(httpGet);
		String result=EntityUtils.toString(httpResponse.getEntity());
		//System.out.println(result);
		return result;		
	}
	/**取订单用**/
	public static String sendbyget(String url,String app_key,String app_sercert,String method,String session,Date date,Date ctime_start,Date ctime_end,String page_size,String page,String status) throws ClientProtocolException, IOException, JException
	{
		StringBuffer command_spell=new StringBuffer();
		/**拼接大法好**/
		command_spell.append("http://");
		command_spell.append(url);
		command_spell.append("/router/rest?");
		command_spell.append("app_key=");
		command_spell.append(app_key);
		command_spell.append("&fields=&format=&");
		command_spell.append("method=");
		command_spell.append(method);
		command_spell.append("&");
		command_spell.append("session=");
		command_spell.append(session);
		command_spell.append("&");
		command_spell.append("sign_method=md5");
		command_spell.append("&");
		command_spell.append("timestamp=");
		command_spell.append(URLEncoder.encode(Formatter.format(date, Formatter.DATE_TIME_FORMAT),"UTF-8"));
		command_spell.append("&");
		command_spell.append("v=1.0");
		command_spell.append("&");
		command_spell.append("page=");
		command_spell.append(page);
		command_spell.append("&");
		command_spell.append("page_size=");
		command_spell.append(page_size);
		command_spell.append("&");
		command_spell.append("status=");
		command_spell.append(status);
		command_spell.append("&");
		command_spell.append("ctime_start=");
		command_spell.append(ctime_start==null?"":URLEncoder.encode(Formatter.format(ctime_start, Formatter.DATE_TIME_FORMAT),"UTF-8"));
		command_spell.append("&");
		command_spell.append("ctime_end=");
		command_spell.append(ctime_end == null?"":URLEncoder.encode(Formatter.format(ctime_end, Formatter.DATE_TIME_FORMAT),"UTF-8"));
		command_spell.append("&uptime_start=&uptime_end=");
		command_spell.append("&");
		command_spell.append("sign=");
		String before_sign=null;
		before_sign=app_sercert + 
					"app_key" + app_key + 
					"ctime_end" + (ctime_end==null?"":Formatter.format(ctime_end, Formatter.DATE_TIME_FORMAT)) +
					"ctime_start" + (ctime_start==null?"":Formatter.format(ctime_start, Formatter.DATE_TIME_FORMAT)) +
					"fieldsformat" +
					"method" + method + 
					"page" + page + 
					"page_size" + page_size + 
					"session" + session + 
					"sign_method" + "md5" +
					"status" + status + 
					"timestamp" + Formatter.format(date, Formatter.DATE_TIME_FORMAT) +
					"uptime_end" +
					"uptime_start"+
					"v1.0" + 
					app_sercert; //这里比单纯的获取商品多了几个列，所以要注意加上去做签名
		//System.out.println(before_sign);
		command_spell.append(Utils.md5(before_sign).toUpperCase());
		//System.out.println(command_spell.toString());
		/**拼接大法好**/
		String params=command_spell.toString();
		HttpGet httpGet=new HttpGet(params);
		HttpResponse httpResponse=new DefaultHttpClient().execute(httpGet);
		String result=EntityUtils.toString(httpResponse.getEntity());
		//System.out.println(result);
		return result;		
	}
	/**发货用**/
	public static String sendbyget(String url,String app_key,String app_sercert,String method,String session,Date date,
			      String order_id,String oid,String express_company,String express_id,String shit) throws ClientProtocolException, IOException, JException
	{
		StringBuffer command_spell=new StringBuffer();
		/**拼接大法好**/
		command_spell.append("http://");
		command_spell.append(url);
		command_spell.append("/router/rest?");
		command_spell.append("app_key=");
		command_spell.append(app_key);
		command_spell.append("&fields=&format=&");
		command_spell.append("method=");
		command_spell.append(method);
		command_spell.append("&");
		command_spell.append("session=");
		command_spell.append(session);
		command_spell.append("&");
		command_spell.append("sign_method=md5");
		command_spell.append("&");
		command_spell.append("timestamp=");
		command_spell.append(URLEncoder.encode(Formatter.format(date, Formatter.DATE_TIME_FORMAT),"UTF-8"));
		command_spell.append("&");
		command_spell.append("v=1.0");
		command_spell.append("&");
		command_spell.append("order_id=");
		command_spell.append(order_id);
		command_spell.append("&");
		command_spell.append("oid=");
		command_spell.append((oid.equals("")||oid==null)?"":oid);
		command_spell.append("&");
		command_spell.append("express_company=");
		command_spell.append(URLEncoder.encode((express_company.equals("")||express_company==null)?"":express_company,"UTF-8"));
		command_spell.append("&");
		command_spell.append("express_id=");
		command_spell.append(URLEncoder.encode((express_id.equals("")||express_id==null)?"":express_id,"UTF-8"));
		command_spell.append("&");
		command_spell.append("sign=");
		String before_sign=null;
		before_sign=app_sercert + 
					"app_key" + app_key + 
					"express_company" + ((express_company.equals("")||express_company==null)?"":express_company) +
					"express_id" + ((express_id.equals("")||express_id==null)?"":express_id) +
					"fieldsformat" +
					"method" + method + 
					"oid" + ((oid.equals("")||oid==null)?"":oid) +
					"order_id" + order_id + 
					"session" + session + 
					"sign_method" + "md5" +
					"timestamp" + Formatter.format(date, Formatter.DATE_TIME_FORMAT) +
					"v1.0" + 
					app_sercert; //这里比单纯的获取商品多了几个列，所以要注意加上去做签名
		//System.out.println(before_sign);
		command_spell.append(Utils.md5(before_sign).toUpperCase());
		//System.out.println(command_spell.toString());
		/**拼接大法好**/
		String params=command_spell.toString();
		HttpGet httpGet=new HttpGet(params);
		HttpResponse httpResponse=new DefaultHttpClient().execute(httpGet);
		String result=EntityUtils.toString(httpResponse.getEntity());
		//System.out.println(result);
		return result;		
	}
	/**取退货列表用**/
	public static String sendbyget(String url,String app_key,String app_sercert,String method,String session,Date date,Date apply_stime,Date apply_etime,String page_size,String page_no,String status,String shit) throws ClientProtocolException, IOException, JException
	{
		StringBuffer command_spell=new StringBuffer();
		/**拼接大法好**/
		command_spell.append("http://");
		command_spell.append(url);
		command_spell.append("/router/rest?");
		command_spell.append("app_key=");
		command_spell.append(app_key);
		command_spell.append("&fields=&format=&");
		command_spell.append("method=");
		command_spell.append(method);
		command_spell.append("&");
		command_spell.append("session=");
		command_spell.append(session);
		command_spell.append("&");
		command_spell.append("sign_method=md5");
		command_spell.append("&");
		command_spell.append("timestamp=");
		command_spell.append(URLEncoder.encode(Formatter.format(date, Formatter.DATE_TIME_FORMAT),"UTF-8"));
		command_spell.append("&");
		command_spell.append("v=1.0");
		command_spell.append("&");
		command_spell.append("page_no=");
		command_spell.append(page_no);
		command_spell.append("&");
		command_spell.append("page_size=");
		command_spell.append(page_size);
		command_spell.append("&");
		command_spell.append("status=");
		command_spell.append(status);
		command_spell.append("&");
		command_spell.append("apply_stime=");
		command_spell.append(apply_stime==null?"":URLEncoder.encode(Formatter.format(apply_stime, Formatter.DATE_TIME_FORMAT),"UTF-8"));
		command_spell.append("&");
		command_spell.append("apply_etime=");
		command_spell.append(apply_etime == null?"":URLEncoder.encode(Formatter.format(apply_etime, Formatter.DATE_TIME_FORMAT),"UTF-8"));
		command_spell.append("&uptime_start=&uptime_end=");
		command_spell.append("&");
		command_spell.append("sign=");
		String before_sign=null;
		before_sign=app_sercert + 
					"app_key" + app_key + 
					"apply_etime" + (apply_etime==null?"":Formatter.format(apply_etime, Formatter.DATE_TIME_FORMAT)) +
					"apply_stime" + (apply_stime==null?"":Formatter.format(apply_stime, Formatter.DATE_TIME_FORMAT)) +
					"fieldsformat" +
					"method" + method + 
					"page_no" + page_no + 
					"page_size" + page_size + 
					"session" + session + 
					"sign_method" + "md5" +
					"status" + status + 
					"timestamp" + Formatter.format(date, Formatter.DATE_TIME_FORMAT) +
					"uptime_end" +
					"uptime_start"+
					"v1.0" + 
					app_sercert; //这里比单纯的获取商品多了几个列，所以要注意加上去做签名
		//System.out.println(before_sign);
		command_spell.append(Utils.md5(before_sign).toUpperCase());
		//System.out.println(command_spell.toString());
		/**拼接大法好**/
		String params=command_spell.toString();
		HttpGet httpGet=new HttpGet(params);
		HttpResponse httpResponse=new DefaultHttpClient().execute(httpGet);
		String result=EntityUtils.toString(httpResponse.getEntity());
		//System.out.println(result);
		return result;		
	}
	
}
