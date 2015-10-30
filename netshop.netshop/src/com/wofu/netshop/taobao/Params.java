package com.wofu.netshop.taobao;
import java.util.Properties;
public class Params {
	public static String dbname = "taobao";
	
	public static String tradecontactid="";

	public static String url = "http://gw.api.taobao.com/router/rest";
	
	public static String encoding="GBK";

	public static String appkey = "12205522";

	public static String appsecret = "0d56c9fb65cf8eb136437b02f52846fa";
	
	public static String authcode="29230f8604eb0f9207db7e879820ed1fe09ce";
	
	public static int total=10;
	
	public static int waittime = 10;
	
	public static String username = "��ʿ��ʱ���콢��";
	
	public static String province="�㶫ʡ";
	
	public static String city="������";
	
	public static String district="�����";
	
	public static String address="����·��ʤ��ҵ��2��301";
	
	public static String zipcode="510610";
	
	public static String linkman="����ΰ";
	public static String dsName="";
	
	public static String phone="020-38458026";

	public static String mobile="15992409145";
	public static String serverIp="";
	public static String socketContent="";
	public static int serverPort;
	public static int SocketwaitMinute;
	
	public static boolean isdistribution=false;    //�Ƿ�Ϊ������
	public static String isJZ;    //�Ƿ�ͨ�˼�װ����
	public static boolean isNeedDelivery=true;    //�Ƿ� ��Ҫ����
	public static boolean isGenCustomerOrder=true;    //�Ƿ� ��Ҫ���ɿͻ�����
	public static boolean isgenCustomerRet=true;    //�Ƿ� ��Ҫ���ɿͻ��˻�����
	public static boolean isUpdateStock=true;    //�Ƿ� ��Ҫ���ɿͻ��˻�����
	public static String jzParams="";    //��װ��˾�б�
	
	public static boolean isc=true;    //�Ƿ�Ϊc��
	public static boolean isNewProc=false;    //�Ƿ�Ϊc��
	public static boolean isRemote=false;    //�Ƿ�Ϊc��
	
	
	public static boolean isrds=true;    //���������Ƿ����Ծ�ʯ��
	public static String sellernick="";    //���������Ƿ����Ծ�ʯ��
	public static int isDelay=0;    //���ɶ������ʱ�����������Ƿ��ӳ�����
	public static String dsid;    //���ɶ������ʱ�����������Ƿ��ӳ�����
	public static int tableType=0;    //���ɶ������ʱ���������Դ����ʱ����Դ��
	public static boolean isStopStockSyn;  //���¿���߳��Ƿ�����
	public static boolean isSockServer;    //�Ƿ���socket�����
	public static boolean isSockClient;    //�Ƿ���socket�ͻ���
	public static String localdsid;      //���ݿ����rds�����ʱ����±���extds���־
	public static boolean isNeedUpdataLocal;//�Ƿ���Ҫ���±���extds���¼
	public static int jobCount;//��������

	public static String isEc;//�Ƿ���ec��������
	
	public Params() {
	}

	public static void init(Properties properties) {
		appkey = properties.getProperty("appkey", "taobao");
		appsecret = properties.getProperty("appsecret", "taobao");
		authcode = properties.getProperty("authcode", "taobao");
		dbname = properties.getProperty("dbname", "taobao");
		tradecontactid=properties.getProperty("tradecontactid","");
		url = properties.getProperty("url", "http://gw.api.taobao.com/router/rest");
		encoding=properties.getProperty("encoding","GBK");
		appkey = properties.getProperty("appkey", "");
		appsecret = properties.getProperty("appsecret", "");
		authcode = properties.getProperty("authcode", "6201a05512211ZZ986db1a3d738339c4092a2d9167a5d80665715024");
		total = (new Integer(properties.getProperty("total", "10"))).intValue();
		waittime = (new Integer(properties.getProperty("waittime", "10"))).intValue();		
		username = properties.getProperty("username", "��ʿ��ʱ���콢��");
		province= properties.getProperty("province", "�㶫ʡ");
		city= properties.getProperty("city", "������");
		district= properties.getProperty("district", "�����");
		address = properties.getProperty("address", "����·��ʤ��ҵ��2��301");
		zipcode = properties.getProperty("zipcode", "510610");
		linkman = properties.getProperty("linkman", "����ΰ");
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
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		isStopStockSyn = Boolean.valueOf(properties.getProperty("isStopStockSyn", "0").equals("0")?"false":"true").booleanValue();
		isSockServer = Boolean.valueOf(properties.getProperty("isSockServer", "0").equals("0")?"false":"true").booleanValue();
		isSockClient = Boolean.valueOf(properties.getProperty("isdistribution", "0").equals("0")?"false":"true").booleanValue();
		isNeedDelivery = Boolean.valueOf(properties.getProperty("isNeedDelivery", "0").equals("0")?"false":"true").booleanValue();
		isGenCustomerOrder = Boolean.valueOf(properties.getProperty("isGenCustomerOrder", "0").equals("0")?"false":"true").booleanValue();
		isgenCustomerRet = Boolean.valueOf(properties.getProperty("isgenCustomerRet", "0").equals("0")?"false":"true").booleanValue();
		isNeedDelivery = Boolean.valueOf(properties.getProperty("isNeedDelivery", "0").equals("0")?"false":"true").booleanValue();
		isRemote = Boolean.valueOf(properties.getProperty("isRemote", "0").equals("0")?"false":"true").booleanValue();
		isUpdateStock = Boolean.valueOf(properties.getProperty("isUpdateStock", "0").equals("0")?"false":"true").booleanValue();
		isNeedUpdataLocal = Boolean.valueOf(properties.getProperty("isNeedUpdataLocal", "0").equals("0")?"false":"true").booleanValue();
		isJZ = properties.getProperty("isJZ", "0");
		isEc = properties.getProperty("isEc", "0");
		if("1".equals(isJZ)) jzParams = properties.getProperty("jzParams", "[{'is_virtual_tp':false,'service_type':20,'tp_code':'1717833274','tp_name':'֣�ݵ�ʦ�������������޹�˾'},{'is_virtual_tp':false,'service_type':20,'tp_code':'2011492230','tp_name':'���ݻ��ﻧ��ҵ�������޹�˾'},{'is_virtual_tp':false,'service_type':20,'tp_code':'2101922596','tp_name':'����־���������Ƽ����޹�˾'},{'is_virtual_tp':false,'service_type':20,'tp_code':'2210500986','tp_name':'�Ϻ���װ����Ϣ�������޹�˾'}]");
	}
}

