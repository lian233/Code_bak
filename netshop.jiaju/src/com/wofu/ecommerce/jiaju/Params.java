package com.wofu.ecommerce.jiaju;

import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;

//家居就参数类
public class Params {
	//变量定义
	public static String username = "";	//商城名称(如:家居就、贝贝怡苏宁商城...)
	public static String dbname = "";	//数据库连接池
	public static int waittime = 10;
	public static final String url = "http://www.jiaju.com/openapi/";	//接口地址
	public static String partner_id = "";	//帐号
	public static String Partner_pwd = "";	//密码
	public static String tradecontactid = "7";		//数据库中:select * from TradeContacts,正式的时候需要添加一条记录进去使用
	public static String company = "";		//快递公司对应表
	public static int isDelay=0;    //生成订单表的时候其它数据是否延迟生成
	public static int tableType=0;    //生成订单表的时候的数据来源是临时表还是源表
	public static Date startTime = new Date();
	public static Date orderAddTime = null;
	
	//从配置文件读取参数内容
	public static void init(Properties properties)
	{
		username=properties.getProperty("username","家居就");
		dbname =  properties.getProperty("dbname", "jiaju");
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		partner_id = properties.getProperty("partner_id", "");
		Partner_pwd = properties.getProperty("Partner_pwd", "");
		tradecontactid=properties.getProperty("tradecontactid","7");
		company = properties.getProperty("company","");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		String tmp = properties.getProperty("startTime", "");
		Log.info("接口设定启动时间:" + tmp);
		try {
			if(!tmp.isEmpty())
				startTime = Formatter.parseDate(tmp, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			Log.info("转换时间出错!");
			startTime = new Date();
		}
		tmp = properties.getProperty("orderAddTime", "");
		Log.info("接口取指定时间之后的订单:" + tmp);
		try {
			if(!tmp.isEmpty())
				orderAddTime = Formatter.parseDate(tmp, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			Log.info("转换时间出错!");
			orderAddTime = null;
		}
	}
}
