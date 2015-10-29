package com.wofu.fire.deliveryservice;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	public static String dbname = "best";
	public static String partnerid = "E-WOLF";
	public static String partnerkey = "w85n2jsu9b7634js";
	public static String url = "http://edi-gateway.800best.com/eoms/api/process";
	public static String callbackurl = "http://fxdis.vicp.cc:8002/BestLogisticsService";   //�ص�URL
	public static String encoding = "GBK";
	public static int waittime = 10;
	public static String customercode="85000267";  //�̼ұ���
	public static String interfacesystem="FKWMS";
	public static String serviceversion="1.0";
	public static String msgtype="sync";
	
	public static String linkman="����ΰ";
	public static String province="�㶫ʡ";	
	public static String city="������";	
	public static String district="�����";	
	public static String address="����·��ʤ��ҵ��2��301";
	public static String zipcode="510610";	
	public static String phone="020-38458026";
	public static String mobile="15992409145";
	public static String email="panxingke@163.com";
	public static Boolean isBarcodeId=false;//isBarcodeId
	public static boolean warehouseMulti=false;  //һ��dc�Ĳ�ͬҵ���Ӧ��ͬ�İ�����
	public static boolean isMultiDcToONeWare=false;  //���dc��Ӧһ��������

	
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	public static String warehouseAddressCode;//��̩����ֿ����
	
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "best");
		partnerid=properties.getProperty("partnerid","");
		partnerkey=properties.getProperty("partnerkey","");
		url=properties.getProperty("url","");
		callbackurl=properties.getProperty("callbackurl","");
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		customercode=properties.getProperty("customercode","");
		interfacesystem=properties.getProperty("interfacesystem","");
		serviceversion=properties.getProperty("serviceversion","1.0");
		msgtype=properties.getProperty("msgtype","sync");
		
		province= properties.getProperty("province", "�㶫ʡ");
		city= properties.getProperty("city", "������");
		district= properties.getProperty("district", "");
		address = properties.getProperty("address", "");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");
		email = properties.getProperty("email", "");
		warehouseAddressCode = properties.getProperty("warehouseAddressCode", "");
		isBarcodeId = Boolean.parseBoolean(properties.getProperty("isBarcodeId","false")) ;
		warehouseMulti = Boolean.parseBoolean(properties.getProperty("warehouseMulti","false")) ;
		isMultiDcToONeWare = Boolean.parseBoolean(properties.getProperty("isMultiDcToONeWare","false")) ;
	
	}
}
