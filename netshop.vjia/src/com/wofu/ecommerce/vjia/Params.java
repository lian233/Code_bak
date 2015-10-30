package com.wofu.ecommerce.vjia;

import java.util.Hashtable;
import java.util.Properties;

import com.wofu.common.tools.util.log.Log;


public class Params {
	public static String dbname = "vjia";
	
	public static String tradecontactid="7";

	public static String wsurl = "http://sws2.vjia.com/swsms";
	
	public static String uri = "http://swsms.vjia.org/" ;
	
	public static String encoding = "GBK";
	
	public static String strkey = "" ;
	
	public static String striv = "" ;
	
	public static String supplierid = "" ;
	
	public static String suppliersign = "" ;
	
	public static String swssupplierid = "" ;
	
	public static String pageSize = "" ;
	
	public static int waittime = 60;
	
	public static String username = "迪士尼时尚官方旗舰店";
	
	public static String address="广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼";
	
	public static String zipcode="510610";
	
	public static String phone="020-38458026";

	public static String mobile="";
	
	public static String companycode="";
	
	public static int timeInterval = 10 ;
	public static int isDelay;
	public static int tableType;
	public static boolean isgenorder;//
	public static boolean isgenorderRet;
	
	
	public static Hashtable<String,String> htCom = new Hashtable<String, String>() ;
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "vjia");
		tradecontactid=properties.getProperty("tradecontactid","7");
		wsurl = properties.getProperty("wsurl", "http://sws2.vjia.com/swsms");
		uri = properties.getProperty("uri", "http://swsms.vjia.org/");
		encoding=properties.getProperty("encoding","GBK");
		strkey=properties.getProperty("strkey","");
		striv=properties.getProperty("striv","");
		supplierid=properties.getProperty("supplierid","");
		suppliersign=properties.getProperty("suppliersign","");
		swssupplierid=properties.getProperty("swssupplierid","");
		pageSize=properties.getProperty("pagesize","10");
		waittime = (new Integer(properties.getProperty("waittime", "60"))).intValue();		
		username = properties.getProperty("username", "迪士尼时尚旗舰店");
		address = properties.getProperty("address", "广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼");
		zipcode = properties.getProperty("zipcode", "510610");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "");
		companycode=properties.getProperty("companycode","EMS:EMS;HTKY:汇通快运;POST:中国邮政平邮;SF:顺丰速运;STO:申通E物流;YTO:圆通速递");
		timeInterval = Integer.parseInt(properties.getProperty("timeInterval", "10"));
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isgenorder = Boolean.parseBoolean(properties.getProperty("isgenorder", "true"));
		isgenorderRet = Boolean.parseBoolean(properties.getProperty("isgenorderRet", "true"));
		//获取对应的快递公司序号
		String com[] = companycode.split(";") ;
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			htCom.put(s[0], s[1]) ;
		}
	}
}

