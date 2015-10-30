package com.wofu.intf.sf;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	public static String dbname = "best";
	public static String company = "";
	public static String checkword = "";
	public static String url = "http://edi-gateway.800best.com/eoms/api/process";
	public static String callbackurl = "http://fxdis.vicp.cc:8002/BestLogisticsService";   //回调URL
	public static String encoding = "UTF-8";
	public static int waittime = 10;
	public static String customercode="85000267";  //商家编码
	public static String interfacesystem="FKWMS";
	public static String serviceversion="1.0";
	public static String msgtype="sync";
	
	public static String linkman="张利伟";
	public static String province="广东省";	
	public static String city="广州市";	
	public static String district="天河区";	
	public static String address="车陂路大胜工业区2栋301";
	public static String zipcode="510610";	
	public static String phone="020-38458026";
	public static String mobile="15992409145";
	public static String email="panxingke@163.com";
	public static String warehouse="";
	public static String monthly_account="";
	public static String source_id="";
	public static Boolean isBarcodeId=false;//isBarcodeId

	
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "best");
		company=properties.getProperty("company","");
		checkword=properties.getProperty("checkword","");
		url=properties.getProperty("url","");
		callbackurl=properties.getProperty("callbackurl","");
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		customercode=properties.getProperty("customercode","");
		interfacesystem=properties.getProperty("interfacesystem","");
		serviceversion=properties.getProperty("serviceversion","1.0");
		msgtype=properties.getProperty("msgtype","sync");
		
		province= properties.getProperty("province", "广东省");
		city= properties.getProperty("city", "广州市");
		district= properties.getProperty("district", "");
		address = properties.getProperty("address", "");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");
		email = properties.getProperty("email", "");
		isBarcodeId = Boolean.parseBoolean(properties.getProperty("isBarcodeId","false")) ;
		encoding = properties.getProperty("encoding","utf-8") ;
		warehouse = properties.getProperty("warehouse","utf-8") ;
		source_id = properties.getProperty("source_id","utf-8") ;
		monthly_account = properties.getProperty("monthly_account","") ;
	
	}
}
