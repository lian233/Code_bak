package com.wofu.ecommerce.coo8;

import java.util.Properties;

public class Params {
	public static String appKey="80000167";
	
	public static String secretKey="d646ab2210e44306bcf015c8595101f6";
	
	public static String url="http://api.coo8.com/ApiControl";
	
	public static String version="2.0";
	
	public static String signmehtod="md5";
	
	public static String requestmodel="json";
	
	public static String username="��ͱ������콢��";
	
	public static String dbname="coo8";
	
	public static int waittime=10;
	
	public static String tradecontactid="";
	
	public static String company="EMS:99900005;STO:99900023;YTO:99900032;YUNDA:99900033";
	
	public static String province="�㶫ʡ";
	
	public static String city="������";
	
	public static String district="�����";
	
	public static String address="����·��ʤ��ҵ��2��301";
	
	public static String zipcode="510610";
	
	public static String linkman="����ΰ";
	
	public static String phone="020-38458026";
	
	public static String mobile="15992409145";
	public static int isDelay;
	public static int tableType;
	public static boolean isgenorder;
	public static boolean isgenorderRet;
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "coo8");
		tradecontactid=properties.getProperty("tradecontactid","10");
		url = properties.getProperty("url", "http://api.coo8.com/ApiControl");
		signmehtod=properties.getProperty("signmehtod", "md5"); 
		requestmodel=properties.getProperty("requestmodel", "json"); 
		appKey = properties.getProperty("appKey", "80000167");
		secretKey=properties.getProperty("secretKey", "d646ab2210e44306bcf015c8595101f6");
		version=properties.getProperty("version", "2.0");
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("username", "��ͱ������콢��");
		company = properties.getProperty("company", "");
		province= properties.getProperty("province", "�㶫ʡ");
		city= properties.getProperty("city", "������");
		district= properties.getProperty("district", "�����");
		address = properties.getProperty("address", "����·��ʤ��ҵ��2��301");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "����ΰ");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");	
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isgenorder = Boolean.parseBoolean(properties.getProperty("isgenorder", "true"));
		isgenorderRet = Boolean.parseBoolean(properties.getProperty("isgenorderRet", "true"));
	}
}
