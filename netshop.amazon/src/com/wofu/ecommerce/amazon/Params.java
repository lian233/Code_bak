package com.wofu.ecommerce.amazon;

import java.util.Properties;

import com.wofu.common.tools.util.log.Log;

public class Params {
	
	public static String dbname = "amazon";
	
	public static String tradecontactid="";

	public static String serviceurl = "https://mws.amazonservices.com.cn/";	

	public static String accesskeyid = "";

	public static String secretaccesskey = "";
	
	public static String applicationname="";
	
	public static int waittime = 10;
	
	public static String applicationversion="";
	
	public static String sellerid="";
	
	public static String marketplaceid = "";
	
	public static String username = "纳纹服饰旗舰店";
	
	public static String linkman="丁呈烨";
	
	public static String address=" 浙江省杭州市下城区石祥路59号新华经济园区12幢2楼";
	
	public static String zipcode="310000";
	
	public static String phone="0571-88130653";

	public static String mobile="";
	public static int isDelay;
	public static int tableType;

	public static String token;

	public static boolean isgenorder;
	public static boolean isgenorderRet;  //是否调用接口订单生成退货订单线程
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "paipai");
		tradecontactid=properties.getProperty("tradecontactid","");
		serviceurl = properties.getProperty("serviceurl", "https://mws.amazonservices.com.cn/");
		accesskeyid = properties.getProperty("accesskeyid", "");
		secretaccesskey = properties.getProperty("secretaccesskey", "");
		applicationname = properties.getProperty("applicationname", "");
		applicationversion = properties.getProperty("applicationversion", "");
		sellerid = properties.getProperty("sellerid", "");
		marketplaceid = properties.getProperty("marketplaceid", "10");		
		username = properties.getProperty("username", "纳纹服饰旗舰店");
		address = properties.getProperty("address", "浙江省杭州市下城区石祥路59号新华经济园区12幢2楼");
		zipcode = properties.getProperty("zipcode", "310000");
		phone = properties.getProperty("phone", "0571-88130653");
		mobile = properties.getProperty("mobile", "");
		linkman = properties.getProperty("linkman", "");
		token = properties.getProperty("token", "");
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isgenorder = Boolean.parseBoolean(properties.getProperty("isgenorder", "true"));
		isgenorderRet = Boolean.parseBoolean(properties.getProperty("isgenorderRet", "true"));
	}
}

