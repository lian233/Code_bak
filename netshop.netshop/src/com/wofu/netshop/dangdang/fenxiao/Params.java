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
	public  String username = "当当商城亨母婴专营店";
	public  String address = "广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼";
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
	 * 2013.12.13新增参数
	 */
	public  String session="";
	public  String app_key = "";
	public  String app_Secret = "";
	public  int isDelay;
	public  int tableType;
	public  boolean isgenorderRet;  //是否调用接口订单生成退货订单线程
	public  boolean isUpdateStock=true;    //是否 需要生成客户退货订单
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
		username=properties.getProperty("username","当当商城亨母婴专营店");
		address=properties.getProperty("address","广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼");
		zipcode=properties.getProperty("zipcode","");
		phone=properties.getProperty("phone","");
		mobile=properties.getProperty("mobile","");
		company=properties.getProperty("company","EMS:EMS;HTKY:汇通快运;POST:中国邮政平邮;SF:顺丰速运;STO:申通E物流;YTO:圆通速递");
		companyTel=properties.getProperty("company","EMS:11183;HTKY:021-62963636;POST:中国邮政平邮;SF:4008111111;STO:400-889-5543;YTO:021-6977888/999");
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
		//获取对应的快递公司名称
		String com[] = company.split(";") ;
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			htComCode.put(s[0], s[1]) ;
		}
		//获取快递公司电话
		String comTel[] = companyTel.split(";") ;
		for(int j = 0 ; j < comTel.length ; j++)
		{
			String s[] = comTel[j].split(":") ;
			htComTel.put(s[0], s[1]) ;
		}
	}
}
