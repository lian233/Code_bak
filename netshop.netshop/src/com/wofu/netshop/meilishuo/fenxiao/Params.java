package com.wofu.netshop.meilishuo.fenxiao;
import java.util.Hashtable;
import java.util.Properties;
public class Params {
	// 请求URL地址
	public  String dbname = "";
	public  String pageSize = "30";
	public  String url = "api.open.meilishuo.com";
	public  String tradecontactid = "23";//实际环境要用23
	public  String encoding = "GBK";
	public  int total = 10;
	public  int waittime = 10;
	public  String orderState = "101";
	public  String username = "美丽说";
	public  String address = "";
	public  String zipcode = "";
	public  String phone = "";
	public  String mobile = "";
	public  String company = "" ;
	public  String companyTel = "" ;
	public  int timeInterval = 30 ;
	public  int shopid;
	public  Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public  Hashtable<String, String> htComTel = new Hashtable<String, String>() ;
	public  String token="";
	public  String appKey = "MJ186861556156";
	public  String appsecret = "041c9877e8eaedf774fd611f7909edb0";
	public  String format = "json";       // 响应格式 xml或者json
	public  boolean isNeedDelivery=true;    //是否 需要发货
	public  boolean isGenCustomerOrder=true;    //是否 需要生成客户订单
	public  boolean isgenCustomerRet=true;    //是否 需要生成客户退货订单
	public  boolean isgetOrder;
	public  int jobCount;
	public  void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "");
		url = properties.getProperty("url", "");
		tradecontactid=properties.getProperty("tradecontactid","23");//实际环境要用23
		token=properties.getProperty("Token","sadf");
		appKey=properties.getProperty("AppKey","MJ186861556156");
		appsecret=properties.getProperty("Session","041c9877e8eaedf774fd611f7909edb0");
		total=Integer.parseInt(properties.getProperty("total","10"));
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		orderState=properties.getProperty("orderState","101");
		username=properties.getProperty("name","美丽说").trim();
		address=properties.getProperty("address","广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼");
		zipcode=properties.getProperty("zipcode","");
		phone=properties.getProperty("phone","");
		mobile=properties.getProperty("mobile","");
		company=properties.getProperty("companycode","EMS:ems;HTKY:huitongkuaidi;POST:guangdongyouzhengwuliu;SF:shunfeng;STO:shentong;YTO:yuantong;YUNDA:yunda");
		companyTel=properties.getProperty("company","EMS:11183;HTKY:021-62963636;POST:中国邮政平邮;SF:4008111111;STO:400-889-5543;YTO:021-6977888/999");
		timeInterval=Integer.parseInt(properties.getProperty("timeInterval","30"));
		shopid=Integer.parseInt(properties.getProperty("id","0"));
		format= properties.getProperty("format","json");
		pageSize= properties.getProperty("pageSize","10");
		isNeedDelivery = Boolean.valueOf(properties.getProperty("isNeedDelivery", "0").equals("0")?"false":"true").booleanValue();
		isGenCustomerOrder = Boolean.valueOf(properties.getProperty("isGenCustomerOrder", "0").equals("0")?"false":"true").booleanValue();
		isgenCustomerRet = Boolean.valueOf(properties.getProperty("isgenCustomerRet", "0").equals("0")?"false":"true").booleanValue();
		isgetOrder = Boolean.valueOf(properties.getProperty("isgetOrder", "1").equals("0")?"false":"true").booleanValue();
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
			String s[] = comTel[j].split(":");
			htComTel.put(s[0], s[1]) ;
		}
	}
}
