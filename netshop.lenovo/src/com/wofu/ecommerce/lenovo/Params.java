package com.wofu.ecommerce.lenovo;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	// ����URL��ַ
	public static String dbname = "suning";
	public static String pageSize = "10";
	public static String url = "http://3.14nbd.com/to_delivery/";
	public static String tradecontactid = "24";
	public static String encoding = "GBK";
	public static int total = 10;
	public static int waittime = 10;
	public static String orderState = "101";
	public static String username = "������˵�";
	public static String address = "�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥";
	public static String zipcode = "510610";
	public static String phone = "020-38458026";
	public static String mobile = "";
	public static String company = "" ;
	public static String companyTel = "" ;
	public static String codDeliveryBeginTime="" ;
	public static String codDeliveryEndTime="" ;
	public static int timeInterval = 30 ;
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	public static String vcode="H2U0Wfn7N9RYSE9i";
	public static int isDelay;
	public static int tableType;
	
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "dangdang");
		tradecontactid=properties.getProperty("tradecontactid","10");
		total=Integer.parseInt(properties.getProperty("total","10"));
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		orderState=properties.getProperty("orderState","101");
		username=properties.getProperty("username","��ʿ�ᵱ���̳�");
		address=properties.getProperty("address","�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥");
		zipcode=properties.getProperty("zipcode","");
		phone=properties.getProperty("phone","");
		mobile=properties.getProperty("mobile","");
		company=properties.getProperty("companycode","EMS:EMS;HTKY:��ͨ����;POST:�й�����ƽ��;SF:˳������;STO:��ͨE����;YTO:Բͨ�ٵ�");
		companyTel=properties.getProperty("company","EMS:11183;HTKY:021-62963636;POST:�й�����ƽ��;SF:4008111111;STO:400-889-5543;YTO:021-6977888/999");
		codDeliveryBeginTime=properties.getProperty("codDeliveryBeginTime","6:00:00");
		codDeliveryEndTime=properties.getProperty("codDeliveryEndTime","11:00:00");
		timeInterval=Integer.parseInt(properties.getProperty("timeInterval","30"));
		pageSize= properties.getProperty("pageSize","10");
		vcode= properties.getProperty("vcode","");
		url= properties.getProperty("url","");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
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
