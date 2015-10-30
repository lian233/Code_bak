package com.wofu.ecommerce.jiaju;

import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;

//�ҾӾͲ�����
public class Params {
	//��������
	public static String username = "";	//�̳�����(��:�ҾӾ͡������������̳�...)
	public static String dbname = "";	//���ݿ����ӳ�
	public static int waittime = 10;
	public static final String url = "http://www.jiaju.com/openapi/";	//�ӿڵ�ַ
	public static String partner_id = "";	//�ʺ�
	public static String Partner_pwd = "";	//����
	public static String tradecontactid = "7";		//���ݿ���:select * from TradeContacts,��ʽ��ʱ����Ҫ���һ����¼��ȥʹ��
	public static String company = "";		//��ݹ�˾��Ӧ��
	public static int isDelay=0;    //���ɶ������ʱ�����������Ƿ��ӳ�����
	public static int tableType=0;    //���ɶ������ʱ���������Դ����ʱ����Դ��
	public static Date startTime = new Date();
	public static Date orderAddTime = null;
	
	//�������ļ���ȡ��������
	public static void init(Properties properties)
	{
		username=properties.getProperty("username","�ҾӾ�");
		dbname =  properties.getProperty("dbname", "jiaju");
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		partner_id = properties.getProperty("partner_id", "");
		Partner_pwd = properties.getProperty("Partner_pwd", "");
		tradecontactid=properties.getProperty("tradecontactid","7");
		company = properties.getProperty("company","");
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		String tmp = properties.getProperty("startTime", "");
		Log.info("�ӿ��趨����ʱ��:" + tmp);
		try {
			if(!tmp.isEmpty())
				startTime = Formatter.parseDate(tmp, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			Log.info("ת��ʱ�����!");
			startTime = new Date();
		}
		tmp = properties.getProperty("orderAddTime", "");
		Log.info("�ӿ�ȡָ��ʱ��֮��Ķ���:" + tmp);
		try {
			if(!tmp.isEmpty())
				orderAddTime = Formatter.parseDate(tmp, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			Log.info("ת��ʱ�����!");
			orderAddTime = null;
		}
	}
}
