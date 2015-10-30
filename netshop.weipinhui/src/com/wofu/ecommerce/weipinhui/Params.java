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
	//ΨƷ��api�������(db)
	public static String apiurl = "http://sandbox.vipapis.com/";
	public static String appSecret = "06BCB91602A8B0AEFAC80F0BE5E314BB";	//����hmac-md5����
	public static String appkey = "dc8eba3a";
	public static int vendor_id = 550;		//��Ӧ��id
	public static String accessToken = "73D3CA3BC9858B13B99EB63DA1539C3B337A29ED";
	//�ӿڳ�������(xml)
	public static String dbname = "weipinhui";
	public static String pageSize = "50";	//(db)
	public static String tradecontactid = "10";	//���ݿ���:select * from TradeContacts,��ʽ��ʱ����Ҫ���һ����¼��ȥʹ��
	public static boolean isgenorder = false;  //(db)�Ƿ���ýӿڶ������ɶ����߳�
	public static boolean isgenorderRet = false;  //(db)�Ƿ���ýӿڶ��������˻������߳�
	public static int isDelay = 0;    //���ɶ������ʱ�����������Ƿ��ӳ�����
	public static int tableType = 0;    //���ɶ������ʱ���������Դ����ʱ����Դ��
	public static int waittime = 10;	//(db)
		public static Date startTime = new Date();	//�ӿ�ָ��������ʱ��
	public static Date orderAddTime = null;
	//�̼һ�����Ϣ(xml)
	public static String username = "�����������̳�";
	public static String address = "�㶫ʡ�������������ݸׯһ��·116�� �㶫����������10¥";
	public static String zipcode = "510610";
	public static String phone = "020-38458026";
	public static String mobile = "";
	//���(xml)
	public static Hashtable<String, String> htPostCompany = new Hashtable<String, String>();
	public static String DeliveryCompanyJsonData = "";
	
	public static void init(Properties properties)
	{
		Log.info("��ʼ������");
		//��xml��ȡ����
		tradecontactid = properties.getProperty("tradecontactid","10");
		dbname = properties.getProperty("dbname", "weipinhui");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		
		//�̼һ�����Ϣ(xml)
		username = properties.getProperty("username","");
		address = properties.getProperty("address","");
		zipcode = properties.getProperty("zipcode","");
		phone = properties.getProperty("phone","");
		mobile = properties.getProperty("mobile","");
				
		//�ӿ��趨����ʱ��
		String tmp = properties.getProperty("startTime", "");
		Log.info("�ӿ��趨����ʱ��:" + tmp);
		try {
			if(!tmp.isEmpty())
				startTime = Formatter.parseDate(tmp, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			Log.info("ת��ʱ�����!");
			startTime = new Date();
		}
		
		//�����ݿ��ȡ����
		UpdateSettingFromDB(null);
		
		Log.info("��ʼ���������");
	}
	
	/**
	 * �������ݿ��еĲ���,�������еĲ�������(���²���)
	 */
	public static void UpdateSettingFromDB(Connection conn)
	{
		Log.info("ͬ�����ݿ����ò���...");
		try {
			if(conn == null)
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
			Hashtable<String, Object> result = SQLHelper.oneRowSelect(conn, "select a.* from ecs_org_params a (nolock),ecs_tradecontactorgcontrast b (nolock) where a.orgid=b.orgid and b.tradecontactid="+tradecontactid);
			
//			Enumeration enum1=result.elements();
//			while(enum1.hasMoreElements())
//				System.out.print(enum1.nextElement()+"\n");
			
			//��ݶ��ձ�(��ݴ����ӦΨƷ������: SF:1600000687  ��ѯ���1600000687 �������)
			String postcompany = PublicUtils.getConfig(conn,Params.username+"��ݶ��ձ�","");
			if(!postcompany.equals(""))
			{
				String com[] = postcompany.split(";") ;
				for(int i = 0 ; i < com.length ; i++)
				{
					String s[] = com[i].split(":") ;
					htPostCompany.put(s[0], s[1]) ;
								//ϵͳ����,ΨƷ�����
				}
			}

			//�ӿ����ò���
			apiurl = result.get("url").toString();
			appSecret = result.get("appsecret").toString();	//����hmac-md5����
			appkey = result.get("appkey").toString();
			vendor_id = Integer.parseInt(result.get("sellerid").toString());		//��Ӧ��id
			pageSize = result.get("pagesize").toString();
			waittime = Integer.parseInt(result.get("waittime").toString());
			isgenorder = Integer.parseInt(result.get("order_handle_enable").toString()) == 1 ? true : false;
			isgenorderRet = Integer.parseInt(result.get("refund_handle_enable").toString()) == 1 ? true : false;
			accessToken = result.get("token").toString();
			
			conn.close();
		} catch (Exception e) {
			System.out.println("�����ݿ��ȡ����ʧ��:\n" + e.getMessage());
		}
	}
}
