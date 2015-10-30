package com.wofu.ecommerce.uwuku;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	public static String dbname = "best";
	public static String tradecontactid="";
	public static String clientid = "1875908317";
	public static String appsecret = "Nawain115";
	public static String url = "http://youwuku.dianking.cn/egou/index.php/api/uwuku/";
	public static String platform = "sina";   //
	public static String encoding = "GBK";
	public static int waittime = 10;
	public static String signtype="md5";  //
	public static String format="json";
	public static String version="1.0";
	public static String username="1.0";
	
	public static String linkman="张利伟";
	public static String province="广东省";	
	public static String city="广州市";	
	public static String district="天河区";	
	public static String address="车陂路大胜工业区2栋301";
	public static String zipcode="510610";	
	public static String phone="020-38458026";
	public static String mobile="15992409145";
	public static String email="panxingke@163.com";


	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "best");
		clientid=properties.getProperty("clientid","");
		appsecret=properties.getProperty("appsecret","");
		url=properties.getProperty("url","");
		platform=properties.getProperty("platform","sina");
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		encoding=properties.getProperty("encoding","GBK");
		signtype=properties.getProperty("signtype","md5");
		format=properties.getProperty("format","json");
		version=properties.getProperty("version","1.0");
		username=properties.getProperty("username","");
		tradecontactid=properties.getProperty("tradecontactid","");
		province= properties.getProperty("province", "广东省");
		city= properties.getProperty("city", "广州市");
		district= properties.getProperty("district", "");
		address = properties.getProperty("address", "");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");
		email = properties.getProperty("email", "");
	}
}
