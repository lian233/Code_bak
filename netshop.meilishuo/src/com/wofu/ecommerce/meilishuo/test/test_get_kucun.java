package com.wofu.ecommerce.meilishuo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.ecommerce.meilishuo.Params;
import com.wofu.ecommerce.meilishuo.util.GetToken2;
import com.wofu.ecommerce.meilishuo.util.Utils;

public class test_get_kucun 
{
	public static void main(String args[])
	{
		try
		{
			Connection connection=conn();
			//GetToken getToken = new GetToken("MJ3600390330","62a43eba3efc063e4acb17c57de01cf6","MZYWMDM5MDMXMZEXMDK",connection);
			GetToken2 getToken2=new GetToken2(null);
			JSONObject object=new JSONObject(PublicUtils.getConfig(connection, "美丽说Token信息2", ""));
			sendbyget(Params.url,Params.appKey,Params.appsecret,"meilishuo.items.inventory.get",object.optString("access_token"),new Date(),"3563910491");
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (com.wolf.common.tools.util.JException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static String sendByPost(Map<String, String> appParamMap, String secretKey, String urlStr ) {
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

			//String sign = Md5Util.md5Signature(treeMap, secretKey);   //MD5签名例子
			//treeMap.put("sign", sign);
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

	static void post(String url,String params) throws ClientProtocolException, IOException
	{
		HttpPost httpPost=new HttpPost(url);
		httpPost.setEntity(new StringEntity(params));
		HttpResponse httpResponse=new DefaultHttpClient().execute(httpPost);
		String result=EntityUtils.toString(httpResponse.getEntity());
		System.out.println(result);
	}
	
	static void sendbyget(String url,String app_key,String app_sercert,String method,String session,Date date,String twitter_id) throws ClientProtocolException, IOException, JException
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
		command_spell.append("twitter_id=");
		command_spell.append(twitter_id);
		command_spell.append("&");
		command_spell.append("sign=");
		String before_sign=app_sercert + "app_key" + app_key + "fieldsformat" + "method" + method + "session" + session + "sign_method" + "md5" + "timestamp" + Formatter.format(date, Formatter.DATE_TIME_FORMAT) 
		                   + "twitter_id" + twitter_id + "v1.0" + app_sercert; //这里比单纯的获取商品多了一个twitter_id的列，所以要注意加上去做签名
		command_spell.append(Utils.md5(before_sign).toUpperCase());
		/**拼接大法好**/
		String params=command_spell.toString();
		HttpGet httpGet=new HttpGet(params);
		HttpResponse httpResponse=new DefaultHttpClient().execute(httpGet);
		String result=EntityUtils.toString(httpResponse.getEntity());
		System.out.println(result);
	}
	

		public static Connection conn()
		{
			String driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			String url = "jdbc:sqlserver://192.168.1.114;DatabaseName=ErpNWBMStock"; // 123为数据库名字
			String password = "disneyatyongjun";
			String user1 = "login";
			Connection conn = null;
			try
			{
				Class.forName(driverClassName);
			} catch (ClassNotFoundException ex)
			{
				System.out.println("加载错误！");
			}
			try
			{
				conn = DriverManager.getConnection(url, user1, password);
				System.out.println("成功");
			} catch (SQLException ex1)
			{
				ex1.printStackTrace();
				System.out.println("失败");
			}
			return conn;
		}
}
