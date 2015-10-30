package com.wofu.ecommerce.meilishuo;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	// ����URL��ַ
	public static String dbname = "";
	public static String pageSize = "10";
	public static String url = "http://api.open.meilishuo.com/router/rest";
	public static String tradecontactid = "23";//ʵ�ʻ���Ҫ��23
	public static String encoding = "GBK";
	public static int total = 10;
	public static int waittime = 10;
	public static String orderState = "101";
	public static String username = "����˵";
	public static String address = "";
	public static String zipcode = "";
	public static String phone = "";
	public static String mobile = "";
	public static String company = "" ;
	public static String companyTel = "" ;
	public static String codDeliveryBeginTime="" ;
	public static String codDeliveryEndTime="" ;
	public static int timeInterval = 30 ;
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	public static String session="";
	public static String appKey = "MJ186861556156";
	public static String appsecret = "041c9877e8eaedf774fd611f7909edb0";
	public static String token = "041c9877e8eaedf774fd611f7909edb0";
	public static String format = "json";       // ��Ӧ��ʽ xml����json
	public static String vcode="rvyxostcqmwnqmrvUDwnqmqmqmyxyxUDwnqmqmtcebwn";
	public static boolean isgenorder;
	public static String refreshToken;
	
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "");
		tradecontactid=properties.getProperty("tradecontactid","23");//ʵ�ʻ���Ҫ��23
		session=properties.getProperty("session","sadf");
		appKey=properties.getProperty("app_key","MJ186861556156");
		appsecret=properties.getProperty("app_Secret","041c9877e8eaedf774fd611f7909edb0");
		total=Integer.parseInt(properties.getProperty("total","10"));
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		orderState=properties.getProperty("orderState","101");
		username=properties.getProperty("username","����˵");
		address=properties.getProperty("address","�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥");
		zipcode=properties.getProperty("zipcode","");
		phone=properties.getProperty("phone","");
		mobile=properties.getProperty("mobile","");
		company=properties.getProperty("companycode","EMS:EMS;HTKY:��ͨ����;POST:�й�����ƽ��;SF:˳������;STO:��ͨE����;YTO:Բͨ�ٵ�");
		companyTel=properties.getProperty("company","EMS:11183;HTKY:021-62963636;POST:�й�����ƽ��;SF:4008111111;STO:400-889-5543;YTO:021-6977888/999");
		codDeliveryBeginTime=properties.getProperty("codDeliveryBeginTime","6:00:00");
		codDeliveryEndTime=properties.getProperty("codDeliveryEndTime","11:00:00");
		timeInterval=Integer.parseInt(properties.getProperty("timeInterval","30"));
		format= properties.getProperty("format","json");
		pageSize= properties.getProperty("pageSize","10");
		vcode= properties.getProperty("vcode","");
		isgenorder = Boolean.parseBoolean(properties.getProperty("isgenorder","true"));
		//��ȡ��Ӧ�Ŀ�ݹ�˾����
		String com[] = company.split(";") ;
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
