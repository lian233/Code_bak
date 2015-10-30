package com.wofu.netshop.jingdong;

import java.util.Properties;

public class Params {
	public static String dbname = "360buy";
	
	public static String tradecontactid="6";

	public static String wsurl = "http://gw.shop.360buy.com/routerjson";
	
	public static String encoding="utf-8";
		
	public static int total=10;
	
	public static int waittime = 10;
	
	public static String username = "��ʿ��ʱ�������";
	
	public static String address="�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥";
	
	public static String zipcode="510610";
	
	public static String linkman="";
	public static int jobCount;
	public static boolean isgenCustomerRet=true;    //�Ƿ� ��Ҫ���ɿͻ��˻�����
	
	public static String phone="020-38458026";

	public static String mobile="";
	
	public static String delivery = "" ;
	
	public static String companycode = "" ;
	
	public static String SERVER_URL = "" ;
	
	public static String token = "" ;
	
	public static String appKey = "" ;
	
	public static String appSecret = "" ;
	public static boolean isNeedGetDeliverysheetid = true ;

	public static boolean isLBP = false ;
	public static boolean jdkdNeedDelivery = true ;  //������������Ķ����Ƿ�Ҫ�Լ�����
	//������ݵ��̼ұ���--�����̼Һ�̨�ı���
	public static String JBDCustomerCode="";
	public static int isDelay=0;    //���ɶ������ʱ�����������Ƿ��ӳ�����
	public static int tableType=0;    //���ɶ������ʱ���������Դ����ʱ����Դ��
	public static boolean isNeedDelivery=true;    //�Ƿ� ��Ҫ����
	public static boolean isGenCustomerOrder=true;    //�Ƿ� ��Ҫ���ɿͻ�����
	//public static boolean isgenCustomerRet=true;    //�Ƿ� ��Ҫ���ɿͻ��˻�����
	public static boolean isUpdateStock=true;    //�Ƿ� ��Ҫ���ɿͻ��˻�����
	public static boolean isgetOrder=true;    //�Ƿ� ��Ҫ���ض���
	public static boolean isgetItem=true;    //�Ƿ� ��Ҫ ��Ʒ����

	public static boolean isGetOrders=true;
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "360buy");
		tradecontactid=properties.getProperty("tradecontactid","");
		wsurl = properties.getProperty("wsurl", "http://gw.shop.360buy.com/routerjson");
		encoding=properties.getProperty("encoding","GBK");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("username", "��ʿ��ʱ�������");
		linkman = properties.getProperty("linkman", "��ϵ��");
		address = properties.getProperty("address", "�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥");
		zipcode = properties.getProperty("zipcode", "510610");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "");
		companycode = properties.getProperty("companycode","EMS:465;SF:467;YTO:463") ;
		SERVER_URL = properties.getProperty("SERVER_URL","http://gw.shop.360buy.com/routerjson") ;
		token = properties.getProperty("token","") ;
		appKey = properties.getProperty("appKey","") ;
		appSecret = properties.getProperty("appSecret","") ;
		JBDCustomerCode = properties.getProperty("JBDCustomerCode","") ;
		isLBP = Boolean.parseBoolean(properties.getProperty("isLBP","false")) ;
		jdkdNeedDelivery = Boolean.parseBoolean(properties.getProperty("jdkdNeedDelivery","true")) ;
		isNeedGetDeliverysheetid = Boolean.parseBoolean(properties.getProperty("isNeedGetDeliverysheetid","true")) ;
		isgenCustomerRet = Boolean.parseBoolean(properties.getProperty("isgenCustomerRet","true")) ;
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
	}
}

