package com.wofu.ecommerce.oauthpaipai;

import java.util.Properties;

import com.wofu.common.tools.util.log.Log;

public class Params {
	public static String dbname = "paipai";
	
	public static String tradecontactid="";

	public static String wsurl = "http://api.paipai.com/";
	
	public static String encoding="GBK";

	public static String spid = "";

	public static String secretkey = "";
	
	public static String uid="";
	
	public static String token="";
	
	public static int total=10;
	
	public static int waittime = 10;
	
	public static String username = "��ʿ��ʱ�йٷ��콢��";
	
	public static String address="�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥";
	
	public static String zipcode="510610";
	
	public static String phone="020-38458026";

	public static String mobile="";
	public static int isDelay=0;    //���ɶ������ʱ�����������Ƿ��ӳ�����
	public static int tableType=0;    //���ɶ������ʱ���������Դ����ʱ����Դ��

	public static boolean isgenorder;
	public static boolean isgenorderRet;
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "paipai");
		tradecontactid=properties.getProperty("tradecontactid","");
		wsurl = properties.getProperty("wsurl", "http://api.paipai.com/");
		encoding=properties.getProperty("encoding","GBK");
		spid = properties.getProperty("spid", "");
		secretkey = properties.getProperty("secretkey", "");
		uid = properties.getProperty("uid", "");
		token = properties.getProperty("token", "");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("username", "��ʿ��ʱ���콢��");
		address = properties.getProperty("address", "�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥");
		zipcode = properties.getProperty("zipcode", "510610");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isgenorder = Boolean.parseBoolean(properties.getProperty("isgenorder", "true"));
		isgenorderRet = Boolean.parseBoolean(properties.getProperty("isgenorderRet", "true"));
	}
}

