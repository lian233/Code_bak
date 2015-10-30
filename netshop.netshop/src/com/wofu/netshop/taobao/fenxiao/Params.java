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
	
	public  String username = "��ʿ��ʱ���콢��";
	
	public  String province="�㶫ʡ";
	
	public  String city="������";
	
	public  String district="�����";
	
	public  String address="����·��ʤ��ҵ��2��301";
	
	public  String zipcode="510610";
	
	public  String linkman="����ΰ";
	public  String dsName="";
	
	public  String phone="020-38458026";

	public  String mobile="15992409145";
	public  String serverIp="";
	public  String socketContent="";
	public  int serverPort;
	public  int SocketwaitMinute;
	
	public  boolean isdistribution=false;    //�Ƿ�Ϊ������
	public  String isJZ;    //�Ƿ�ͨ�˼�װ����
	public  boolean isNeedDelivery=true;    //�Ƿ� ��Ҫ����
	public  boolean isGenCustomerOrder=true;    //�Ƿ� ��Ҫ���ɿͻ�����
	public  boolean isgenCustomerRet=true;    //�Ƿ� ��Ҫ���ɿͻ��˻�����
	public  boolean isUpdateStock=true;    //�Ƿ� ��Ҫ���ɿͻ��˻�����
	public  String jzParams="";    //��װ��˾�б�
	
	public  boolean isc=true;    //�Ƿ�Ϊc��
	public  boolean isNewProc=false;    //�Ƿ�Ϊc��
	public  boolean isRemote=false;    //�Ƿ�Ϊc��
	
	
	public  boolean isrds=true;    //���������Ƿ����Ծ�ʯ��
	public  String sellernick="";    //���������Ƿ����Ծ�ʯ��
	public  int isDelay=0;    //���ɶ�������ʱ�����������Ƿ��ӳ�����
	public  String dsid;    //���ɶ�������ʱ�����������Ƿ��ӳ�����
	public  int tableType=0;    //���ɶ�������ʱ���������Դ����ʱ������Դ��
	public  boolean isStopStockSyn;  //���¿���߳��Ƿ�����
	public  boolean isSockServer;    //�Ƿ���socket�����
	public  boolean isSockClient;    //�Ƿ���socket�ͻ���
	public  String localdsid;      //���ݿ����rds�����ʱ����±���extds����־
	public  boolean isNeedUpdataLocal;//�Ƿ���Ҫ���±���extds����¼
	public  int jobCount;//��������

	public  String isEc;//�Ƿ���ec��������

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
		username = properties.getProperty("name", "��ʿ��ʱ���콢��").trim();
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
		if("1".equals(isJZ)) jzParams = properties.getProperty("jzParams", "[{'is_virtual_tp':false,'service_type':20,'tp_code':'1717833274','tp_name':'֣�ݵ�ʦ�������������޹�˾'},{'is_virtual_tp':false,'service_type':20,'tp_code':'2011492230','tp_name':'���ݻ��ﻧ��ҵ�������޹�˾'},{'is_virtual_tp':false,'service_type':20,'tp_code':'2101922596','tp_name':'����־���������Ƽ����޹�˾'},{'is_virtual_tp':false,'service_type':20,'tp_code':'2210500986','tp_name':'�Ϻ���װ����Ϣ�������޹�˾'}]");
	}
}
