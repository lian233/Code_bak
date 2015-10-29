// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package com.wofu.intf.best;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.util.DOMHelper;

// Referenced classes of package com.wofu.intf.best:
//			BestUtil, CommHelper

public class test11
{

	private static String partnerid = "E-WOLF-85000283";
	private static String partnerkey = "w85n2jsu9b7634js678";
	private static String url = "http://edi-gateway.800best.com/eoms/api/process";
	private static String callbackurl = "http://fxdis.vicp.cc:8002/BestLogisticsService";
	private static String encoding = "GBK";
	private static int waittime = 10;
	private static String customercode = "85000283";
	private static String storecode = "WH_EC_XJ";
	private static String interfacesystem = "best";
	private static String serviceversion = "1.0";
	private static String msgtype = "sync";

	public test11()
	{
	}

	public static void main(String args[])
		throws Exception
	{
		getSalesOrderStatus();
	}
	
	private static void getSalesOrderStatus() throws Exception
	{
		String serviceType = "GetSalesOrderStatus";
		StringBuffer bizData = new StringBuffer();
		bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bizData.append("<"+serviceType+">");
		bizData.append("<customerCode>"+customercode+"</customerCode>");
		bizData.append("<warehouseCode>"+storecode+"</warehouseCode>");
		bizData.append("<orderCode>991D0L1410190013</orderCode>");
		bizData.append("</"+serviceType+">");
		
		String msgId="4002";
		
		List signParams=BestUtil.makeSignParams(bizData.toString(), serviceType,msgtype,
				partnerid,partnerkey,serviceversion,callbackurl,msgId);
		String sign=BestUtil.makeSign(signParams);

	
		Map requestParams=BestUtil.makeRequestParams(bizData.toString(), serviceType, 
				msgId, msgtype, sign,callbackurl,
				serviceversion,partnerid);
		

		String result=CommHelper.sendRequest(url, requestParams, "");
		
		System.out.println(result);
	}
	


