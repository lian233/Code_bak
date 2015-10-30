package com.wofu.ecommerce.weipinhui;

import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.sql.Connection;

import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;


public class Params {
	//唯品会api所需参数(db)
	public static String apiurl = "http://sandbox.vipapis.com/";
	public static String appSecret = "06BCB91602A8B0AEFAC80F0BE5E314BB";	//用于hmac-md5加密
	public static String appkey = "dc8eba3a";
	public static int vendor_id = 550;		//供应商id
	public static String accessToken = "73D3CA3BC9858B13B99EB63DA1539C3B337A29ED";
	//接口程序配置(xml)
	public static String dbname = "weipinhui";
	public static String pageSize = "50";	//(db)
	public static String tradecontactid = "10";	//数据库中:select * from TradeContacts,正式的时候需要添加一条记录进去使用
	public static boolean isgenorder = false;  //(db)是否调用接口订单生成订单线程
	public static boolean isgenorderRet = false;  //(db)是否调用接口订单生成退货订单线程
	public static int isDelay = 0;    //生成订单表的时候其它数据是否延迟生成
	public static int tableType = 0;    //生成订单表的时候的数据来源是临时表还是源表
	public static int waittime = 10;	//(db)
		public static Date startTime = new Date();	//接口指定启动的时间
	public static Date orderAddTime = null;
	//商家基本信息(xml)
	public static String username = "贝贝怡苏宁商城";
	public static String address = "广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼";
	public static String zipcode = "510610";
	public static String phone = "020-38458026";
	public static String mobile = "";
	//快递(xml)
	public static Hashtable<String, String> htPostCompany = new Hashtable<String, String>();
	public static String DeliveryCompanyJsonData = "";
	
	public static void init(Properties properties)
	{
		Log.info("初始化参数");
		//从xml读取参数
		tradecontactid = properties.getProperty("tradecontactid","10");
		dbname = properties.getProperty("dbname", "weipinhui");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		
		//商家基本信息(xml)
		username = properties.getProperty("username","");
		address = properties.getProperty("address","");
		zipcode = properties.getProperty("zipcode","");
		phone = properties.getProperty("phone","");
		mobile = properties.getProperty("mobile","");
				
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
		
		//从数据库读取参数
		UpdateSettingFromDB(null);
		
		Log.info("初始化参数完成");
	}
	
	/**
	 * 载入数据库中的参数,覆盖现有的参数数据(更新参数)
	 */
	public static void UpdateSettingFromDB(Connection conn)
	{
		Log.info("同步数据库配置参数...");
		try {
			if(conn == null)
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
			Hashtable<String, Object> result = SQLHelper.oneRowSelect(conn, "select a.* from ecs_org_params a (nolock),ecs_tradecontactorgcontrast b (nolock) where a.orgid=b.orgid and b.tradecontactid="+tradecontactid);
			
//			Enumeration enum1=result.elements();
//			while(enum1.hasMoreElements())
//				System.out.print(enum1.nextElement()+"\n");
			
			//快递对照表(快递代码对应唯品会代码表: SF:1600000687  查询编号1600000687 获得名称)
			String postcompany = PublicUtils.getConfig(conn,Params.username+"快递对照表","");
			if(!postcompany.equals(""))
			{
				String com[] = postcompany.split(";") ;
				for(int i = 0 ; i < com.length ; i++)
				{
					String s[] = com[i].split(":") ;
					htPostCompany.put(s[0], s[1]) ;
								//系统代号,唯品会代号
				}
			}

			//接口配置参数
			apiurl = result.get("url").toString();
			appSecret = result.get("appsecret").toString();	//用于hmac-md5加密
			appkey = result.get("appkey").toString();
			vendor_id = Integer.parseInt(result.get("sellerid").toString());		//供应商id
			pageSize = result.get("pagesize").toString();
			waittime = Integer.parseInt(result.get("waittime").toString());
			isgenorder = Integer.parseInt(result.get("order_handle_enable").toString()) == 1 ? true : false;
			isgenorderRet = Integer.parseInt(result.get("refund_handle_enable").toString()) == 1 ? true : false;
			accessToken = result.get("token").toString();
			
			conn.close();
		} catch (Exception e) {
			System.out.println("从数据库获取参数失败:\n" + e.getMessage());
		}
	}
}
