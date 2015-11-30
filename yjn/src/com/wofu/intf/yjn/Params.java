package com.wofu.intf.yjn;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	public static String dbname = "best";
	public static String url = "http://edi-gateway.800best.com/eoms/api/process";
	public static String callbackurl = "http://fxdis.vicp.cc:8002/BestLogisticsService";   //�ص�URL
	public static String encoding = "GBK";
	public static int waittime = 10;
	public static String customercode="85000267";  //�̼ұ���
	public static String serviceversion="1.0";
	public static String SenderId="";//������
	public static String ReceiverId="";//������
	public static String UserNo="";//�û���
	public static String Password="";//����
	public static String CustomsCode="8012";//���ش���
	public static String biz_type_code="";//ҵ������   ֱ�����ڣ�I10,������˰���ڣ�I20
	//public static String biz_type_code = "";
	public static String EshopEntCode = "";//������ҵ����
	public static String EshopEntName = "";//������ҵ����
	public static String PaymentEntCode = "";//֧����ҵ����
	public static String PaymentEntName = "";//֧����ҵ����
	//public static String DESP_ARRI_COUNTRY_CODE = "";
	//public static String SHIP_TOOL_CODE = "";
	//public static String CURRENCY_CODE = "";
	public static String SortLineID = "";	//
	public static String linkman="����ΰ";
	public static String province="�㶫ʡ";	
	public static String city="������";	
	public static String district="�����";	
	public static String address="����·��ʤ��ҵ��2��301";
	public static String zipcode="510610";	
	public static String phone="020-38458026";
	public static String mobile="15992409145";
	public static String email="panxingke@163.com";
	public static String company="";

	
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "best");
		url=properties.getProperty("url","");
		callbackurl=properties.getProperty("callbackurl","");
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		customercode=properties.getProperty("customercode","");
		serviceversion=properties.getProperty("serviceversion","1.0");
		province= properties.getProperty("province", "�㶫ʡ");
		city= properties.getProperty("city", "������");
		district= properties.getProperty("district", "");
		address = properties.getProperty("address", "");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");
		ReceiverId = properties.getProperty("ReceiverId", "CQITC");
		UserNo = properties.getProperty("UserNo", "1234567891");
		SenderId = properties.getProperty("SenderId", "1234567891");
		Password = properties.getProperty("Password", "1234567891");
		CustomsCode = properties.getProperty("CustomsCode", "8012");
		EshopEntCode = properties.getProperty("EshopEntCode", "1234567891");
		EshopEntName = properties.getProperty("EshopEntName", "����������Ϣ�Ƽ����޹�˾");
		PaymentEntCode = properties.getProperty("PaymentEntCode", "1234567891");
		PaymentEntName = properties.getProperty("PaymentEntName", "����������Ϣ�Ƽ����޹�˾");
		biz_type_code = properties.getProperty("biz_type_code", "I20");
		
		/*
		biz_type_code = properties.getProperty("biz_type_code", "I20");
		DESP_ARRI_COUNTRY_CODE = properties.getProperty("DESP_ARRI_COUNTRY_CODE", "116");
		SHIP_TOOL_CODE = properties.getProperty("SHIP_TOOL_CODE", "Y");
		CURRENCY_CODE = properties.getProperty("CURRENCY_CODE", "142");*/
		SortLineID = properties.getProperty("SortlineID", "SORTLINE01");//�ּ���ID SORTLINE01�������̲�ո�  SORTLINE02�������������� SORTLINE03�������̲ˮ��"
		email = properties.getProperty("email", "");
		company = properties.getProperty("company", "");
	
	}
}
