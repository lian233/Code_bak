package com.wofu.ecommerce.weidian;

import java.util.Hashtable;
import java.util.Properties;

public class Params
{
	public static String url = "http://api.vdian.com/api";  
	public static String tradecontactid = "34";
	public static String encoding = "GBK";
	public static int waittime = 10;
	public static String username = "贝贝怡官方微商城";
	public static String dbname = "weidian";
	public static String session;
	public static String app_key="624836";
	public static String app_Secret="be3e43263cffd693671bf842422e3829";
	public static int total;
	public static String sendMode;
	public static String orderState;
	public static String address;
	public static String zipcode;
	public static String phone;
	public static String mobile;
	public static String company;
	public static String companyTel;
	public static String codDeliveryBeginTime;
	public static String codDeliveryEndTime;
	public static int timeInterval;
	public static Integer isDelay;
	public static Integer tableType;
	public static String format = "json";
	public static String ver;
	public static boolean isgenorder;  //是否调用接口订单生成订单线程
	public static boolean isgenorderRet;  //是否调用接口订单生成退货订单线程
	public static Hashtable<Object, Object> htComCode= new Hashtable<Object, Object>();
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "weidian");
		url=properties.getProperty("url","http://api.vdian.com/api");
		tradecontactid=properties.getProperty("tradecontactid","34");
		encoding=properties.getProperty("encoding","GBK");
		session=properties.getProperty("session","");
		app_key=properties.getProperty("app_key","624836"); 
		app_Secret=properties.getProperty("app_Secret","be3e43263cffd693671bf842422e3829");
		total=Integer.parseInt(properties.getProperty("total","10"));
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		sendMode=properties.getProperty("sendMode","9999");
		orderState=properties.getProperty("orderState","101");
		username=properties.getProperty("username","贝贝怡官方微商城");
		address=properties.getProperty("address","");
		zipcode=properties.getProperty("zipcode","");
		phone=properties.getProperty("phone","");
		mobile=properties.getProperty("mobile","");
		company=properties.getProperty("company","圆通:2;   EMS:9;   申通:4");
		companyTel=properties.getProperty("company","EMS:11183;HTKY:021-62963636;POST:中国邮政平邮;SF:4008111111;STO:400-889-5543;YTO:021-6977888/999");
		codDeliveryBeginTime=properties.getProperty("codDeliveryBeginTime","6:00:00");
		codDeliveryEndTime=properties.getProperty("codDeliveryEndTime","11:00:00");
		timeInterval=Integer.parseInt(properties.getProperty("timeInterval","30"));
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isgenorder = Boolean.parseBoolean(properties.getProperty("isgenorder", "true"));
		isgenorderRet = Boolean.parseBoolean(properties.getProperty("isgenorderRet", "true"));
		format=properties.getProperty("format","json");
		ver=properties.getProperty("ver","2.0");
		//获取对应的快递公司名称
		String com[] = company.split(";") ;
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			htComCode.put(s[0], s[1]) ;
		}
	}
}
