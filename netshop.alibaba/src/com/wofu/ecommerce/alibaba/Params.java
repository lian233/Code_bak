package com.wofu.ecommerce.alibaba;

import java.util.Properties;

import com.wofu.common.tools.util.log.Log;

public class Params {
	public static String dbname = "alibaba";
	
	public static String tradecontactid="";
	
	public static String namespace ="cn.alibaba.open";            //命名空间
	
	public static String url = "http://gw.open.1688.com/openapi/";
	
	public static String host="gw.open.1688.com";
	
	public static String encoding="UTF-8";
	
	public static int version=1;
	
	public static String redirect_uri="http://163.com";
	
	public static String requestmodel="param2";
	
	public static String sellerMemberId="datangsilu";
	
	public static String appkey = "1008771";
	
	public static String secretKey  = "AmUvfEqcm2U";
	public static String refresh_token="451136cd-c184-4b4c-ab5b-d4c05738fa93";
	                                  //b36e4aff-19aa-4695-aa6a-d193ddb6c869
	
	public static String company = "德邦物流:41;新邦物流:42;大田陆运:45;天地华宇:81;天天:5;" +
										"中通:3;韵达:4;申通:1;圆通:2;顺丰:6;EMS经济快递:354;" +
										"EMS:7;中铁快运:44;佳吉快运:43;汇通快运:352;宅急送:106;"+
										"联邦快递:107;CCES快递:108;UPS:109;DHL:110;苏粤货运:122;" +
										"贵平物流:142;佳怡物流:163;中铁物流:164;盛辉物流:182;城市之星:202;" +
										"远成物流:222;新时代通成:242;共速达物流:262;国通快递:107726;其它:8;";
	public static String token="";
	
	public static int total=10;
	
	public static int waittime = 10;
	
	public static String username = "迪士尼时尚旗舰店";
	
	public static String province="广东省";
	
	public static String city="广州市";
	
	public static String district="天河区";
	
	public static String address="车陂路大胜工业区2栋301";
	
	public static String zipcode="510610";
	
	public static String linkman="张利伟";
	
	public static String phone="020-38458026";

	public static String mobile="15992409145";
	public static int isDelay=0;    //生成订单表的时候其它数据是否延迟生成
	public static int tableType=0;    //生成订单表的时候的数据来源是临时表还是源表

	public static boolean isgenorder;
	

	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "alibaba");
		tradecontactid=properties.getProperty("tradecontactid","");
		url = properties.getProperty("url", "http://gw.open.1688.com/openapi");
		encoding=properties.getProperty("encoding","GBK");
		appkey = properties.getProperty("appkey", "1008771");
		company = properties.getProperty("company", "");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("username", "迪士尼时尚旗舰店");
		province= properties.getProperty("province", "广东省");
		city= properties.getProperty("city", "广州市");
		district= properties.getProperty("district", "天河区");
		address = properties.getProperty("address", "车陂路大胜工业区2栋301");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "张利伟");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");	
		secretKey = properties.getProperty("secretKey", "AmUvfEqcm2U");	
		refresh_token = properties.getProperty("refresh_token", "");	
		redirect_uri = properties.getProperty("redirect_uri", "");	
		sellerMemberId = properties.getProperty("sellerMemberId", "");	
		isgenorder = Boolean.parseBoolean(properties.getProperty("isgenorder", "true"));
	}
}

