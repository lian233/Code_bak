package com.wofu.netshop.mogujie.fenxiao;
import java.util.Properties;
public class Params {
	public String dbname = "shop";
	
	public String tradecontactid="";

	public String url = "http://openapi.yhd.com/app/api/rest/newRouter";
	
	public String encoding="UTF-8";

	public String merchantid = "7816";

	public String checkcode  = "77-37927-19-99-11107-48257971-28-16-6076";
	
	public String secretkey="XcdFt5934LkoPDTRhGQ9";
	
	public String format="xml";
	
	public String company="";
	
	public String ver="1.0";
	
	public String username = "迪士尼时尚旗舰店";
	
	public String province="广东省";
	
	public String city="广州市";
	
	public String district="天河区";
	
	public String address="车陂路大胜工业区2栋301";
	
	public String zipcode="510610";
	
	public String linkman="张利伟";
	
	public String phone="020-38458026";

	public String mobile="15992409145";
	
	public String app_key="";
	public String app_secret="";
	public String token="7a2b396ea85a769703187935d52da647";

	public int jobCount;

	public boolean isNeedDelivery=false;

	public boolean isGenCustomerOrder;

	public boolean isgenCustomerRet;

	public boolean isgetOrder;

	public boolean isUpdateStock=false;

	public int shopid;
	
	public Params() {
	}

	public void init(Properties properties) {
		dbname = properties.getProperty("dbname", "taobao");
		shopid = Integer.parseInt(properties.getProperty("id", "0"));
		tradecontactid=properties.getProperty("tradecontactid","");
		url = properties.getProperty("url", "http://gw.api.taobao.com/router/rest");
		encoding=properties.getProperty("encoding","GBK");
		merchantid = properties.getProperty("merchantid", "");
		format = properties.getProperty("format", "xml");
		company = properties.getProperty("company", "");
		ver = properties.getProperty("ver", "1.0");
		username = properties.getProperty("name", "迪士尼时尚旗舰店").trim();
		province= properties.getProperty("province", "广东省");
		city= properties.getProperty("city", "广州市");
		district= properties.getProperty("district", "天河区");
		address = properties.getProperty("address", "车陂路大胜工业区2栋301");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "张利伟");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");
		app_key = properties.getProperty("AppKey", "15992409145");
		app_secret = properties.getProperty("Session", "15992409145");
		token = properties.getProperty("Token", "15992409145");
		isGenCustomerOrder = Boolean.valueOf(properties.getProperty("isGenCustomerOrder", "1").equals("0")?"false":"true").booleanValue();
		isgenCustomerRet = Boolean.valueOf(properties.getProperty("isgenCustomerRet", "0").equals("0")?"false":"true").booleanValue();
		isgetOrder = Boolean.valueOf(properties.getProperty("isgetOrder", "1").equals("0")?"false":"true").booleanValue();
		isNeedDelivery = Boolean.valueOf(properties.getProperty("isNeedDelivery", "0").equals("0")?"false":"true").booleanValue();
	}
}

