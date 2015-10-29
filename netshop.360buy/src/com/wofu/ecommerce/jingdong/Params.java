package com.wofu.ecommerce.jingdong;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

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

	public static boolean isgenorder;//是否生成系统订单
	public static boolean isDistrictMode;//是否是分销模式 存储过程调用每四个参数为'c'
	public static boolean isSystemPrice;//是否是分销模式 存储过程调用每四个参数为'c'
	public static boolean isgenorderRet;  //是否调用接口订单生成退货订单线程
	
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
		Connection conn=null;
		String sql ="select a.token from ecs_org_params a,ecs_tradecontactorgcontrast b where a.orgid=b.orgid and b.tradecontactid="+tradecontactid;
		try{
			conn = PoolHelper.getInstance().getConnection(dbname);
			token = SQLHelper.strSelect(conn, sql);
		}catch(Exception e){
			Log.error("取token信息出错", e.getMessage());
		}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		appKey = properties.getProperty("appKey","") ;
		appSecret = properties.getProperty("appSecret","") ;
		JBDCustomerCode = properties.getProperty("JBDCustomerCode","") ;
		isLBP = Boolean.parseBoolean(properties.getProperty("isLBP","false")) ;
		jdkdNeedDelivery = Boolean.parseBoolean(properties.getProperty("jdkdNeedDelivery","true")) ;
		isNeedGetDeliverysheetid = Boolean.parseBoolean(properties.getProperty("isNeedGetDeliverysheetid","true")) ;
		isgenorder = Boolean.parseBoolean(properties.getProperty("isgenorder","true")) ;
		isgenorderRet = Boolean.parseBoolean(properties.getProperty("isgenorderRet","true")) ;
		isDistrictMode = Boolean.parseBoolean(properties.getProperty("isDistrictMode","false")) ;
		isSystemPrice = Boolean.parseBoolean(properties.getProperty("isSystemPrice","false")) ;
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
	}
}

