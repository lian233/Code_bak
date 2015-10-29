package com.wofu.intf.fedex;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	public static String dbname = "best";
	public static String Token = "1F80583CB50FA596";
	public static String Key = "e57af65df8b2e46d95f8e983f21c6302";
	public static String url = "http://edi-gateway.800best.com/eoms/api/process";
	public static String callbackurl = "http://fxdis.vicp.cc:8002/BestLogisticsService";   //回调URL
	public static String encoding = "GBK";
	public static int waittime = 10;
	public static String customercode="C0136";  //商家编码
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
	public static Boolean isBarcodeId=false;//isBarcodeId
	public static boolean warehouseMulti=false;  //一个dc的不同业务对应不同的百世仓
	public static boolean isMultiDcToONeWare=false;  //多个dc对应一个百世仓

	
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	public static String partnerkey;
	public static String partnerid;
	
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "best");
		Token=properties.getProperty("Token","");
		Key=properties.getProperty("Key","");
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
		warehouseMulti = Boolean.parseBoolean(properties.getProperty("warehouseMulti","false")) ;
		isMultiDcToONeWare = Boolean.parseBoolean(properties.getProperty("isMultiDcToONeWare","false")) ;
	
	}
}
