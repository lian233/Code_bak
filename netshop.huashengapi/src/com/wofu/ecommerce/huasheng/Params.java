package com.wofu.ecommerce.huasheng;

import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;


public class Params {
	//api�������(xml)
	public static String apiurl = "http://seryhappy.imwork.net/wscenter/UpdateServlet";
	public static String vcode = "530102019";
	public static String VCODE = "402880a335c807030135c818190f0001";	//����ܲ���֤��,�̶�
	
	//402880a335c807030135c818190f0001  �����ܲ���֤��
	//ff8080814ebaaa14014ed27a30560b72 ��ʽ�ܲ���֤��
	
	//�ӿڳ�������(xml)
	public static String dbname = "huasheng";
	public static String tradecontactid = "27";	//���ݿ���:select * from TradeContacts,��ʽ��ʱ����Ҫ���һ����¼��ȥʹ��
	public static int isDelay = 0;    //���ɶ������ʱ�����������Ƿ��ӳ�����
	public static int tableType = 0;    //���ɶ������ʱ���������Դ����ʱ����Դ��
	public static int waittime = 10;	//�ȴ�ʱ��
	public static Date startTime = new Date();	//�ӿ�ָ��������ʱ��
	//�̼һ�����Ϣ(xml)
	public static String username = "����";
	public static String address = "����";
	public static String zipcode = "510000";
	public static String phone = "020-85630529";
	public static String mobile = "";
	//���(xml)
	public static Hashtable<String, String> htPostCompany = new Hashtable<String, String>();
	
	public static void init(Properties properties)
	{
		Log.info("��ʼ������");
		//��xml��ȡ����
		apiurl = properties.getProperty("apiurl","");
		vcode = properties.getProperty("vcode","");
		tradecontactid = properties.getProperty("tradecontactid","");
		dbname = properties.getProperty("dbname", "huasheng");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		waittime = Integer.valueOf(properties.getProperty("apiurl","0"));
		
		//�̼һ�����Ϣ(xml)
		username = properties.getProperty("username","");
		address = properties.getProperty("address","");
		zipcode = properties.getProperty("zipcode","");
		phone = properties.getProperty("phone","");
		mobile = properties.getProperty("mobile","");
		
		//��ݶ��ձ�(��: "SF:SF;ZTO:zto,..."  ��������涨��ݹ�˾Ҫ�ô���,Ϊ�˷��������������˶��ձ�����)
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
		
		Log.info("��ʼ���������");
	}
}
