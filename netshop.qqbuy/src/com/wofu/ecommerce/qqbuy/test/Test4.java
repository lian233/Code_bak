package com.wofu.ecommerce.qqbuy.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

//http://love-love-l.blog.163.com/blog/static/210783042009111101333464/
public class Test4 {

	public static void main(String[] args) {
		String urlStr = "http://api.buy.qq.com/deal/queryDealListV2.xhtml?sign=%2F9ILspCugd4k1lApOJ1m%2FdB3sHU%3D&accessToken=7faff45d7bd43cae61c72f3101c0572b&appOAuthID=700043070&cooperatorId=855010773&dealState=STATE_WAIT_CHECK&randomValue=1344840404640&timeStamp=1344840404640&uin=855010773";
		String result = getResponseData(urlStr) ;
		System.out.println(result) ;
	}
	public static String getResponseData(String urlStr)
	{
		URL url;
		String sTotalString = "";
		try {
			url = new URL(urlStr);
			URLConnection URLconnection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) 
			{
				InputStream urlStream = httpConnection.getInputStream();
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(urlStream));
				String sCurrentLine = "";
				while ((sCurrentLine = bufferedReader.readLine()) != null) 
				{
					sTotalString += sCurrentLine;
				}
			} 
			else 
			{
				System.err.println("Ê§°Ü");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch blockeb
			e.printStackTrace();
		}
		return sTotalString ;
	}
}