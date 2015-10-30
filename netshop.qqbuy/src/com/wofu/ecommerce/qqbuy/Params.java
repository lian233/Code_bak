package com.wofu.ecommerce.qqbuy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Properties;

public class Params {
	public static String dbname = "qqbuy";
	
	public static String uin = "";

	public static String appOAuthID = "";

	public static String secretOAuthKey = "";

	public static String accessToken = "";

	public static String cooperatorId = "";

	public static String encoding = "utf-8";

	public static String format = "xml";

	public static String pageSize = "";

	public static String orderState = "";

	public static String timeType = "";
	
	public static String tradecontactid = "11";

	public static String host = "http://api.buy.qq.com";

	public static int total = 10;

	public static int waittime = 60;

	public static String username = "迪士尼时尚生活馆";

	public static String address = "广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼";

	public static String zipcode = "510610";

	public static String phone = "020-38458026";

	public static String mobile = "";

	private static String expressCompanyIdList = "";
	
	private static String expressCompanyNameList = "";
	
	public static boolean isNeedInvoice = false ;

	public static Hashtable<String, String> expressCompanyIdTable = new Hashtable<String, String>();
	
	public static Hashtable<String, String> expressCompanyNameTable = new Hashtable<String, String>();

	public Params() {
	}

	public static void init(Properties properties) throws UnsupportedEncodingException {
		dbname = properties.getProperty("dbname", "qqbuy");
		uin = properties.getProperty("uin", "");
		appOAuthID = properties.getProperty("appOAuthID", "");
		secretOAuthKey = properties.getProperty("secretOAuthKey", "");
		accessToken = properties.getProperty("accessToken", "");
		cooperatorId = properties.getProperty("cooperatorId", "");
		encoding = properties.getProperty("encoding", "");
		format = properties.getProperty("format", "");
		pageSize = properties.getProperty("pageSize", "");
		orderState = properties.getProperty("orderState", "");
		timeType = properties.getProperty("timeType", "");
		tradecontactid = properties.getProperty("tradecontactid", "");
		host = properties.getProperty("host", "");
		waittime = Integer.parseInt(properties.getProperty("waittime", ""));
		username = properties.getProperty("username", "");
		address = properties.getProperty("address", "");
		zipcode = properties.getProperty("zipcode", "");
		phone = properties.getProperty("phone", "");
		mobile = properties.getProperty("mobile", "");
		expressCompanyIdList = properties.getProperty("expressCompanyIdList","");
		expressCompanyNameList = properties.getProperty("expressCompanyNameList","");
		isNeedInvoice = Boolean.parseBoolean(properties.getProperty("isNeedInvoice", "false")) ;

		//初始化快递公司id列表
		String expressCompanyId[] = expressCompanyIdList.split(";");
		for (int i = 0; i < expressCompanyId.length; i++) {
			String company[] = expressCompanyId[i].split(":");
			expressCompanyIdTable.put(company[0], company[1]);
		}
		//初始化快递公司名称列表
		String expressCompanyName[] = expressCompanyNameList.split(";");
		for (int i = 0; i < expressCompanyName.length; i++) {
			String company[] = expressCompanyName[i].split(":");
			expressCompanyNameTable.put(company[0], URLEncoder.encode(company[1], "utf-8"));
		}

	}
}
