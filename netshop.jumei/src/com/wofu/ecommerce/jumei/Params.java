package com.wofu.ecommerce.jumei;

import java.util.Properties;


public class Params {
	public static String dbname = "jumei";
	
	public static String tradecontactid="";

	public static String url = "http://openapi.ext.jumei.com/";
	
	public static String encoding="UTF-8";

	public static String clientid = "722";

	public static String clientkey  = "feee076d68acdf01e10180cc589eca45";
	
	public static String signkey="6fd14c42b714e5070af7fcc6f4535acaef943228";

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
	
	public static String company="STO:82600;YTO:83100;YUNDA:82601";
	public static int isDelay;
	public static int tableType;
	public static boolean isgenorder;  //是否调用接口订单生成订单线程
	public static boolean isgenorderRet;  //是否调用接口订单生成退货订单线程
	
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "taobao");
		tradecontactid=properties.getProperty("tradecontactid","");
		url = properties.getProperty("url", "http://gw.api.taobao.com/router/rest");
		encoding=properties.getProperty("encoding","GBK");
		clientid = properties.getProperty("clientid", "");
		clientkey = properties.getProperty("clientkey", "");
		signkey = properties.getProperty("signkey", "");	
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
		company = properties.getProperty("company", "EMS:6;STO:1;YTO:3;YUNDA:5;SF:4");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isgenorder = Boolean.parseBoolean(properties.getProperty("isgenorder", "true"));
		isgenorderRet = Boolean.parseBoolean(properties.getProperty("isgenorderRet", "true"));
	}
}

