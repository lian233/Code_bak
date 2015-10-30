package com.wofu.ecommerce.icbc;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	// 请求URL地址
	public static String dbname = "icbc";
	public static String pageSize = "10";
	public static String url = "https://82.200.111.247:8443//icbcrouter";
	public static String tradecontactid = "24";
	public static String encoding = "GBK";
	public static int total = 10;
	public static int waittime = 10;
	public static String orderState = "101";
	public static String username = "工行商城";
	public static String address = "广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼";
	public static String zipcode = "510610";
	public static String phone = "020-38458026";
	public static String mobile = "";
	public static String company = "" ;
	public static String companyTel = "" ;
	public static String codDeliveryBeginTime="" ;
	public static String codDeliveryEndTime="" ;
	public static int timeInterval = 30 ;
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	public static String OUT_HOST_IP="82.200.111.247";
	public static int staticOUT_HOST_PORT=8443;
	public static String OUT_HOST_URI="icbcrouter";

	public static String OUT_APP_KEY="test_APP_KEY_01";
	public static String OUT_AUTH_CODE="UZTD";
	public static String OUT_APP_SECRET="test_APP_SECRET_01";

	public static final String OUT_API_VERSION="1.0";
	public static final String OUT_API_FORMAT="xml";
	public static String OUT_API_TIMESTAMP="";
	public static String OUT_API_REQSID="";
	public static String OUT_COMMUNICATION_CHARSET="UTF-8";

	//#DON'T MODIFY the OUT_API_SIGNTYPE
	public static String OUT_API_SIGNTYPE="HMACSHA256";

	//#the https keystore path
	//#the https password
	public static String OUT_PASSWORD="123456";
	public static String trustStore="";
	
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "dangdang");
		tradecontactid=properties.getProperty("tradecontactid","10");
		total=Integer.parseInt(properties.getProperty("total","10"));
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		orderState=properties.getProperty("orderState","101");
		username=properties.getProperty("username","迪士尼当当商城");
		address=properties.getProperty("address","广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼");
		zipcode=properties.getProperty("zipcode","");
		phone=properties.getProperty("phone","");
		mobile=properties.getProperty("mobile","");
		company=properties.getProperty("companycode","EMS:EMS;HTKY:汇通快运;POST:中国邮政平邮;SF:顺丰速运;STO:申通E物流;YTO:圆通速递");
		companyTel=properties.getProperty("company","EMS:11183;HTKY:021-62963636;POST:中国邮政平邮;SF:4008111111;STO:400-889-5543;YTO:021-6977888/999");
		codDeliveryBeginTime=properties.getProperty("codDeliveryBeginTime","6:00:00");
		codDeliveryEndTime=properties.getProperty("codDeliveryEndTime","11:00:00");
		timeInterval=Integer.parseInt(properties.getProperty("timeInterval","30"));
		pageSize= properties.getProperty("pageSize","10");
		url= properties.getProperty("url","");
		OUT_APP_KEY= properties.getProperty("OUT_APP_KEY","");
		OUT_AUTH_CODE= properties.getProperty("OUT_AUTH_CODE","");
		OUT_APP_SECRET= properties.getProperty("OUT_APP_SECRET","");
		trustStore= properties.getProperty("trustStore","");
		OUT_PASSWORD= properties.getProperty("OUT_PASSWORD","");
		//获取对应的快递公司名称
		String com[] = company.split(";") ;
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			htComCode.put(s[0], s[1]) ;
		}
		//获取快递公司电话
		String comTel[] = companyTel.split(";") ;
		for(int j = 0 ; j < comTel.length ; j++)
		{
			String s[] = comTel[j].split(":") ;
			htComTel.put(s[0], s[1]) ;
		}
	}
}
