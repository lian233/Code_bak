package com.wofu.netshop.jingdong.fenxiao;

import java.util.Properties;

public class Params {
	public  String dbname = "360buy";
	
	public  String tradecontactid="6";

	public  String wsurl = "http://gw.shop.360buy.com/routerjson";
	
	public  String encoding="utf-8";
		
	public  int total=10;
	
	public  int waittime = 10;
	
	public  String username = "��ʿ��ʱ�������";
	
	public  String address="�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥";
	
	public  String zipcode="510610";
	
	public  String linkman="";
	public  int jobCount;
	public  boolean isgenCustomerRet=true;    //�Ƿ� ��Ҫ���ɿͻ��˻�����
	
	public  String phone="020-38458026";

	public  String mobile="";
	
	public  String delivery = "" ;
	
	public  String companycode = "" ;
	
	public  String SERVER_URL = "" ;
	
	public  String token = "" ;
	
	public  String appKey = "" ;
	
	public  String appSecret = "" ;
	public  boolean isNeedGetDeliverysheetid = true ;

	public  boolean isLBP = false ;
	public  boolean jdkdNeedDelivery = true ;  //������������Ķ����Ƿ�Ҫ�Լ�����
	//������ݵ��̼ұ���--�����̼Һ�̨�ı���
	public  String JBDCustomerCode="";
	public  int isDelay=0;    //���ɶ������ʱ�����������Ƿ��ӳ�����
	public  int tableType=0;    //���ɶ������ʱ���������Դ����ʱ����Դ��
	public  boolean isNeedDelivery=true;    //�Ƿ� ��Ҫ����
	public  boolean isGenCustomerOrder=true;    //�Ƿ� ��Ҫ���ɿͻ�����
	//public static boolean isgenCustomerRet=true;    //�Ƿ� ��Ҫ���ɿͻ��˻�����
	public  boolean isUpdateStock=true;    //�Ƿ� ��Ҫ���ɿͻ��˻�����
	public  boolean isgetOrder=true;    //�Ƿ� ��Ҫ���ض���
	public  boolean isgetItem=true;    //�Ƿ� ��Ҫ ��Ʒ����


	public  int shopid;    //������ID
	


	public  void init(Properties properties) {
		dbname = properties.getProperty("dbname", "360buy");
		tradecontactid=properties.getProperty("tradecontactid","");
		wsurl = properties.getProperty("wsurl", "http://gw.shop.360buy.com/routerjson");
		encoding=properties.getProperty("encoding","GBK");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("name", "��ʿ��ʱ�������").trim();
		linkman = properties.getProperty("linkman", "��ϵ��");
		address = properties.getProperty("address", "�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥");
		zipcode = properties.getProperty("zipcode", "510610");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "");
		companycode = properties.getProperty("companycode","EMS:465;SF:467;YTO:463") ;
		SERVER_URL = properties.getProperty("SERVER_URL","http://gw.api.360buy.com/routerjson") ;
		token = properties.getProperty("Token","") ;
		appKey = properties.getProperty("AppKey","") ;
		appSecret = properties.getProperty("Session","") ;
		JBDCustomerCode = properties.getProperty("JBDCustomerCode","") ;
		isLBP = Boolean.parseBoolean(properties.getProperty("isLBP","false")) ;
		jdkdNeedDelivery = Boolean.parseBoolean(properties.getProperty("jdkdNeedDelivery","true")) ;
		isNeedGetDeliverysheetid = Boolean.parseBoolean(properties.getProperty("isNeedGetDeliverysheetid","true")) ;
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		shopid = Integer.parseInt(properties.getProperty("id", "1"));
		isUpdateStock = Boolean.valueOf(properties.getProperty("isUpdateStock", "0").equals("0")?"false":"true").booleanValue();
		isNeedDelivery = Boolean.valueOf(properties.getProperty("isNeedDelivery", "1").equals("0")?"false":"true").booleanValue();
		isGenCustomerOrder = Boolean.valueOf(properties.getProperty("isGenCustomerOrder", "1").equals("0")?"false":"true").booleanValue();
		isgetOrder = Boolean.valueOf(properties.getProperty("isgetOrder", "1").equals("0")?"false":"true").booleanValue();
		isgetItem = Boolean.valueOf(properties.getProperty("isgetItem", "1").equals("0")?"false":"true").booleanValue();
		isgenCustomerRet = Boolean.valueOf(properties.getProperty("isgenCustomerRet", "0").equals("0")?"false":"true").booleanValue();
		isUpdateStock = Boolean.valueOf(properties.getProperty("isUpdateStock", "0").equals("0")?"false":"true").booleanValue();
		
	}
}

