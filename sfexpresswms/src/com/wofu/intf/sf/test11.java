// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package com.wofu.intf.sf;

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

	private static String partnerid = "E-WOLF";
	private static String partnerkey = "w85n2jsu9b7634js";
	private static String url = "http://edi-gateway.800best.com/eoms/api/process";
	private static String callbackurl = "http://fxdis.vicp.cc:8002/BestLogisticsService";
	private static String encoding = "GBK";
	private static int waittime = 10;
	private static String customercode = "85000267";
	private static String storecode = "EC_GZ_DGZ";
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
		bizData.append("<orderCode>020F0L1307050049</orderCode>");
		bizData.append("</"+serviceType+">");
		
		String msgId="400";
		
		List signParams=sfUtil.makeSignParams(bizData.toString(), serviceType,msgtype,
				partnerid,partnerkey,serviceversion,callbackurl,msgId);
		String sign=sfUtil.makeSign(signParams);

	
		Map requestParams=sfUtil.makeRequestParams(bizData.toString(), serviceType, 
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
		stringbuffer.append("<customerName>斐克迪斯</warehouseCode>");
		stringbuffer.append("<company>广州市万尚服装有限公司</company>");
		stringbuffer.append("<contactName>晓辉</contactName>");
		stringbuffer.append("<phoneNumber>020-35624125’</phoneNumber>");
		stringbuffer.append("<mobileNumber>15088067988</mobileNumber>");
		stringbuffer.append("<postalCode>435255</postalCode>");
		stringbuffer.append("<address>广州市天河区车陂</address>");
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
		java.util.List list = sfUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = sfUtil.makeSign(list);
		java.util.Map map = sfUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
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
		stringbuffer.append("<name>男装浅色中裤</name>");
		stringbuffer.append("<englishName>MD-055</englishName>");
		stringbuffer.append("<category>短裤 </category>");
		stringbuffer.append("<barCode>1000000000003</barCode>");
		stringbuffer.append("<serialNo>23</serialNo>");
		stringbuffer.append("<property>颜色:红色 尺码:30码 </property>");
		stringbuffer.append("<volume>0</volume>");
		stringbuffer.append("<length>0</length>");
		stringbuffer.append("<width>0</width>");
		stringbuffer.append("<height>0</height>");
		stringbuffer.append("<weight>0</weight>");
		stringbuffer.append("<unit>件</unit>");
		stringbuffer.append("<packageSpec>1*1</packageSpec>");
		stringbuffer.append("<unitPrice>145</unitPrice>");
		stringbuffer.append("</product>");
		stringbuffer.append("<product>");
		stringbuffer.append("<skuCode>BL00030000022</skuCode>");
		stringbuffer.append("<actionType>OW</actionType>");
		stringbuffer.append("<name>男装商务休闲 MS-030</name>");
		stringbuffer.append("<englishName>MS-030</englishName>");
		stringbuffer.append("<category>长裤            </category>");
		stringbuffer.append("<barCode>1000000000023</barCode>");
		stringbuffer.append("<serialNo>47</serialNo>");
		stringbuffer.append("<property>颜色:黑色 尺码:XS</property>");
		stringbuffer.append("<volume>0</volume>");
		stringbuffer.append("<length>0</length>");
		stringbuffer.append("<width>0</width>");
		stringbuffer.append("<height>0</height>");
		stringbuffer.append("<weight>0</weight>");
		stringbuffer.append("<unit>件</unit>");
		stringbuffer.append("<packageSpec>1*1</packageSpec>");
		stringbuffer.append("<unitPrice>134</unitPrice>");
		stringbuffer.append("</product>");
		stringbuffer.append("</products>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "10059";
		java.util.List list = sfUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = sfUtil.makeSign(list);
		java.util.Map map = sfUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
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
		stringbuffer.append("<note>称重取消测试</note>");
		stringbuffer.append("<buyerName>liuyue199484</buyerName>");
		stringbuffer.append("<buyerPhone>13587217776 0572-6510000</buyerPhone>");
		stringbuffer.append("<recipient>");
		stringbuffer.append("<name>陈晔</name>");
		stringbuffer.append("<postalCode>425220</postalCode>");
		stringbuffer.append("<phoneNumber>0572-6510000</phoneNumber>");
		stringbuffer.append("<mobileNumber>13587217776</mobileNumber>");
		stringbuffer.append("<province>浙江省</province>");
		stringbuffer.append("<city>湖州市</city>");
		stringbuffer.append("<district>长兴县</district>");
		stringbuffer.append("<shippingAddress>浙江省长兴县轻纺城26幢5-6号长日驾校</shippingAddress>");
		stringbuffer.append("</recipient>");
		stringbuffer.append("<items>");
		stringbuffer.append("<item>");
		stringbuffer.append("<itemSkuCode>BL00030000022</itemSkuCode>");
		stringbuffer.append("<itemName>斐克迪思fxdis 牛仔裤女长裤显瘦 水洗微喇叭裤 女士牛崽裤韩版潮 收腰设计复古女式中腰微喇牛仔裤</itemName>");
		stringbuffer.append("<itemQuantity>1</itemQuantity>");
		stringbuffer.append("<itemUnitPrice>105</itemUnitPrice>");
		stringbuffer.append("<itemNote></itemNote>");
		stringbuffer.append("</item>");
		stringbuffer.append("</items>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "100064'";
		java.util.List list = sfUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = sfUtil.makeSign(list);
		java.util.Map map = sfUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
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
		stringbuffer.append("<note>测试B2B</note>");
		stringbuffer.append("<recipient>");
		stringbuffer.append("<name>潘兴科</name>");
		stringbuffer.append("<postalCode>425220</postalCode>");
		stringbuffer.append("<phoneNumber>020-6510000</phoneNumber>");
		stringbuffer.append("<mobileNumber>13587217776</mobileNumber>");
		stringbuffer.append("<province>广东省</province>");
		stringbuffer.append("<city>广州市</city>");
		stringbuffer.append("<district>天河区</district>");
		stringbuffer.append("<shippingAddress>棠德南路110号</shippingAddress>");
		stringbuffer.append("</recipient>");
		stringbuffer.append("<items>");
		stringbuffer.append("<item>");
		stringbuffer.append("<itemSkuCode>BL00030000022</itemSkuCode>");
		stringbuffer.append("<itemName>男装商务休闲 MS-030</itemName>");
		stringbuffer.append("<itemQuantity>47</itemQuantity>");
		stringbuffer.append("<itemUnitPrice>105</itemUnitPrice>");
		stringbuffer.append("<itemNote></itemNote>");
		stringbuffer.append("</item>");
		stringbuffer.append("<item>");
		stringbuffer.append("<itemSkuCode>BL00030000001</itemSkuCode>");
		stringbuffer.append("<itemName>男装浅色中裤 MD-055</itemName>");
		stringbuffer.append("<itemQuantity>55</itemQuantity>");
		stringbuffer.append("<itemUnitPrice>136</itemUnitPrice>");
		stringbuffer.append("<itemNote></itemNote>");
		stringbuffer.append("</item>");
		stringbuffer.append("</items>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "100050";
		java.util.List list = sfUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = sfUtil.makeSign(list);
		java.util.Map map = sfUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
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
		stringbuffer.append("<name>潘兴科</name>");
		stringbuffer.append("<postalCode>425500</postalCode>");
		stringbuffer.append("<phoneNumber>020-2563214</phoneNumber>");
		stringbuffer.append("<mobileNumber>15360416289</mobileNumber>");
		stringbuffer.append("<province>广东省</province>");
		stringbuffer.append("<city>广州市</city>");
		stringbuffer.append("<district>天河区</district>");
		stringbuffer.append("<shippingAddress>棠德南路123号</shippingAddress>");
		stringbuffer.append("<email>panxingke@163.com</email>");
		stringbuffer.append("</sender>");
		stringbuffer.append("<items>");
		stringbuffer.append("<item>");
		stringbuffer.append("<itemSkuCode>BL00030000022</itemSkuCode>");
		stringbuffer.append("<itemName>男装商务休闲 MS-030</itemName>");
		stringbuffer.append("<itemQuantity>350</itemQuantity>");
		stringbuffer.append("<itemNote></itemNote>");
		stringbuffer.append("</item>");
		stringbuffer.append("<item>");
		stringbuffer.append("<itemSkuCode>BL00030000001</itemSkuCode>");
		stringbuffer.append("<itemName>男装浅色中裤 MD-055</itemName>");
		stringbuffer.append("<itemQuantity>100</itemQuantity>");
		stringbuffer.append("<itemNote>颜色:蓝色 尺码:30码</itemNote>");
		stringbuffer.append("</item>");
		stringbuffer.append("</items>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "100057";
		java.util.List list = sfUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = sfUtil.makeSign(list);
		java.util.Map map = sfUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
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
		java.util.List list = sfUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = sfUtil.makeSign(list);
		java.util.Map map = sfUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
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
		stringbuffer.append("<province>浙江省</province>");
		stringbuffer.append("<city>湖州市</city>");
		stringbuffer.append("<district>长兴县</district>");
		stringbuffer.append("<shippingAddress>浙江省长兴县轻纺城26幢5-6号长日驾校</shippingAddress>");
		stringbuffer.append("<email></email>");
		stringbuffer.append("</sender>");
		stringbuffer.append("<items>");
		stringbuffer.append("<item>");
		stringbuffer.append("<itemSkuCode>BL00030000022</itemSkuCode>");
		stringbuffer.append("<itemName>男装商务休闲 MS-030</itemName>");
		stringbuffer.append("<itemQuantity>1</itemQuantity>");
		stringbuffer.append("<itemNote></itemNote>");
		stringbuffer.append("</item>");
		stringbuffer.append("</items>");
		stringbuffer.append((new StringBuilder()).append("</").append(s).append(">").toString());
		String s1 = "100043";
		java.util.List list = sfUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = sfUtil.makeSign(list);
		java.util.Map map = sfUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
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
			java.util.List list = sfUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
			String s2 = sfUtil.makeSign(list);
			java.util.Map map = sfUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
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
		java.util.List list = sfUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s2);
		String s3 = sfUtil.makeSign(list);
		java.util.Map map = sfUtil.makeRequestParams(stringbuffer.toString(), s, s2, msgtype, s3, callbackurl, serviceversion, partnerid);
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
		java.util.List list = sfUtil.makeSignParams(stringbuffer.toString(), s, msgtype, partnerid, partnerkey, serviceversion, callbackurl, s1);
		String s2 = sfUtil.makeSign(list);
		java.util.Map map = sfUtil.makeRequestParams(stringbuffer.toString(), s, s1, msgtype, s2, callbackurl, serviceversion, partnerid);
		String s3 = CommHelper.sendRequest(url, map, "");
		System.out.println(s3);
	}

}
