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
	
	public static String username = "��ʿ��ʱ�������";
	
	public static String address="�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥";
	
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
	public static boolean jdkdNeedDelivery = true ;  //������������Ķ����Ƿ�Ҫ�Լ�����
	//������ݵ��̼ұ���--�����̼Һ�̨�ı���
	public static String JBDCustomerCode="";
	public static int isDelay=0;    //���ɶ������ʱ�����������Ƿ��ӳ�����
	public static int tableType=0;    //���ɶ������ʱ���������Դ����ʱ����Դ��

	public static boolean isgenorder;//�Ƿ�����ϵͳ����
	public static boolean isDistrictMode;//�Ƿ��Ƿ���ģʽ �洢���̵���ÿ�ĸ�����Ϊ'c'
	public static boolean isSystemPrice;//�Ƿ��Ƿ���ģʽ �洢���̵���ÿ�ĸ�����Ϊ'c'
	public static boolean isgenorderRet;  //�Ƿ���ýӿڶ��������˻������߳�
	
	public Params() {
	}

	public static void init(Properties properties) {
		dbname = properties.getProperty("dbname", "360buy");
		tradecontactid=properties.getProperty("tradecontactid","");
		wsurl = properties.getProperty("wsurl", "http://gw.shop.360buy.com/routerjson");
		encoding=properties.getProperty("encoding","GBK");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("username", "��ʿ��ʱ�������");
		linkman = properties.getProperty("linkman", "��ϵ��");
		address = properties.getProperty("address", "�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥");
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
			Log.error("ȡtoken��Ϣ����", e.getMessage());
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

