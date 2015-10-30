package com.wofu.ecommerce.miya;

import java.util.Properties;


public class Params {
	public static String dbname = "yhd";
	
	public static String tradecontactid="";

	public static String url = "http://api.open.beibei.com/outer_api/out_gateway/route.html";
	
	public static String encoding="UTF-8";

	public static String merchantid = "7816";

	public static String checkcode  = "77-37927-19-99-11107-48257971-28-16-6076";
	
	public static String secretkey="XcdFt5934LkoPDTRhGQ9";
	
	public static String erp="self";
	
	public static String erpver="1.0";
	
	public static String session="ffee9661497659005621be3a7989e";
	
	public static String company="";
	
	public static String ver="1.0";
	
	public static int total=10;
	
	public static int waittime = 10;
	
	public static String username = "迪士尼时尚旗舰店";
	
	public static String province="广东省";
	
	public static String city="广州市";
	
	public static String district="天河区";
	
	public static String address="车陂路大胜工业区2栋301";
	
	public static String zipcode="510610";
	
	public static String linkman="张利伟";
	
	public static String phone="020-38458026";

	public static String mobile="15992409145";
	
	public static String appid="ecla";
	public static String app_secret="";
	public static String secret="a1961fbd661b64a7a760fde5b2a5fc35";
	public static int isDelay;
	public static int tableType;

	public static boolean isgenorder;//是否生成系统订单
	public static boolean isgenorderRet;  //是否调用接口订单生成退货订单线程

	public static String app_key;

	public static String format;

	public static String token;

	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "taobao");
		tradecontactid=properties.getProperty("tradecontactid","");
		url = properties.getProperty("url", "http://api.open.beibei.com/outer_api/out_gateway/route.html");
		encoding=properties.getProperty("encoding","GBK");
		merchantid = properties.getProperty("merchantid", "");
		erp = properties.getProperty("erp", "");
		erpver = properties.getProperty("erpver", "");
		session = properties.getProperty("session", "ffee9661497659005621be3a7989e");
		company = properties.getProperty("company", "");
		ver = properties.getProperty("ver", "1.0");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("username", "迪士尼时尚旗舰店");
		province= properties.getProperty("province", "广东省");
		city= properties.getProperty("city", "广州市");
		district= properties.getProperty("district", "天河区");
		address = properties.getProperty("address", "车陂路大胜工业区2栋301");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "张利伟");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");
		appid = properties.getProperty("appid", "ecla");
		app_secret = properties.getProperty("app_secret", "15992409145");
		secret = properties.getProperty("secret", "a1961fbd661b64a7a760fde5b2a5fc35");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isgenorder = Boolean.parseBoolean(properties.getProperty("isgenorder", "true"));
		isgenorderRet = Boolean.parseBoolean(properties.getProperty("isgenorderRet", "true"));
	}
}

