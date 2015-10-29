package com.wofu.intf.jw;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	public static String dbname = "jw";
	public static String appSecret = "E-WOLF";
	public static String appkey = "w85n2jsu9b7634js";
	public static String url = "http://edi-gateway.800best.com/eoms/api/process";
	public static String callbackurl = "http://fxdis.vicp.cc:8002/BestLogisticsService";   //回调URL
	public static String encoding = "GBK";
	public static int waittime = 10;
	public static String interfacesystem="FKWMS";
	public static String serviceversion="1.0";
	public static String v="sync";
	
	public static String linkman="张利伟";
	public static String province="广东省";	
	public static String city="广州市";	
	public static String district="天河区";	
	public static String address="车陂路大胜工业区2栋301";
	public static String zipcode="510610";	
	public static String phone="020-38458026";
	public static String mobile="15992409145";
	public static String email="panxingke@163.com";
	public static String format="json";//isBarcodeId
	public static String ownerCode="";//货主编号
	public static String ownerName="";//货主名称
	public static String platFromName="";//平台编码
	public static String shopName="";//店铺编码
	public static String skuHgId="";//海关编号
	public static String hgzc="";//海关账册号
	public static String hgxh="";//海关项号
	public static String vcode="";//验证码
	public static String EshopEntCode = "";//电商企业代码
	public static String EshopEntName = "";//电商企业名称

	
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	public static String  emscode;//ems大客户号
	
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "jw");
		appkey=properties.getProperty("appkey","");
		appSecret=properties.getProperty("appSecret","");
		url=properties.getProperty("url","");
		callbackurl=properties.getProperty("callbackurl","");
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		interfacesystem=properties.getProperty("interfacesystem","");
		serviceversion=properties.getProperty("serviceversion","1.0");
		v=properties.getProperty("v","sync");
		province= properties.getProperty("province", "广东省");
		city= properties.getProperty("city", "广州市");
		district= properties.getProperty("district", "");
		address = properties.getProperty("address", "");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");
		email = properties.getProperty("email", "");
		format = properties.getProperty("format","json") ;
		ownerCode = properties.getProperty("ownerCode","") ;
		ownerName = properties.getProperty("ownerName","") ;
		platFromName = properties.getProperty("platFromName","") ;
		shopName = properties.getProperty("shopName","") ;
		skuHgId = properties.getProperty("skuHgId","") ;
		hgzc = properties.getProperty("hgzc","") ;
		hgxh = properties.getProperty("hgxh","") ;
		vcode = properties.getProperty("vcode","") ;
		EshopEntCode = properties.getProperty("EshopEntCode","") ;
		EshopEntName = properties.getProperty("EshopEntName","") ;
		emscode = properties.getProperty("emscode","") ;
	
	}
}
