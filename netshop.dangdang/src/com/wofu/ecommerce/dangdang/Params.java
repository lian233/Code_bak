package com.wofu.ecommerce.dangdang;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	public static String dbname = "dangdang";
	public static final String url = "http://api.open.dangdang.com/openapi/rest?v=1.0";
	public static String tradecontactid = "10";
	public static String encoding = "GBK";
	public static int total = 10;
	public static int waittime = 10;
	public static String sendMode = "9999";
	public static String orderState = "101";
	public static String username = "迪士尼当当商城";
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
	/**
	 * 2013.12.13新增参数
	 */
	public static String session="";
	public static String app_key = "";
	public static String app_Secret = "";
	public static int isDelay;
	public static int tableType;
	public static boolean isgenorder;
	public static boolean isgenorderRet;  //是否调用接口订单生成退货订单线程
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "dangdang");
		//url=properties.getProperty("url","http://api.open.dangdang.com/openapi/rest?v=1.0");
		tradecontactid=properties.getProperty("tradecontactid","10");
		encoding=properties.getProperty("encoding","GBK");
		session=properties.getProperty("session","sadf");
		app_key=properties.getProperty("app_key","df");
		app_Secret=properties.getProperty("app_Secret","eee");
		total=Integer.parseInt(properties.getProperty("total","10"));
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		sendMode=properties.getProperty("sendMode","9999");
		orderState=properties.getProperty("orderState","101");
		username=properties.getProperty("username","迪士尼当当商城");
		address=properties.getProperty("address","广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼");
		zipcode=properties.getProperty("zipcode","");
		phone=properties.getProperty("phone","");
		mobile=properties.getProperty("mobile","");
		company=properties.getProperty("company","EMS:EMS;HTKY:汇通快运;POST:中国邮政平邮;SF:顺丰速运;STO:申通E物流;YTO:圆通速递");
		companyTel=properties.getProperty("company","EMS:11183;HTKY:021-62963636;POST:中国邮政平邮;SF:4008111111;STO:400-889-5543;YTO:021-6977888/999");
		codDeliveryBeginTime=properties.getProperty("codDeliveryBeginTime","6:00:00");
		codDeliveryEndTime=properties.getProperty("codDeliveryEndTime","11:00:00");
		timeInterval=Integer.parseInt(properties.getProperty("timeInterval","30"));
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isgenorder = Boolean.parseBoolean(properties.getProperty("isgenorder", "true"));
		isgenorderRet = Boolean.parseBoolean(properties.getProperty("isgenorderRet", "true"));
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