	private static void syncCustomerInfo()
		throws Exception
	{
		String s = "SyncCustomerInfo";
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringbuffer.append((new StringBuilder()).append("<").append(s).append(">").toString());
		stringbuffer.append((new StringBuilder()).append("<customerCode>").append(customercode).append("</customerCode>").toString());
		stringbuffer.append("<actionType>ADD</actionType>");
		stringbuffer.append("<customerName>쳿˵�˹</warehouseCode>");
		stringbuffer.append("<company>���������з�װ���޹�˾</company>");
		stringbuffer.append("<contactName>����</contactName>");
		stringbuffer.append("<phoneNumber>020-35624125��</phoneNumber>");
		stringbuffer.append("<mobileNumber>15088067988</mobileNumber>");
		stringbuffer.append("<postalCode>435255</postalCode>");
		stringbuffer.append("<address>���������������</address>");
		stringbuffer.append("<email>18666970@qq.com</email>");
		stringbuffer.append("<firstWareHouseCode>020F0L</firstWareHouseCode>");
		stringbuffer.append("<warehouses>");
		stringbuffer.append("<warehouse>");
		stringbuffer.append("<actionType>ADD</actionType>");
		stringbuffer.append("<warehouseCode>020F0L</warehouseCode>");
		stringbuffer.append("</warehouse>");
		stringbuffer.append("<warehouse>");
		stringbuffer.append("<actionType>ADD</actionType>");
		stringbuffer.append("<warehouseCode>020F1L</warehouseCode>");
		stringbuffer.append("</warehouse>");
		stringbuffer.append("</warehouses>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "10001";
		java.util.List list = BestUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = BestUtil.makeSign(list);
		java.util.Map map = BestUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
		String s3 = CommHelper.sendRequest(url, map, "");
		System.out.println(s3);
	}

	private static void syncProductInfo()
		throws Exception
	{
		String s = "SyncProductInfo";
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringbuffer.append((new StringBuilder()).append("<").append(s).append(">").toString());
		stringbuffer.append((new StringBuilder()).append("<customerCode>").append(customercode).append("</customerCode>").toString());
		stringbuffer.append("<products>");
		stringbuffer.append("<product>");
		stringbuffer.append("<skuCode>BL00030000001</skuCode>");
		stringbuffer.append("<actionType>OW</actionType>");
		stringbuffer.append("<name>��װǳɫ�п�</name>");
		stringbuffer.append("<englishName>MD-055</englishName>");
		stringbuffer.append("<category>�̿� </category>");
		stringbuffer.append("<barCode>1000000000003</barCode>");
		stringbuffer.append("<serialNo>23</serialNo>");
		stringbuffer.append("<property>��ɫ:��ɫ ����:30�� </property>");
		stringbuffer.append("<volume>0</volume>");
		stringbuffer.append("<length>0</length>");
		stringbuffer.append("<width>0</width>");
		stringbuffer.append("<height>0</height>");
		stringbuffer.append("<weight>0</weight>");
		stringbuffer.append("<unit>��</unit>");
		stringbuffer.append("<packageSpec>1*1</packageSpec>");
		stringbuffer.append("<unitPrice>145</unitPrice>");
		stringbuffer.append("</product>");
		stringbuffer.append("<product>");
		stringbuffer.append("<skuCode>BL00030000022</skuCode>");
		stringbuffer.append("<actionType>OW</actionType>");
		stringbuffer.append("<name>��װ�������� MS-030</name>");
		stringbuffer.append("<englishName>MS-030</englishName>");
		stringbuffer.append("<category>����            </category>");
		stringbuffer.append("<barCode>1000000000023</barCode>");
		stringbuffer.append("<serialNo>47</serialNo>");
		stringbuffer.append("<property>��ɫ:��ɫ ����:XS</property>");
		stringbuffer.append("<volume>0</volume>");
		stringbuffer.append("<length>0</length>");
		stringbuffer.append("<width>0</width>");
		stringbuffer.append("<height>0</height>");
		stringbuffer.append("<weight>0</weight>");
		stringbuffer.append("<unit>��</unit>");
		stringbuffer.append("<packageSpec>1*1</packageSpec>");
		stringbuffer.append("<unitPrice>134</unitPrice>");
		stringbuffer.append("</product>");
		stringbuffer.append("</products>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "10059";
		java.util.List list = BestUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = BestUtil.makeSign(list);
		java.util.Map map = BestUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
		String s3 = CommHelper.sendRequest(url, map, "");
		System.out.println(s3);
	}

	private static void syncSalesOrderInfo()
		throws Exception
	{
		String s = "SyncSalesOrderInfo";
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringbuffer.append((new StringBuilder()).append("<").append(s).append(">").toString());
		stringbuffer.append((new StringBuilder()).append("<customerCode>").append(customercode).append("</customerCode>").toString());
		stringbuffer.append((new StringBuilder()).append("<warehouseCode>").append(storecode).append("</warehouseCode>").toString());
		stringbuffer.append("<orderCode>020F0L1306260008</orderCode>");
		stringbuffer.append("<actionType>CANCEL</actionType>");
		stringbuffer.append("<extTradeId>339247805881716</extTradeId>");
		stringbuffer.append("<orderType>NORMAL</orderType>");
		stringbuffer.append("<orderSource>TAOBAO</orderSource>");
		stringbuffer.append("<orderTime>2013-06-26 14:41:43.000</orderTime>");
		stringbuffer.append("<totalAmount>254.6</totalAmount>");
		stringbuffer.append("<logisticsProviderCode>STO</logisticsProviderCode>");
		stringbuffer.append("<note>����ȡ������</note>");
		stringbuffer.append("<buyerName>liuyue199484</buyerName>");
		stringbuffer.append("<buyerPhone>13587217776 0572-6510000</buyerPhone>");
		stringbuffer.append("<recipient>");
		stringbuffer.append("<name>����</name>");
		stringbuffer.append("<postalCode>425220</postalCode>");
		stringbuffer.append("<phoneNumber>0572-6510000</phoneNumber>");
		stringbuffer.append("<mobileNumber>13587217776</mobileNumber>");
		stringbuffer.append("<province>�㽭ʡ</province>");
		stringbuffer.append("<city>������</city>");
		stringbuffer.append("<district>������</district>");
		stringbuffer.append("<shippingAddress>�㽭ʡ��������ĳ�26��5-6�ų��ռ�У</shippingAddress>");
		stringbuffer.append("</recipient>");
		stringbuffer.append("<items>");
		stringbuffer.append("<item>");
		stringbuffer.append("<itemSkuCode>BL00030000022</itemSkuCode>");
		stringbuffer.append("<itemName>쳿˵�˼fxdis ţ�п�Ů�������� ˮϴ΢���ȿ� Ůʿţ�̿㺫�泱 ������Ƹ���Ůʽ����΢��ţ�п�</itemName>");
		stringbuffer.append("<itemQuantity>1</itemQuantity>");
		stringbuffer.append("<itemUnitPrice>105</itemUnitPrice>");
		stringbuffer.append("<itemNote></itemNote>");
		stringbuffer.append("</item>");
		stringbuffer.append("</items>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "100064'";
		java.util.List list = BestUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = BestUtil.makeSign(list);
		java.util.Map map = BestUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
		String s3 = CommHelper.sendRequest(url, map, "");
		System.out.println(s3);
	}

	private static void syncReturn()
		throws Exception
	{
		String s = "SyncSalesOrderInfo";
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringbuffer.append((new StringBuilder()).append("<").append(s).append(">").toString());
		stringbuffer.append((new StringBuilder()).append("<customerCode>").append(customercode).append("</customerCode>").toString());
		stringbuffer.append((new StringBuilder()).append("<warehouseCode>").append(storecode).append("</warehouseCode>").toString());
		stringbuffer.append("<orderCode>020F0L13062600013</orderCode>");
		stringbuffer.append("<actionType>CANCEL</actionType>");
		stringbuffer.append("<orderType>WDO</orderType>");
		stringbuffer.append("<orderTime>2013-06-26 14:41:43.000</orderTime>");
		stringbuffer.append("<totalAmount>101525</totalAmount>");
		stringbuffer.append("<note>����B2B</note>");
		stringbuffer.append("<recipient>");
		stringbuffer.append("<name>���˿�</name>");
		stringbuffer.append("<postalCode>425220</postalCode>");
		stringbuffer.append("<phoneNumber>020-6510000</phoneNumber>");
		stringbuffer.append("<mobileNumber>13587217776</mobileNumber>");
		stringbuffer.append("<province>�㶫ʡ</province>");
		stringbuffer.append("<city>������</city>");
		stringbuffer.append("<district>�����</district>");
		stringbuffer.append("<shippingAddress>�ĵ���·110��</shippingAddress>");
		stringbuffer.append("</recipient>");
		stringbuffer.append("<items>");
		stringbuffer.append("<item>");
		stringbuffer.append("<itemSkuCode>BL00030000022</itemSkuCode>");
		stringbuffer.append("<itemName>��װ�������� MS-030</itemName>");
		stringbuffer.append("<itemQuantity>47</itemQuantity>");
		stringbuffer.append("<itemUnitPrice>105</itemUnitPrice>");
		stringbuffer.append("<itemNote></itemNote>");
		stringbuffer.append("</item>");
		stringbuffer.append("<item>");
		stringbuffer.append("<itemSkuCode>BL00030000001</itemSkuCode>");
		stringbuffer.append("<itemName>��װǳɫ�п� MD-055</itemName>");
		stringbuffer.append("<itemQuantity>55</itemQuantity>");
		stringbuffer.append("<itemUnitPrice>136</itemUnitPrice>");
		stringbuffer.append("<itemNote></itemNote>");
		stringbuffer.append("</item>");
		stringbuffer.append("</items>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "100050";
		java.util.List list = BestUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = BestUtil.makeSign(list);
		java.util.Map map = BestUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
		String s3 = CommHelper.sendRequest(url, map, "");
		System.out.println(s3);
	}

	private static void syncAsnInfo()
		throws Exception
	{
		String s = "SyncAsnInfo";
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringbuffer.append((new StringBuilder()).append("<").append(s).append(">").toString());
		stringbuffer.append((new StringBuilder()).append("<customerCode>").append(customercode).append("</customerCode>").toString());
		stringbuffer.append((new StringBuilder()).append("<warehouseCode>").append(storecode).append("</warehouseCode>").toString());
		stringbuffer.append("<asnCode>020F0L13062600002</asnCode>");
		stringbuffer.append("<actionType>ADD</actionType>");
		stringbuffer.append("<extTradeId>2435448</extTradeId>");
		stringbuffer.append("<sender>");
		stringbuffer.append("<name>���˿�</name>");
		stringbuffer.append("<postalCode>425500</postalCode>");
		stringbuffer.append("<phoneNumber>020-2563214</phoneNumber>");
		stringbuffer.append("<mobileNumber>15360416289</mobileNumber>");
		stringbuffer.append("<province>�㶫ʡ</province>");
		stringbuffer.append("<city>������</city>");
		stringbuffer.append("<district>�����</district>");
		stringbuffer.append("<shippingAddress>�ĵ���·123��</shippingAddress>");
		stringbuffer.append("<email>panxingke@163.com</email>");
		stringbuffer.append("</sender>");
		stringbuffer.append("<items>");
		stringbuffer.append("<item>");
		stringbuffer.append("<itemSkuCode>BL00030000022</itemSkuCode>");
		stringbuffer.append("<itemName>��װ�������� MS-030</itemName>");
		stringbuffer.append("<itemQuantity>350</itemQuantity>");
		stringbuffer.append("<itemNote></itemNote>");
		stringbuffer.append("</item>");
		stringbuffer.append("<item>");
		stringbuffer.append("<itemSkuCode>BL00030000001</itemSkuCode>");
		stringbuffer.append("<itemName>��װǳɫ�п� MD-055</itemName>");
		stringbuffer.append("<itemQuantity>100</itemQuantity>");
		stringbuffer.append("<itemNote>��ɫ:��ɫ ����:30��</itemNote>");
		stringbuffer.append("</item>");
		stringbuffer.append("</items>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "100057";
		java.util.List list = BestUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = BestUtil.makeSign(list);
		java.util.Map map = BestUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
		String s3 = CommHelper.sendRequest(url, map, "");
		System.out.println(s3);
	}

	private static void getAsnStatus()
		throws Exception
	{
		String s = "GetAsnStatus";
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringbuffer.append((new StringBuilder()).append("<").append(s).append(">").toString());
		stringbuffer.append((new StringBuilder()).append("<customerCode>").append(customercode).append("</customerCode>").toString());
		stringbuffer.append((new StringBuilder()).append("<warehouseCode>").append(storecode).append("</warehouseCode>").toString());
		stringbuffer.append("<asnCode>020F0L13062500005</asnCode>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "100027";
		java.util.List list = BestUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = BestUtil.makeSign(list);
		java.util.Map map = BestUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
		String s3 = CommHelper.sendRequest(url, map, "");
		System.out.println(s3);
	}

	private static void syncRmaInfo()
		throws Exception
	{
		String s = "SyncRmaInfo";
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringbuffer.append((new StringBuilder()).append("<").append(s).append(">").toString());
		stringbuffer.append((new StringBuilder()).append("<customerCode>").append(customercode).append("</customerCode>").toString());
		stringbuffer.append((new StringBuilder()).append("<warehouseCode>").append(storecode).append("</warehouseCode>").toString());
		stringbuffer.append("<rmaCode>020F0L13062600056</rmaCode>");
		stringbuffer.append("<actionType>ADD</actionType>");
		stringbuffer.append("<note></note>");
		stringbuffer.append("<refOrderCode>020F0L1306260001</refOrderCode>");
		stringbuffer.append("<extTradeId>223333189694987</extTradeId>");
		stringbuffer.append("<sender>");
		stringbuffer.append("<name>system</name>");
		stringbuffer.append("<postalCode>425500</postalCode>");
		stringbuffer.append("<phoneNumber>0572-6510000</phoneNumber>");
		stringbuffer.append("<mobileNumber>15360416289</mobileNumber>");
		stringbuffer.append("<province>�㽭ʡ</province>");
		stringbuffer.append("<city>������</city>");
		stringbuffer.append("<district>������</district>");
		stringbuffer.append("<shippingAddress>�㽭ʡ��������ĳ�26��5-6�ų��ռ�У</shippingAddress>");
		stringbuffer.append("<email></email>");
		stringbuffer.append("</sender>");
		stringbuffer.append("<items>");
		stringbuffer.append("<item>");
		stringbuffer.append("<itemSkuCode>BL00030000022</itemSkuCode>");
		stringbuffer.append("<itemName>��װ�������� MS-030</itemName>");
		stringbuffer.append("<itemQuantity>1</itemQuantity>");
		stringbuffer.append("<itemNote></itemNote>");
		stringbuffer.append("</item>");
		stringbuffer.append("</items>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "100043";
		java.util.List list = BestUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = BestUtil.makeSign(list);
		java.util.Map map = BestUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
		String s3 = CommHelper.sendRequest(url, map, "");
		System.out.println(s3);
	}

	private static void getAdjustmentList()
		throws Exception
	{
		String s = "GetAdjustmentList";
		int i = 1;
		int j = 1;
		do
		{
			StringBuffer stringbuffer = new StringBuffer();
			stringbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			stringbuffer.append((new StringBuilder()).append("<").append(s).append(">").toString());
			stringbuffer.append((new StringBuilder()).append("<customerCode>").append(customercode).append("</customerCode>").toString());
			stringbuffer.append((new StringBuilder()).append("<warehouseCode>").append(storecode).append("</warehouseCode>").toString());
			stringbuffer.append("<adjustTimeFrom>2013-06-25 00:00:00</adjustTimeFrom>");
			stringbuffer.append("<adjustTimeTo>2013-06-27 23:59:59</adjustTimeTo>");
			stringbuffer.append((new StringBuilder()).append("<currentPage>").append(j).append("</currentPage>").toString());
			stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
			System.out.println(stringbuffer.toString());
			String s1 = "100040";
			java.util.List list = BestUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
			String s2 = BestUtil.makeSign(list);
			java.util.Map map = BestUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
			String s3 = CommHelper.sendRequest(url, map, "");
			System.out.println(s3);
			j++;
		} while (j <= i);
	}

	private static void getAdjustmentStatus()
		throws Exception
	{
		String s = "GetAdjustmentStatus";
		String s1 = "";
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringbuffer.append((new StringBuilder()).append("<").append(s).append(">").toString());
		stringbuffer.append((new StringBuilder()).append("<customerCode>").append(customercode).append("</customerCode>").toString());
		stringbuffer.append((new StringBuilder()).append("<warehouseCode>").append(storecode).append("</warehouseCode>").toString());
		stringbuffer.append((new StringBuilder()).append("<adjustmentCode>").append(s1).append("</adjustmentCode>").toString());
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s2 = "100027";
		java.util.List list = BestUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s2);
		String s3 = BestUtil.makeSign(list);
		java.util.Map map = BestUtil.makeRequestParams(stringbuffer.toString(), s, s2, msgtype, s3, callbackurl, serviceversion, partnerid);
		String s4 = CommHelper.sendRequest(url, map, "");
		System.out.println(s4);
	}

	private static void getProductInventory()
		throws Exception
	{
		String s = "GetProductInventory";
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringbuffer.append((new StringBuilder()).append("<").append(s).append(">").toString());
		stringbuffer.append((new StringBuilder()).append("<customerCode>").append(customercode).append("</customerCode>").toString());
		stringbuffer.append((new StringBuilder()).append("<warehouseCode>").append(storecode).append("</warehouseCode>").toString());
		stringbuffer.append("<products>");
		stringbuffer.append("<product>");
		stringbuffer.append("<skuCode>BL00030000022</skuCode>");
		stringbuffer.append("</product>");
		stringbuffer.append("<product>");
		stringbuffer.append("<skuCode>BL00030000001</skuCode>");
		stringbuffer.append("</product>");
		stringbuffer.append("</products>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "100038";
		java.util.List list = BestUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = BestUtil.makeSign(list);
		java.util.Map map = BestUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
		String s3 = CommHelper.sendRequest(url, map, "");
		System.out.println(s3);
	}

}
