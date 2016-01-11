package com.wofu.intf.dtc;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	public static String dbname = "best";
	public static String url = "http://edi-gateway.800best.com/eoms/api/process";
	public static String callbackurl = "http://fxdis.vicp.cc:8002/BestLogisticsService";   //回调URL
	public static String encoding = "GBK";
	public static int waittime = 10;
	public static String customercode="85000267";  //商家编码
	public static String serviceversion="1.0";
	public static String SenderId="";//发送者
	public static String ReceiverId="";//接收者
	public static String UserNo="";//用户名
	public static String Password="";//密码
	public static String CustomsCode="8012";//海关代码
	public static String biz_type_code="";//业务类型   直购进口：I10,网购保税进口：I20
	//public static String biz_type_code = "";
	public static String EshopEntCode = "";//电商企业代码
	public static String EshopEntName = "";//电商企业名称
	public static String PaymentEntCode = "";//支付企业代码
	public static String PaymentEntName = "";//支付企业名称
	//public static String DESP_ARRI_COUNTRY_CODE = "";
	//public static String SHIP_TOOL_CODE = "";
	//public static String CURRENCY_CODE = "";
	public static String SortLineID = "";	//
	public static String linkman="张利伟";
	public static String province="广东省";	
	public static String city="广州市";	
	public static String district="天河区";	
	public static String address="车陂路大胜工业区2栋301";
	public static String zipcode="510610";	
	public static String phone="020-38458026";
	public static String mobile="15992409145";
	public static String email="panxingke@163.com";
	public static String company="";
	
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	public static String ship_tool_code="Y";
	
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "best");
		url=properties.getProperty("url","");
		callbackurl=properties.getProperty("callbackurl","");
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		customercode=properties.getProperty("customercode","");
		serviceversion=properties.getProperty("serviceversion","1.0");
		province= properties.getProperty("province", "广东省");
		city= properties.getProperty("city", "广州市");
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
		ship_tool_code = properties.getProperty("ship_tool_code", "1234567891");
		EshopEntName = properties.getProperty("EshopEntName", "无锡火蚁信息科技有限公司");
		PaymentEntCode = properties.getProperty("PaymentEntCode", "1234567891");
		PaymentEntName = properties.getProperty("PaymentEntName", "无锡火蚁信息科技有限公司");
		biz_type_code = properties.getProperty("biz_type_code", "I20");
		
		/*
		biz_type_code = properties.getProperty("biz_type_code", "I20");
		DESP_ARRI_COUNTRY_CODE = properties.getProperty("DESP_ARRI_COUNTRY_CODE", "116");
		SHIP_TOOL_CODE = properties.getProperty("SHIP_TOOL_CODE", "Y");
		CURRENCY_CODE = properties.getProperty("CURRENCY_CODE", "142");*/
		SortLineID = properties.getProperty("SortlineID", "SORTLINE01");//分拣线ID SORTLINE01：代表寸滩空港  SORTLINE02：代表重庆西永 SORTLINE03：代表寸滩水港"
		email = properties.getProperty("email", "");
		company = properties.getProperty("company", "");
	
	}
}
