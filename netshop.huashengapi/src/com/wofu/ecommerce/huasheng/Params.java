package com.wofu.ecommerce.huasheng;

import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;


public class Params {
	//api所需参数(xml)
	public static String apiurl = "http://seryhappy.imwork.net/wscenter/UpdateServlet";
	public static String vcode = "530102019";
	public static String VCODE = "402880a335c807030135c818190f0001";	//库存总部验证码,固定
	
	//402880a335c807030135c818190f0001  测试总部验证码
	//ff8080814ebaaa14014ed27a30560b72 正式总部验证码
	
	//接口程序配置(xml)
	public static String dbname = "huasheng";
	public static String tradecontactid = "27";	//数据库中:select * from TradeContacts,正式的时候需要添加一条记录进去使用
	public static int isDelay = 0;    //生成订单表的时候其它数据是否延迟生成
	public static int tableType = 0;    //生成订单表的时候的数据来源是临时表还是源表
	public static int waittime = 10;	//等待时间
	public static Date startTime = new Date();	//接口指定启动的时间
	//商家基本信息(xml)
	public static String username = "测试";
	public static String address = "测试";
	public static String zipcode = "510000";
	public static String phone = "020-85630529";
	public static String mobile = "";
	//快递(xml)
	public static Hashtable<String, String> htPostCompany = new Hashtable<String, String>();
	
	public static void init(Properties properties)
	{
		Log.info("初始化参数");
		//从xml读取参数
		apiurl = properties.getProperty("apiurl","");
		vcode = properties.getProperty("vcode","");
		tradecontactid = properties.getProperty("tradecontactid","");
		dbname = properties.getProperty("dbname", "huasheng");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		waittime = Integer.valueOf(properties.getProperty("apiurl","0"));
		
		//商家基本信息(xml)
		username = properties.getProperty("username","");
		address = properties.getProperty("address","");
		zipcode = properties.getProperty("zipcode","");
		phone = properties.getProperty("phone","");
		mobile = properties.getProperty("mobile","");
		
		//快递对照表(如: "SF:SF;ZTO:zto,..."  花生哪里规定快递公司要用代号,为了方便设置所以用了对照表来查)
		String postcompany = properties.getProperty("PostCompany", "");
		if(!postcompany.equals(""))
		{
			String com[] = postcompany.split(";") ;
			for(int i = 0 ; i < com.length ; i++)
			{
				String s[] = com[i].split(":") ;
				htPostCompany.put(s[0], s[1]) ;
			}
		}
		
		//接口设定启动时间
		String tmp = properties.getProperty("startTime", "");
		Log.info("接口设定启动时间:" + tmp);
		try {
			if(!tmp.isEmpty())
				startTime = Formatter.parseDate(tmp, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			Log.info("转换时间出错!");
			startTime = new Date();
		}
		
		Log.info("初始化参数完成");
	}
}
