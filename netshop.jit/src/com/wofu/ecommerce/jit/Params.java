package com.wofu.ecommerce.jit;
import java.util.Properties;
public class Params {
	public static String dbname = "yhd";
	public static String tradecontactid="";
	public static String url = "http://openapi.yhd.com/app/api/rest/newRouter";
	public static String encoding="UTF-8";
	public static String driver_tel = "7816";
	public static String checkcode  = "77-37927-19-99-11107-48257971-28-16-6076";
	public static String secretkey="XcdFt5934LkoPDTRhGQ9";
	public static String erp="self";
	public static String erpver="1.0.0";
	public static String format="xml";
	public static String company="";
	public static String ver="1.0.0";
	public static int total=10;
	public static int waittime = 10;
	public static String username = "µÏÊ¿ÄáÊ±ÉÐÆì½¢µê";
	public static String companyname="";  //Éú³ÉdeliverynoÓÃ
	public static final String service="vipapis.delivery.JitDeliveryService";
	public static String zipcode="510610";
	public static String VendorType="COMMON";
	public static String phone="020-38458026";
	public static String mobile="15992409145";
	public static String app_key="ab8823b4";
	public static String app_secret="51C6F3B078ED00AB38E8B463B36601B9";
	public static String token="7a2b396ea85a769703187935d52da647";
	public static String vendor_id;
	public static String pick_no;
	public static String po_no;
	public static boolean isGetSinglePo;
	public static boolean isGetSinglePick;
	public Params() {
	}
	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "taobao");
		companyname = properties.getProperty("companyname", "by");
		tradecontactid=properties.getProperty("tradecontactid","");
		url = properties.getProperty("url", "http://gw.api.taobao.com/router/rest");
		encoding=properties.getProperty("encoding","GBK");
		driver_tel = properties.getProperty("driver_tel", "13478457845");
		erp = properties.getProperty("erp", "");
		erpver = properties.getProperty("erpver", "");
		format = properties.getProperty("format", "xml");
		company = properties.getProperty("company", "");
		ver = properties.getProperty("ver", "1.0");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("username", "µÏÊ¿ÄáÊ±ÉÐÆì½¢µê");
		zipcode = properties.getProperty("zipcode", "510610");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");
		VendorType = properties.getProperty("VendorType", "COMMON");
		app_key = properties.getProperty("app_key", "15992409145");
		app_secret = properties.getProperty("app_secret", "15992409145");
		vendor_id = properties.getProperty("vendor_id", "15992409145");
		token = properties.getProperty("token", "15992409145");
		pick_no = properties.getProperty("pick_no", "15992409145");
		po_no = properties.getProperty("po_no", "15992409145");
		isGetSinglePo = Boolean.valueOf(properties.getProperty("isGetSinglePo", "false"));
		isGetSinglePick = Boolean.valueOf(properties.getProperty("isGetSinglePick", "false"));
	}
}

