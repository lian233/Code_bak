package com.wofu.ecommerce.rke;

import java.util.Properties;


public class Params {
	public static String dbname = "yhd";
	
	public static String tradecontactid="";

	public static String url = "http://www.mesuca.com/shop/index.php?";
	
	public static String encoding="UTF-8";
	public static String AppSecret="";
	
	public static String secretkey="XcdFt5934LkoPDTRhGQ9";
	
	public static String erp="self";
	
	public static String erpver="1.0";
	
	public static String format="xml";
	
	public static String company="";
	
	public static String ver="1.0";
	
	public static int total=10;
	
	public static int waittime = 10;
	public static String pageSize = "50";
	
	public static String username = "迪士尼时尚旗舰店";
	
	public static String province="广东省";
	
	public static String city="广州市";
	
	public static String district="天河区";
	
	public static String address="车陂路大胜工业区2栋301";
	
	public static String zipcode="510610";
	
	public static String linkman="张利伟";
	
	public static String phone="020-38458026";

	public static String mobile="15992409145";
	
	public static String app_key="";
	public static String app_secret="";
	public static int isDelay;
	public static int tableType;
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "taobao");
		tradecontactid=properties.getProperty("tradecontactid","");
		url = properties.getProperty("url", "http://www.mesuca.com/shop/index.php?");
		encoding=properties.getProperty("encoding","GBK");
		erp = properties.getProperty("erp", "");
		erpver = properties.getProperty("erpver", "");
		format = properties.getProperty("format", "xml");
		company = properties.getProperty("company", "");
		ver = properties.getProperty("ver", "1.0");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		pageSize = properties.getProperty("pageSize", "30");		
		username = properties.getProperty("username", "迪士尼时尚旗舰店");
		province= properties.getProperty("province", "广东省");
		city= properties.getProperty("city", "广州市");
		district= properties.getProperty("district", "天河区");
		address = properties.getProperty("address", "车陂路大胜工业区2栋301");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "张利伟");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");
		app_key = properties.getProperty("app_key", "15992409145");
		app_secret = properties.getProperty("app_secret", "15992409145");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
	}
}

