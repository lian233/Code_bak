package com.wofu.netshop.jingdong;

import java.util.Properties;

public class Params {
	public static String dbname = "360buy";
	
	public static String tradecontactid="6";

	public static String wsurl = "http://gw.shop.360buy.com/routerjson";
	
	public static String encoding="utf-8";
		
	public static int total=10;
	
	public static int waittime = 10;
	
	public static String username = "迪士尼时尚生活馆";
	
	public static String address="广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼";
	
	public static String zipcode="510610";
	
	public static String linkman="";
	public static int jobCount;
	public static boolean isgenCustomerRet=true;    //是否 需要生成客户退货订单
	
	public static String phone="020-38458026";

	public static String mobile="";
	
	public static String delivery = "" ;
	
	public static String companycode = "" ;
	
	public static String SERVER_URL = "" ;
	
	public static String token = "" ;
	
	public static String appKey = "" ;
	
	public static String appSecret = "" ;
	public static boolean isNeedGetDeliverysheetid = true ;

	public static boolean isLBP = false ;
	public static boolean jdkdNeedDelivery = true ;  //京东货到付款的订单是否要自己发货
	//京东快递的商家编码--并非商家后台的编码
	public static String JBDCustomerCode="";
	public static int isDelay=0;    //生成订单表的时候其它数据是否延迟生成
	public static int tableType=0;    //生成订单表的时候的数据来源是临时表还是源表
	public static boolean isNeedDelivery=true;    //是否 需要发货
	public static boolean isGenCustomerOrder=true;    //是否 需要生成客户订单
	//public static boolean isgenCustomerRet=true;    //是否 需要生成客户退货订单
	public static boolean isUpdateStock=true;    //是否 需要生成客户退货订单
	public static boolean isgetOrder=true;    //是否 需要下载订单
	public static boolean isgetItem=true;    //是否 需要 商品资料

	public static boolean isGetOrders=true;
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "360buy");
		tradecontactid=properties.getProperty("tradecontactid","");
		wsurl = properties.getProperty("wsurl", "http://gw.shop.360buy.com/routerjson");
		encoding=properties.getProperty("encoding","GBK");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("username", "迪士尼时尚生活馆");
		linkman = properties.getProperty("linkman", "联系人");
		address = properties.getProperty("address", "广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼");
		zipcode = properties.getProperty("zipcode", "510610");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "");
		companycode = properties.getProperty("companycode","EMS:465;SF:467;YTO:463") ;
		SERVER_URL = properties.getProperty("SERVER_URL","http://gw.shop.360buy.com/routerjson") ;
		token = properties.getProperty("token","") ;
		appKey = properties.getProperty("appKey","") ;
		appSecret = properties.getProperty("appSecret","") ;
		JBDCustomerCode = properties.getProperty("JBDCustomerCode","") ;
		isLBP = Boolean.parseBoolean(properties.getProperty("isLBP","false")) ;
		jdkdNeedDelivery = Boolean.parseBoolean(properties.getProperty("jdkdNeedDelivery","true")) ;
		isNeedGetDeliverysheetid = Boolean.parseBoolean(properties.getProperty("isNeedGetDeliverysheetid","true")) ;
		isgenCustomerRet = Boolean.parseBoolean(properties.getProperty("isgenCustomerRet","true")) ;
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
	}
}

