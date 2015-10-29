package com.wofu.intf.jw;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	public static String dbname = "jw";
	public static String appSecret = "E-WOLF";
	public static String appkey = "w85n2jsu9b7634js";
	public static String url = "http://edi-gateway.800best.com/eoms/api/process";
	public static String callbackurl = "http://fxdis.vicp.cc:8002/BestLogisticsService";   //�ص�URL
	public static String encoding = "GBK";
	public static int waittime = 10;
	public static String interfacesystem="FKWMS";
	public static String serviceversion="1.0";
	public static String v="sync";
	
	public static String linkman="����ΰ";
	public static String province="�㶫ʡ";	
	public static String city="������";	
	public static String district="�����";	
	public static String address="����·��ʤ��ҵ��2��301";
	public static String zipcode="510610";	
	public static String phone="020-38458026";
	public static String mobile="15992409145";
	public static String email="panxingke@163.com";
	public static String format="json";//isBarcodeId
	public static String ownerCode="";//�������
	public static String ownerName="";//��������
	public static String platFromName="";//ƽ̨����
	public static String shopName="";//���̱���
	public static String skuHgId="";//���ر��
	public static String hgzc="";//�����˲��
	public static String hgxh="";//�������
	public static String vcode="";//��֤��
	public static String EshopEntCode = "";//������ҵ����
	public static String EshopEntName = "";//������ҵ����

	
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	public static String  emscode;//ems��ͻ���
	
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "jw");
		appkey=properties.getProperty("appkey","");
		appSecret=properties.getProperty("appSecret","");
		url=properties.getProperty("url","");
		callbackurl=properties.getProperty("callbackurl","");
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		interfacesystem=properties.getProperty("interfacesystem","");
		serviceversion=properties.getProperty("serviceversion","1.0");
		v=properties.getProperty("v","sync");
		province= properties.getProperty("province", "�㶫ʡ");
		city= properties.getProperty("city", "������");
		district= properties.getProperty("district", "");
		address = properties.getProperty("address", "");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");
		email = properties.getProperty("email", "");
		format = properties.getProperty("format","json") ;
		ownerCode = properties.getProperty("ownerCode","") ;
		ownerName = properties.getProperty("ownerName","") ;
		platFromName = properties.getProperty("platFromName","") ;
		shopName = properties.getProperty("shopName","") ;
		skuHgId = properties.getProperty("skuHgId","") ;
		hgzc = properties.getProperty("hgzc","") ;
		hgxh = properties.getProperty("hgxh","") ;
		vcode = properties.getProperty("vcode","") ;
		EshopEntCode = properties.getProperty("EshopEntCode","") ;
		EshopEntName = properties.getProperty("EshopEntName","") ;
		emscode = properties.getProperty("emscode","") ;
	
	}
}
