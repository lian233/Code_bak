package com.wofu.ecommerce.lefeng;

import java.util.Properties;

import com.wofu.common.tools.util.log.Log;

public class Params {
	public static String dbname = "lefeng";
	
	public static String tradecontactid="";

	public static String url = "http://116.90.82.204:3568/suning/cron_shop/";
	
	public static String encoding="UTF-8";

	public static String shopid = "1000013";

	public static String secretKey  = "&^gK&9Bf9&nw";

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
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "taobao");
		tradecontactid=properties.getProperty("tradecontactid","");
		url = properties.getProperty("url", "http://gw.api.taobao.com/router/rest");
		encoding=properties.getProperty("encoding","GBK");
		shopid = properties.getProperty("shopid", "");
		secretKey = properties.getProperty("secretKey", "");
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
		company = properties.getProperty("company", "STO:82600;YTO:83100;YUNDA:82601");
	}
}

