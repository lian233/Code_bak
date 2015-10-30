package com.wofu.ecommerce.vjia;

import java.util.Hashtable;
import java.util.Properties;

import com.wofu.common.tools.util.log.Log;


public class Params {
	public static String dbname = "vjia";
	
	public static String tradecontactid="7";

	public static String wsurl = "http://sws2.vjia.com/swsms";
	
	public static String uri = "http://swsms.vjia.org/" ;
	
	public static String encoding = "GBK";
	
	public static String strkey = "" ;
	
	public static String striv = "" ;
	
	public static String supplierid = "" ;
	
	public static String suppliersign = "" ;
	
	public static String swssupplierid = "" ;
	
	public static String pageSize = "" ;
	
	public static int waittime = 60;
	
	public static String username = "��ʿ��ʱ�йٷ��콢��";
	
	public static String address="�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥";
	
	public static String zipcode="510610";
	
	public static String phone="020-38458026";

	public static String mobile="";
	
	public static String companycode="";
	
	public static int timeInterval = 10 ;
	public static int isDelay;
	public static int tableType;
	public static boolean isgenorder;//
	public static boolean isgenorderRet;
	
	
	public static Hashtable<String,String> htCom = new Hashtable<String, String>() ;
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "vjia");
		tradecontactid=properties.getProperty("tradecontactid","7");
		wsurl = properties.getProperty("wsurl", "http://sws2.vjia.com/swsms");
		uri = properties.getProperty("uri", "http://swsms.vjia.org/");
		encoding=properties.getProperty("encoding","GBK");
		strkey=properties.getProperty("strkey","");
		striv=properties.getProperty("striv","");
		supplierid=properties.getProperty("supplierid","");
		suppliersign=properties.getProperty("suppliersign","");
		swssupplierid=properties.getProperty("swssupplierid","");
		pageSize=properties.getProperty("pagesize","10");
		waittime = (new Integer(properties.getProperty("waittime", "60"))).intValue();		
		username = properties.getProperty("username", "��ʿ��ʱ���콢��");
		address = properties.getProperty("address", "�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥");
		zipcode = properties.getProperty("zipcode", "510610");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "");
		companycode=properties.getProperty("companycode","EMS:EMS;HTKY:��ͨ����;POST:�й�����ƽ��;SF:˳������;STO:��ͨE����;YTO:Բͨ�ٵ�");
		timeInterval = Integer.parseInt(properties.getProperty("timeInterval", "10"));
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isgenorder = Boolean.parseBoolean(properties.getProperty("isgenorder", "true"));
		isgenorderRet = Boolean.parseBoolean(properties.getProperty("isgenorderRet", "true"));
		//��ȡ��Ӧ�Ŀ�ݹ�˾���
		String com[] = companycode.split(";") ;
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			htCom.put(s[0], s[1]) ;
		}
	}
}

