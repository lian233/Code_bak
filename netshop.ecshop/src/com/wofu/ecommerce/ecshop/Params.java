package com.wofu.ecommerce.ecshop;

import java.util.Hashtable;
import java.util.Properties;

import com.wofu.common.tools.util.log.Log;

public class Params {
	// ����URL��ַ
	public static String dbname = "suning";
	public static String pageSize = "10";
	public static String url = "http://www.mesuca.com/api.php";
	public static String center_key = "wofuerp1101";
	public static String tradecontactid = "24";
	public static String encoding = "GBK";
	public static int total = 10;
	public static int waittime = 10;
	public static String orderState = "101";
	public static String username = "�����������̳�";
	public static String address = "�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥";
	public static String zipcode = "510610";
	public static String phone = "020-38458026";
	public static String mobile = "";
	public static String company = "" ;
	public static String companycode = "" ;
	public static String companyTel = "" ;
	public static String codDeliveryBeginTime="" ;
	public static String codDeliveryEndTime="" ;
	public static int timeInterval = 30 ;
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComCode1 = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	public static String session="";
	public static String appKey = "";
	public static String appsecret = "";
	public static String format = "";       // ��Ӧ��ʽ xml����json
	public static int isDelay;
	public static int tableType;
	public static boolean isFire=false;
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "dangdang");
		tradecontactid=properties.getProperty("tradecontactid","10");
		session=properties.getProperty("session","sadf");
		appKey=properties.getProperty("app_key","df");
		appsecret=properties.getProperty("app_Secret","eee");
		total=Integer.parseInt(properties.getProperty("total","10"));
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		orderState=properties.getProperty("orderState","101");
		username=properties.getProperty("username","��ʿ�ᵱ���̳�");
		address=properties.getProperty("address","�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥");
		zipcode=properties.getProperty("zipcode","");
		phone=properties.getProperty("phone","");
		mobile=properties.getProperty("mobile","");
		company=properties.getProperty("company","12:EMS;HTKY:��ͨ����;POST:�й�����ƽ��;17:SF;STO:��ͨE����;YTO:Բͨ�ٵ�");
		companyTel=properties.getProperty("company","EMS:11183;HTKY:021-62963636;POST:�й�����ƽ��;SF:4008111111;STO:400-889-5543;YTO:021-6977888/999");
		timeInterval=Integer.parseInt(properties.getProperty("timeInterval","30"));
		format= properties.getProperty("format","json");
		pageSize= properties.getProperty("pageSize","10");
		center_key= properties.getProperty("center_key","wofu");
		url= properties.getProperty("url","wofu");
		companycode= properties.getProperty("companycode","EMS:EMS;HTKY:��ͨ����;POST:�й�����ƽ��;SF:˳������;STO:��ͨE����;YTO:Բͨ�ٵ�");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isFire = Boolean.parseBoolean(properties.getProperty("isFire","false"));
		//��ȡ��Ӧ�Ŀ�ݹ�˾����
		String com1[] = company.split(";") ;
		for(int i = 0 ; i < com1.length ; i++)
		{
			String s[] = com1[i].split(":") ;
			htComCode1.put(s[0], s[1]) ;
		}
		String com[] = companycode.split(";") ;
		System.out.println(com.length);
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			htComCode.put(s[0], s[1]) ;
		}
		//��ȡ��ݹ�˾�绰
		String comTel[] = companyTel.split(";") ;
		for(int j = 0 ; j < comTel.length ; j++)
		{
			String s[] = comTel[j].split(":") ;
			htComTel.put(s[0], s[1]) ;
		}
	}
}