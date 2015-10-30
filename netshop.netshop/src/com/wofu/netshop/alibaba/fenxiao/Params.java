package com.wofu.netshop.alibaba.fenxiao;
import java.util.Properties;
public class Params {
	public static String dbname = "alibaba";
	
	public  String tradecontactid="";
	
	public  String namespace ="cn.alibaba.open";            //�����ռ�
	
	public  String url = "http://gw.open.1688.com/openapi/";
	
	public  String host="gw.open.1688.com";
	
	public  String encoding="UTF-8";
	
	public  int version=1;
	
	public  String redirect_uri="http://163.com";
	
	public  String requestmodel="param2";
	
	public  String sellerMemberId="datangsilu";
	
	public  String appkey = "1008771";
	
	public  String secretKey  = "AmUvfEqcm2U";
	public  String refresh_token="451136cd-c184-4b4c-ab5b-d4c05738fa93";
	                                  //b36e4aff-19aa-4695-aa6a-d193ddb6c869
	public int jobCount;
	public boolean isGenCustomerOrder;
	public boolean isgetOrder;
	public boolean isNeedDelivery;
	public boolean isgenCustomerRet;

	public int shopid;

	
	public  String company = "�°�����:41;�°�����:42;����½��:45;��ػ���:81;����:5;" +
										"��ͨ:3;�ϴ�:4;��ͨ:1;Բͨ:2;˳��:6;EMS���ÿ��:354;" +
										"EMS:7;��������:44;�Ѽ�����:43;��ͨ����:352;լ����:106;"+
										"������:107;CCES���:108;UPS:109;DHL:110;��������:122;" +
										"��ƽ����:142;��������:163;��������:164;ʢ������:182;����֮��:202;" +
										"Զ������:222;��ʱ��ͨ��:242;���ٴ�����:262;��ͨ���:107726;����:8;";
	public static String token="";
	
	public static int total=10;
	
	public static int waittime = 10;
	
	public static String username = "��ʿ��ʱ���콢��";
	
	public static String province="�㶫ʡ";
	
	public static String city="������";
	
	public static String district="�����";
	
	public static String address="����·��ʤ��ҵ��2��301";
	
	public static String zipcode="510610";
	
	public static String linkman="����ΰ";
	
	public static String phone="020-38458026";

	public static String mobile="15992409145";
	public static int isDelay=0;    //���ɶ������ʱ�����������Ƿ��ӳ�����
	public static int tableType=0;    //���ɶ������ʱ���������Դ����ʱ����Դ��

	public static boolean isgenorder;
	

	public Params() {
	}

	public void init(Properties properties) {
		dbname = properties.getProperty("dbname", "alibaba");
		shopid = Integer.parseInt(properties.getProperty("id", "0"));
		tradecontactid=properties.getProperty("tradecontactid","");
		url = properties.getProperty("url", "http://gw.open.1688.com/openapi");
		encoding=properties.getProperty("encoding","GBK");
		appkey = properties.getProperty("AppKey", "1008771");
		company = properties.getProperty("company", "");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("name", "��ʿ��ʱ���콢��").trim();
		province= properties.getProperty("province", "�㶫ʡ");
		city= properties.getProperty("city", "������");
		district= properties.getProperty("district", "�����");
		address = properties.getProperty("address", "����·��ʤ��ҵ��2��301");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "����ΰ");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");	
		secretKey = properties.getProperty("Session", "AmUvfEqcm2U");	
		refresh_token = properties.getProperty("refreshtoken", "");	
		redirect_uri = properties.getProperty("redirect_uri", "");	
		sellerMemberId = properties.getProperty("sellerMemberId", "");	
		isGenCustomerOrder = Boolean.valueOf(properties.getProperty("isGenCustomerOrder", "0").equals("0")?"false":"true").booleanValue();
		isgenCustomerRet = Boolean.valueOf(properties.getProperty("isgenCustomerRet", "0").equals("0")?"false":"true").booleanValue();
		isgetOrder = Boolean.valueOf(properties.getProperty("isgetOrder", "1").equals("0")?"false":"true").booleanValue();
		isNeedDelivery = Boolean.valueOf(properties.getProperty("isNeedDelivery", "0").equals("0")?"false":"true").booleanValue();
	}
}

