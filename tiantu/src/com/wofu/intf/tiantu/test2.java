package com.wofu.intf.tiantu;

import java.util.HashMap;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.config.Resource;
import com.wofu.common.tools.util.DOMHelper;

public class test2 {
	private static String DEFINE_FILE = Processors.class.getName();
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		StringBuffer bizData=new StringBuffer();
		bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		bizData.append("<loms:UpdateSalesOrderStatus xmlns:loms=\"http://loms.800best.com\" xmlns:ns3=\"http://www.w3.org/2001/XMLSchema-instance\" ns3:schemaLocation=\"http://loms.800best.com /UpdateSalesOrderStatus.xsd\">");
		bizData.append("    <customerCode>85000267</customerCode>");
		bizData.append("    <warehouseCode>EC_GZ_DGZ</warehouseCode>");
		bizData.append("    <orderCode>020F0L1307020001</orderCode>");
		bizData.append("    <orderStatus>DELIVERED</orderStatus>");
		bizData.append("    <orderType>NORMAL</orderType>");
		bizData.append("    <operatorTime>2013-07-02 14:55:15</operatorTime>");
		bizData.append("    <logisticsProviderCode>STO</logisticsProviderCode>");
		bizData.append("    <shippingOrderNo>668223692003</shippingOrderNo>");
		bizData.append("    <weight>0.8</weight>");
		bizData.append("    <volume>0.0</volume>");
		bizData.append("    <length>40.0</length>");     
		bizData.append("    <width>40.0</width>");       
		bizData.append("    <height>6.0</height>");      
		bizData.append("    <extOrderType>NORMAL</extOrderType>");
		bizData.append("    <products>");                
		bizData.append("        <product>");             
		bizData.append("            <skuCode>BL00030000870</skuCode>");
		bizData.append("            <normalQuantity>1</normalQuantity>");
		bizData.append("            <defectiveQuantity>0</defectiveQuantity>");
		bizData.append("            <averageWeight>0.0</averageWeight>");
		bizData.append("            <lineNo>1</lineNo>");
		bizData.append("            <batchs>          ");
		bizData.append("                <batch>       ");
		bizData.append("                    <fixStatusCode>Y</fixStatusCode>");
		bizData.append("                    <packCode>STANDARD</packCode>");
		bizData.append("                    <quantity>1</quantity>");
		bizData.append("                </batch>      ");
		bizData.append("            </batchs>         ");
		bizData.append("        </product>            ");
		bizData.append("    </products>               ");
		bizData.append("    <boxes>                   ");
		bizData.append("        <box>                 ");
		bizData.append("            <boxCode>SJZX</boxCode>");
		bizData.append("            <boxName>SJZX</boxName>");
		bizData.append("        </box>                ");
		bizData.append("    </boxes>                  ");
		bizData.append("</loms:UpdateSalesOrderStatus>");
		HashMap params=new HashMap();
		
		params.put("msgId", "c8dcf31a-e5ae-41f5-b642-35f9360a7356");
		params.put("partnerId", "E-WOLF");
		params.put("serviceType", "UpdateSalesOrderStatus");
		params.put("bizData", bizData.toString());

		String s=CommHelper.sendRequest("http://fxdis.vicp.cc:8002/BestLogisticsServer", params, "");
		
		System.out.println(s);
	}
	
	

}
