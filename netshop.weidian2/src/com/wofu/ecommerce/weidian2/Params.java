package com.wofu.ecommerce.weidian2;

import java.util.Hashtable;
import java.util.Properties;

public class Params
{
	public static String url = "";  
	public static String tradecontactid = "24";
	public static String encoding = "GBK";
	public static int waittime = 10;
	public static String username = "";
	public static String dbname = "weidian2";
	public static String session;
	public static String vcode = "CE6BB129-CE11-4008-9D4E-B0AC4003F69F";
	public static String app_key="";
	public static String app_Secret="";
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
	public static int	 page_size = 100; //20
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	public static void init(Properties properties)
	{	
		dbname = properties.getProperty("dbname", "weidian2");
		url=properties.getProperty("url","http://gx.s.cn/api/fx");
		tradecontactid=properties.getProperty("tradecontactid","24");
		encoding=properties.getProperty("encoding","GBK");
		session=properties.getProperty("session","");
		app_key=properties.getProperty("app_key","yitong"); //名鞋库也用到
		app_Secret=properties.getProperty("app_Secret","ose89salg256g9w");//名鞋库也用到
		total=Integer.parseInt(properties.getProperty("total","10"));
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		page_size=Integer.parseInt(properties.getProperty("page_size","20"));
		sendMode=properties.getProperty("sendMode","9999");
		orderState=properties.getProperty("orderState","101");
		username=properties.getProperty("username","名鞋库");
		address=properties.getProperty("address","");
		zipcode=properties.getProperty("zipcode","");
		phone=properties.getProperty("phone","");
		mobile=properties.getProperty("mobile","");
		company=properties.getProperty("company","顺丰");
		companyTel=properties.getProperty("company","EMS:11183;HTKY:021-62963636;POST:中国邮政平邮;SF:4008111111;STO:400-889-5543;YTO:021-6977888/999");
		codDeliveryBeginTime=properties.getProperty("codDeliveryBeginTime","6:00:00");
		codDeliveryEndTime=properties.getProperty("codDeliveryEndTime","11:00:00");
		timeInterval=Integer.parseInt(properties.getProperty("timeInterval","30"));
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		/***微店API独有部分****/
		vcode = properties.getProperty("vcode","CE6BB129-CE11-4008-9D4E-B0AC4003F69F");
		format=properties.getProperty("format","json");
		ver=properties.getProperty("ver","2.0");
		
		//获取对应的快递公司名称
		String com[] = company.split(";");
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			htComCode.put(s[0],s[1]);
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
