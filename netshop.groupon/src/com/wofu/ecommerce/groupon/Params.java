package com.wofu.ecommerce.groupon;

import java.util.Properties;

public class Params {
	public static String dbname = "groupon";
	
	public static String tradecontactid="";

	public static String namespace = "http://www.groupon.cn/";

	public static String wsurl = "http://store.groupon.cn/services/BusinessProjectService";
	
	public static String encoding="GBK";

	public static String key = "28831102";

	public static String categoryid = "68";
	
	public static int waittime = 10;
	
	public static int limit=5;
	
	public static int total=20;

	public static int requesttotal=10; 
	
	public static String username = "广州永骏经济发展有限公司";
	
	public static String address="广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼";
	
	public static String zipcode="510610";
	
	public static String phone="020-38458026";

	public static String mobile="";
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "groupon");	
		tradecontactid=properties.getProperty("tradecontactid","");
		namespace = properties.getProperty("namespace", "http://www.groupon.cn/");
		wsurl = properties.getProperty("wsurl", "http://store.groupon.cn/services/BusinessProjectService");
		encoding=properties.getProperty("encoding","GBK");
		key = properties.getProperty("key", "28831102");
		categoryid = properties.getProperty("categoryid", "68");
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();
		limit = (new Integer(properties.getProperty("limit", "5"))).intValue();
		total = (new Integer(properties.getProperty("total", "20"))).intValue();
		requesttotal = (new Integer(properties.getProperty("requesttotal", "10"))).intValue();
		username = properties.getProperty("username", "广州永骏经济发展有限公司");
		address = properties.getProperty("address", "广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼");
		zipcode = properties.getProperty("zipcode", "510610");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "");
	}
}

