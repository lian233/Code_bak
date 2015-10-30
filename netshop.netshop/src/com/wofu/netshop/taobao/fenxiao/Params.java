package com.wofu.netshop.taobao.fenxiao;
import java.util.Properties;

import com.wofu.common.tools.util.log.Log;
public class Params {
	public  String dbname = "taobao";
	
	public  String tradecontactid="";

	public  String url = "http://gw.api.taobao.com/router/rest";
	
	public  String encoding="GBK";

	public  String appkey = "12205522";

	public  String appsecret = "0d56c9fb65cf8eb136437b02f52846fa";
	
	public  String authcode="29230f8604eb0f9207db7e879820ed1fe09ce";
	
	public  int total=10;
	
	public  int waittime = 10;
	
	public  String username = "迪士尼时尚旗舰店";
	
	public  String province="广东省";
	
	public  String city="广州市";
	
	public  String district="天河区";
	
	public  String address="车陂路大胜工业区2栋301";
	
	public  String zipcode="510610";
	
	public  String linkman="张利伟";
	public  String dsName="";
	
	public  String phone="020-38458026";

	public  String mobile="15992409145";
	public  String serverIp="";
	public  String socketContent="";
	public  int serverPort;
	public  int SocketwaitMinute;
	
	public  boolean isdistribution=false;    //是否为分销店
	public  String isJZ;    //是否开通了家装服务
	public  boolean isNeedDelivery=true;    //是否 需要发货
	public  boolean isGenCustomerOrder=true;    //是否 需要生成客户订单
	public  boolean isgenCustomerRet=true;    //是否 需要生成客户退货订单
	public  boolean isUpdateStock=true;    //是否 需要生成客户退货订单
	public  String jzParams="";    //家装公司列表
	
	public  boolean isc=true;    //是否为c店
	public  boolean isNewProc=false;    //是否为c店
	public  boolean isRemote=false;    //是否为c店
	
	
	public  boolean isrds=true;    //订单数据是否来自聚石塔
	public  String sellernick="";    //订单数据是否来自聚石塔
	public  int isDelay=0;    //生成订单表的时候其它数据是否延迟生成
	public  String dsid;    //生成订单表的时候其它数据是否延迟生成
	public  int tableType=0;    //生成订单表的时候的数据来源是临时表还是源表
	public  boolean isStopStockSyn;  //更新库存线程是否启动
	public  boolean isSockServer;    //是否是socket服务端
	public  boolean isSockClient;    //是否是socket客户端
	public  String localdsid;      //数据库挂在rds上面的时候更新本地extds表标志
	public  boolean isNeedUpdataLocal;//是否需要更新本地extds表记录
	public  int jobCount;//总任务数

	public  String isEc;//是否是ec类型数据

	public  int shopid;

	
	public Params() {
	}

	public  void init(Properties properties) {
		appkey = properties.getProperty("AppKey", "taobao");
		appsecret = properties.getProperty("Session", "taobao");
		authcode = properties.getProperty("Token", "taobao");
		dbname = properties.getProperty("dbname", "taobao");
		tradecontactid=properties.getProperty("tradecontactid","");
		url = properties.getProperty("url", "http://gw.api.taobao.com/router/rest");
		encoding=properties.getProperty("encoding","GBK");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("name", "迪士尼时尚旗舰店").trim();
		province= properties.getProperty("province", "广东省");
		city= properties.getProperty("city", "广州市");
		district= properties.getProperty("district", "天河区");
		address = properties.getProperty("address", "车陂路大胜工业区2栋301");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "张利伟");
		phone = properties.getProperty("phone", "020-38458026");
		mobile = properties.getProperty("mobile", "15992409145");
		dsName = properties.getProperty("dsName", "");
		serverIp = properties.getProperty("serverIp", "127.0.0.1");
		serverPort = Integer.parseInt(properties.getProperty("serverPort", "30004"));
		//jobCount = Integer.parseInt(properties.getProperty("jobCount", "4"));
		sellernick = properties.getProperty("sellernick", "");
		socketContent = properties.getProperty("socketContent", "");
		dsid = properties.getProperty("dsid", "");
		localdsid = properties.getProperty("localdsid", "");
		isdistribution = Boolean.valueOf(properties.getProperty("isdistribution", "0").equals("0")?"false":"true").booleanValue();
		isc = Boolean.valueOf(properties.getProperty("isc", "0").equals("0")?"false":"true").booleanValue();
		isrds = Boolean.valueOf(properties.getProperty("isrds", "0").equals("0")?"false":"true").booleanValue();
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		SocketwaitMinute = Integer.valueOf(properties.getProperty("SocketwaitMinute", "0"));
		shopid = Integer.valueOf(properties.getProperty("id", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isStopStockSyn = Boolean.valueOf(properties.getProperty("isStopStockSyn", "0").equals("0")?"false":"true").booleanValue();
		isSockServer = Boolean.valueOf(properties.getProperty("isSockServer", "0").equals("0")?"false":"true").booleanValue();
		isSockClient = Boolean.valueOf(properties.getProperty("isdistribution", "0").equals("0")?"false":"true").booleanValue();
		isNeedDelivery = Boolean.valueOf(properties.getProperty("isNeedDelivery", "0").equals("0")?"false":"true").booleanValue();
		isGenCustomerOrder = Boolean.valueOf(properties.getProperty("isGenCustomerOrder", "1").equals("0")?"false":"true").booleanValue();
		isgenCustomerRet = Boolean.valueOf(properties.getProperty("isgenCustomerRet", "0").equals("0")?"false":"true").booleanValue();
		isRemote = Boolean.valueOf(properties.getProperty("isRemote", "0").equals("0")?"false":"true").booleanValue();
		isUpdateStock = Boolean.valueOf(properties.getProperty("isUpdateStock", "0").equals("0")?"false":"true").booleanValue();
		isNeedUpdataLocal = Boolean.valueOf(properties.getProperty("isNeedUpdataLocal", "0").equals("0")?"false":"true").booleanValue();
		isJZ = properties.getProperty("isJZ", "0");
		isEc = properties.getProperty("isEc", "0");
		if("1".equals(isJZ)) jzParams = properties.getProperty("jzParams", "[{'is_virtual_tp':false,'service_type':20,'tp_code':'1717833274','tp_name':'郑州灯师傅照明工程有限公司'},{'is_virtual_tp':false,'service_type':20,'tp_code':'2011492230','tp_name':'杭州户帮户企业管理有限公司'},{'is_virtual_tp':false,'service_type':20,'tp_code':'2101922596','tp_name':'神工众志（北京）科技有限公司'},{'is_virtual_tp':false,'service_type':20,'tp_code':'2210500986','tp_name':'上海家装宝信息技术有限公司'}]");
	}
}

