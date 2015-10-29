package com.wofu.ecommerce.threeg;

import java.util.Properties;

import com.wofu.common.tools.util.StringUtil;

public class Params {
	public static String dbname = "3G";
	
	public static String tradecontactid="5";

	public static String wsurl = "http://61.145.124.250/jb-kwu/GGMall/CommService/";
	
	public static String encoding="GBK";

	public static String CustomerPrivateKeyPath= "";
	
	public static String GGMallPublicKeyPath="";

	public static String agentid = "1";
	
	public static int waittime = 10;
		
	public static String username = "迪士尼时尚3G商城";
	
	public static String address="广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼";
	
	public static String zipcode="510610";
	
	public static String phone="020-38458026";

	public static String mobile="";
	
	public Params() {
	}

	public static void init(Properties properties) {
		String workdir=System.getProperty("user.dir");
		workdir=StringUtil.replace(workdir,"\\", "/");
		dbname = properties.getProperty("dbname", "3G");	
		tradecontactid=properties.getProperty("tradecontactid","");
		wsurl = properties.getProperty("wsurl", "http://store.groupon.cn/services/BusinessProjectService");
		encoding=properties.getProperty("encoding","GBK");
		CustomerPrivateKeyPath = workdir+"/"+properties.getProperty("CustomerPrivateKeyPath", "");	
		GGMallPublicKeyPath = workdir+"/"+properties.getProperty("GGMallPublicKeyPath", "");
		agentid = properties.getProperty("agentid", "58");
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();	
		username = properties.getProperty("username", "迪士尼时尚3G商城");
		address = properties.getProperty("address", "广东省广州市天河区东莞庄一横路116号 广东生产力大厦10楼");
		zipcode = properties.getProperty("zipcode", "510610");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "");
	}
}

