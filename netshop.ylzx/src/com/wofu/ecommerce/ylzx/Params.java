package com.wofu.ecommerce.ylzx;

import java.util.Properties;


public class Params {
	public static String dbname = "yhd";
	
	public static String tradecontactid="";

	public static String url = "http://openapi.yhd.com/app/api/rest/newRouter";
	
	public static String encoding="UTF-8";

	public static String password = "7816";

	public static String checkcode  = "77-37927-19-99-11107-48257971-28-16-6076";
	
	public static String secretkey="XcdFt5934LkoPDTRhGQ9";
	
	public static String erp="self";
	
	public static String erpver="1.0";
	
	public static String format="xml";
	
	public static String company="";
	
	public static String ver="1.0";
	
	public static int total=10;
	
	public static int waittime = 10;
	
	public static String username = "��ʿ��ʱ���콢��";
	public static String user_name = "��ʿ��ʱ���콢��";
	
	public static String province="�㶫ʡ";
	
	public static String city="������";
	
	public static String district="�����";
	
	public static String address="����·��ʤ��ҵ��2��301";
	
	public static String zipcode="510610";
	
	public static String linkman="����ΰ";
	
	public static String phone="020-38458026";

	public static String mobile="15992409145";
	
	public static String app_key="";
	public static String app_secret="";
	public static String token="7a2b396ea85a769703187935d52da647";
	public static int isDelay;
	public static int tableType;
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "taobao");
		tradecontactid=properties.getProperty("tradecontactid","");
		url = properties.getProperty("url", "http://gw.api.taobao.com/router/rest");
		encoding=properties.getProperty("encoding","GBK");
		password = properties.getProperty("password", "");
		user_name = properties.getProperty("user_name", "");
		erp = properties.getProperty("erp", "");
		erpver = properties.getProperty("erpver", "");
		format = properties.getProperty("format", "xml");
		company = properties.getProperty("company", "");
		ver = properties.getProperty("ver", "1.0");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("username", "��ʿ��ʱ���콢��");
		province= properties.getProperty("province", "�㶫ʡ");
		city= properties.getProperty("city", "������");
		district= properties.getProperty("district", "�����");
		address = properties.getProperty("address", "����·��ʤ��ҵ��2��301");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "����ΰ");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");
		app_key = properties.getProperty("app_key", "15992409145");
		app_secret = properties.getProperty("app_secret", "15992409145");
		token = properties.getProperty("token", "15992409145");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
	}
}

