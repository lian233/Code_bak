package com.wofu.ecommerce.taobao.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.taobao.api.internal.util.WebUtils;


import java.net.MalformedURLException;

public class test1 {

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void main(String[] args) throws Exception {

		System.out.println("www");
		getToken();   
		//refreshToken();
		
	}
	
	private static void getToken() throws Exception
	{
		Map<String, String> param = new HashMap<String, String>();
		/*param.put("grant_type", "authorization_code");
		param.put("code", "oU46bk6RbEVXK1aTB6EUOYLY1974644");
		param.put("client_id", "21520535");
		param.put("client_secret", "766bce17fd8ac852ea02a740277f1289");
		param.put("redirect_uri", "http://122.225.94.174:8002/login.html");
		param.put("view", "web");
		param.put("state", "code");*/
		
		System.out.println("test1");
		//http://121.196.132.134:30002/login.html?code=zdlKe9iWhw5a0H8DzuLj8Gyh802045&state=scope%3Ar1%2Cr2%2Cw1%2Cw2%3Bsign%3AE7B823B15F696C2BB58C8EF5FCDA3F54%3BleaseId%3A0%3Btimestamp%3A1393984973064%3BversionNo%3A1%3Bouter_trade_code%3A%3BitemCode%3AFW_GOODS-1872240-1
		param.put("grant_type", "authorization_code");
		param.put("code", "ZF8hMe3KJPiavlbG7ZkoOiUp350106");
		param.put("client_id", "21520535");
		param.put("client_secret", "766bce17fd8ac852ea02a740277f1289");
		param.put("redirect_uri", "http://122.225.94.174:8002/login.html");
		param.put("view", "web");
		param.put("state", "code");

		String responseJson=WebUtils.doPost("https://oauth.taobao.com/token", param, 3000, 3000);

		System.out.println(responseJson);
	}
	
	private static void refreshToken() throws Exception
	{
		Map<String, String> param = new HashMap<String, String>();
		param.put("grant_type", "refresh_token");
		param.put("refresh_token", "6202723285a7f96236ba9945ef00ddceg99259ccbfa39d71629289588");
		param.put("client_id", "21520535");
		param.put("client_secret", "766bce17fd8ac852ea02a740277f1289");
		param.put("redirect_uri", "http://gzwolfsoft.oicp.net:8002/login.html");
		param.put("view", "web");
		param.put("state", "code");

		String responseJson=WebUtils.doPost("https://oauth.taobao.com/token", param, 3000, 3000);

		System.out.println(responseJson);
	}
	
	private static String verifyBarcode(String in_sBarcode12) 
	{
		String sStr="";
		int nNum1 = Integer.valueOf(in_sBarcode12.substring(11,12)).intValue();
		System.out.println("test1");
		
		int nNum2 = Integer.valueOf(in_sBarcode12.substring(9,10)).intValue();
		int nNum3 = Integer.valueOf(in_sBarcode12.substring(7,8)).intValue();
		int nNum4 = Integer.valueOf(in_sBarcode12.substring(5,6)).intValue();
		int nNum5 = Integer.valueOf(in_sBarcode12.substring(3,4)).intValue();
		int nNum6 = Integer.valueOf(in_sBarcode12.substring(1,2)).intValue();

		System.out.println("test2");
		int nNum = ( nNum1+nNum2+nNum3+nNum4+nNum5+nNum6 )*3;
	  
		nNum1 = Integer.valueOf(in_sBarcode12.substring(10,11)).intValue();
		nNum2 = Integer.valueOf(in_sBarcode12.substring(8,9)).intValue();
		nNum3 = Integer.valueOf(in_sBarcode12.substring(6,7)).intValue();
		nNum4 = Integer.valueOf(in_sBarcode12.substring(4,5)).intValue();
		nNum5 = Integer.valueOf(in_sBarcode12.substring(2,3)).intValue();
		nNum6 = Integer.valueOf(in_sBarcode12.substring(0,1)).intValue();
		System.out.println("test3");

		nNum = nNum+nNum1+nNum2+nNum3+nNum4+nNum5+nNum6;
		nNum1 = nNum/10;
		nNum = 10-(nNum - nNum1*10);
		if (nNum ==10)
		    sStr = "0";
		  else
			sStr=String.valueOf(nNum);

		String out_sBarCode13=in_sBarcode12.concat(sStr);
		
		return out_sBarCode13;
	}
	
	private static Connection getConnection() throws Exception
	{

		String driver="com.microsoft.jdbc.sqlserver.SQLServerDriver";
		String url="jdbc:microsoft:sqlserver://127.0.0.1:1433;DatabaseName=ecsshop";
		String user="admin";
		String password="yongjun2006WMS";
		 
		if (driver != null && !driver.equals("")) {
			DriverManager.registerDriver(
				(Driver) Class.forName(driver).newInstance());
		}
		if (user != null) {
			return DriverManager.getConnection(url, user, password);
		} else {
			return DriverManager.getConnection(url);
		}
			
	}
	
	private static String getOneHtml(final String htmlurl) throws IOException {

		URL url;
		String temp;
		final StringBuffer sb = new StringBuffer();
		try {
			url = new URL(htmlurl);
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream(), "UTF-8"));// 读取网页全部内容
			while ((temp = in.readLine()) != null) {
				sb.append(temp);
			}
			in.close();
		} catch (final MalformedURLException me) {
			System.out.println("你输入的URL格式有问题！请仔细输入");
			me.getMessage();
			throw me;
		} catch (final IOException e) {
			e.printStackTrace();
			throw e;
		}
		return sb.toString();
	}

	private static String getTitle(final String s) {
		String regex;
		String title = "";
		final List<String> list = new ArrayList<String>();
		regex = "<title>.*?</title>";
		final Pattern pa = Pattern.compile(regex, Pattern.CANON_EQ);
		final Matcher ma = pa.matcher(s);
		while (ma.find()) {
			list.add(ma.group());
		}
		for (int i = 0; i < list.size(); i++) {
			title = title + list.get(i);
		}
		return outTag(title);
	}

	private static String outTag(final String s) {
		return s.replaceAll("<.*?>", "");
	}

}
