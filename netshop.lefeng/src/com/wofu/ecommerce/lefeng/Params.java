package com.wofu.ecommerce.lefeng;

import java.util.Properties;

import com.wofu.common.tools.util.log.Log;

public class Params {
	public static String dbname = "lefeng";
	
	public static String tradecontactid="";

	public static String url = "http://116.90.82.204:3568/suning/cron_shop/";
	
	public static String encoding="UTF-8";

	public static String shopid = "1000013";

	public static String secretKey  = "&^gK&9Bf9&nw";

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
	
	public static String company="STO:82600;YTO:83100;YUNDA:82601";
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "taobao");
		tradecontactid=properties.getProperty("tradecontactid","");
		url = properties.getProperty("url", "http://gw.api.taobao.com/router/rest");
		encoding=properties.getProperty("encoding","GBK");
		shopid = properties.getProperty("shopid", "");
		secretKey = properties.getProperty("secretKey", "");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("username", "��ʿ��ʱ���콢��");
		province= properties.getProperty("province", "�㶫ʡ");
		city= properties.getProperty("city", "������");
		district= properties.getProperty("district", "�����");
		address = properties.getProperty("address", "����·��ʤ��ҵ��2��301");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "����ΰ");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");		
		company = properties.getProperty("company", "STO:82600;YTO:83100;YUNDA:82601");
	}
}

