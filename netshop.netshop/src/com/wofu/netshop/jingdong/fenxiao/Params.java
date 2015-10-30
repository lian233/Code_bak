package com.wofu.netshop.jingdong.fenxiao;

import java.util.Properties;

public class Params {
	public  String dbname = "360buy";
	
	public  String tradecontactid="6";

	public  String wsurl = "http://gw.shop.360buy.com/routerjson";
	
	public  String encoding="utf-8";
		
	public  int total=10;
	
	public  int waittime = 10;
	
	public  String username = "迪士尼时尚生活馆";
	
	public  String address="广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼";
	
	public  String zipcode="510610";
	
	public  String linkman="";
	public  int jobCount;
	public  boolean isgenCustomerRet=true;    //是否 需要生成客户退货订单
	
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
	public  boolean jdkdNeedDelivery = true ;  //京东货到付款的订单是否要自己发货
	//京东快递的商家编码--并非商家后台的编码
	public  String JBDCustomerCode="";
	public  int isDelay=0;    //生成订单表的时候其它数据是否延迟生成
	public  int tableType=0;    //生成订单表的时候的数据来源是临时表还是源表
	public  boolean isNeedDelivery=true;    //是否 需要发货
	public  boolean isGenCustomerOrder=true;    //是否 需要生成客户订单
	//public static boolean isgenCustomerRet=true;    //是否 需要生成客户退货订单
	public  boolean isUpdateStock=true;    //是否 需要生成客户退货订单
	public  boolean isgetOrder=true;    //是否 需要下载订单
	public  boolean isgetItem=true;    //是否 需要 商品资料


	public  int shopid;    //分销店ID
	


	public  void init(Properties properties) {
		dbname = properties.getProperty("dbname", "360buy");
		tradecontactid=properties.getProperty("tradecontactid","");
		wsurl = properties.getProperty("wsurl", "http://gw.shop.360buy.com/routerjson");
		encoding=properties.getProperty("encoding","GBK");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("name", "迪士尼时尚生活馆").trim();
		linkman = properties.getProperty("linkman", "联系人");
		address = properties.getProperty("address", "广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼");
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

