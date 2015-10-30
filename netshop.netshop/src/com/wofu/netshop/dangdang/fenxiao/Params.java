package com.wofu.netshop.dangdang.fenxiao;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	public  String dbname = "dangdang";
	public  final String url = "http://api.open.dangdang.com/openapi/rest?v=1.0";
	public  String tradecontactid = "10";
	public  String encoding = "GBK";
	public  int total = 10;
	public  int waittime = 10;
	public  String sendMode = "9999";
	public  String orderState = "101";
	public  String username = "�����̳Ǻ�ĸӤרӪ��";
	public  String address = "�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥";
	public  String zipcode = "510610";
	public  String phone = "020-38458026";
	public  String mobile = "";
	public  String company = "" ;
	public  String companyTel = "" ;
	public  String codDeliveryBeginTime="" ;
	public  String codDeliveryEndTime="" ;
	public  int timeInterval = 30 ;
	public  boolean isgetOrder = false;
	public  boolean isGenCustomerOrder = false;
	public  boolean isgenCustomerRet = false;
	public   int shopid;
	public  boolean isNeedDelivery = true;
	public int jobCount =1;
	public  Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public  Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	/**
	 * 2013.12.13��������
	 */
	public  String session="";
	public  String app_key = "";
	public  String app_Secret = "";
	public  int isDelay;
	public  int tableType;
	public  boolean isgenorderRet;  //�Ƿ���ýӿڶ��������˻������߳�
	public  boolean isUpdateStock=true;    //�Ƿ� ��Ҫ���ɿͻ��˻�����
	public  void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "dangdang");
		//url=properties.getProperty("url","http://api.open.dangdang.com/openapi/rest?v=1.0");
		tradecontactid=properties.getProperty("tradecontactid","10");
		encoding=properties.getProperty("encoding","GBK");
		session=properties.getProperty("Session","1");
		app_key=properties.getProperty("AppKey","1");
		app_Secret=properties.getProperty("app_Secret","D1E9A9D5C71EA9B3530825D3D6E2C424");
		total=Integer.parseInt(properties.getProperty("total","10"));
		shopid = Integer.parseInt(properties.getProperty("id", "1"));
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		sendMode=properties.getProperty("sendMode","9999");
		orderState=properties.getProperty("orderState","101");
		username=properties.getProperty("username","�����̳Ǻ�ĸӤרӪ��");
		address=properties.getProperty("address","�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥");
		zipcode=properties.getProperty("zipcode","");
		phone=properties.getProperty("phone","");
		mobile=properties.getProperty("mobile","");
		company=properties.getProperty("company","EMS:EMS;HTKY:��ͨ����;POST:�й�����ƽ��;SF:˳������;STO:��ͨE����;YTO:Բͨ�ٵ�");
		companyTel=properties.getProperty("company","EMS:11183;HTKY:021-62963636;POST:�й�����ƽ��;SF:4008111111;STO:400-889-5543;YTO:021-6977888/999");
		codDeliveryBeginTime=properties.getProperty("codDeliveryBeginTime","6:00:00");
		codDeliveryEndTime=properties.getProperty("codDeliveryEndTime","11:00:00");
		timeInterval=Integer.parseInt(properties.getProperty("timeInterval","30"));
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isgenorderRet = Boolean.parseBoolean(properties.getProperty("isgenorderRet", "true"));
		
		isgetOrder = Boolean.valueOf(properties.getProperty("isgetOrder", "1").equals("0")?"false":"true").booleanValue();
		isGenCustomerOrder = Boolean.valueOf(properties.getProperty("isGenCustomerOrder", "1").equals("0")?"false":"true").booleanValue();
		isgenCustomerRet = Boolean.valueOf(properties.getProperty("isgenCustomerRet", "0").equals("0")?"false":"true").booleanValue();
		isNeedDelivery = Boolean.valueOf(properties.getProperty("isNeedDelivery", "0").equals("0")?"false":"true").booleanValue();
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
