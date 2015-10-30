package com.wofu.common.test;

import com.groupon.domain.model.ws.DisneyRequestBean;
import com.groupon.ws.ObjBodyWriter;
import com.wofu.ecommerce.groupon.Params;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import meta.MD5Util;
import org.apache.axiom.om.*;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class test18 {

	public test18() {
	}
	
	public static void main(String args[]) throws JException,ParseException
	{
		
		//Date dt=new Date(1311910061417L);
		//System.out.println(Formatter.format(dt, Formatter.DATE_TIME_FORMAT));
		//getBusinessProjectList("9105333605228628609");
		getBusinessProjectInfo();
		//ArrayList<String> list=new ArrayList<String>();
		//list.add("4996333609058218313");
		//list.add("9207116391203778968");
		//list.add("8775445019397387775");
		//list.add("-4321047603944408043");
		//list.add("6048704523362378001");
		//list.add("-8388405886326607863");
		//list.add("-7811697621419236743");
		//list.add("-6258109093116490594");
		
		
		//getBusinessProjectOrderDelivery();
		//getBusinessProjectInfo();
		/*
		for (java.util.Iterator it=list.iterator();it.hasNext();)
		{
			String grouponid=(String) it.next();
			//System.out.println(grouponid);
			getBusinessProjectList(grouponid);
		}*/
		//getBusinessProjectList("8775445019397387775");
		//getBusinessProjectInfo();
		
		//getBusinessProjectList();

		// Date dt =new Date(1308711294741L);
		
		// System.out.println(Formatter.format(dt,"yyyy-MM-dd HH:mm:ss"));
		
		/*
		 * Date dt1=Formatter.parseDate("2011-06-22 12:44:50", "yyyy-MM-dd
		 * HH:mm:ss"); System.out.println(dt1.getTime());
		 * System.out.println(Formatter.format(dt1,"yyyy-MM-dd HH:mm:ss"));
		 * 
		 * System.out.println(System.currentTimeMillis()); Date dt2=new Date();
		 * System.out.println(dt2.getTime());
		 */
		// getBusinessProjectOrderDelivery();
		// step1();
		/*
		 * Integer i = Integer.valueOf(Integer.parseInt(args[0])); switch
		 * (i.intValue()) { case 1: // '\001' step1(); break;
		 * 
		 * case 2: // '\002' step2(); break;
		 * 
		 * case 3: // '\003' step3(); break;
		 * 
		 * case 4: // '\004' step4(); break;
		 * 
		 * default: System.out.println("Arguments Error ... ... "); break; }
		 */		
		
	}
	private static void updateSaleRule()
	{
		StringBuffer buffer = new StringBuffer();
		String key = "28831102";
		String itemid = "-3271007032185393930";
		String time = String.valueOf(System.currentTimeMillis());
		buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		buffer.append("<groupon>");
		buffer.append("<request>");
		buffer.append((new StringBuilder("<request_time>")).append(time)
				.append("</request_time>").toString());
		buffer.append((new StringBuilder("<sign>")).append(
				MD5Util.MD5Encode((new StringBuilder(itemid).append(time))
						.append(key).toString())).append("</sign>").toString());
		buffer.append((new StringBuilder("<itemId>")).append(itemid)
				.append("</itemId>").toString());
		buffer.append((new StringBuilder("<addFlag>")).append("0")
				.append("</addFlag>").toString());		
		buffer.append("<rule>");
		buffer.append("<ruleBean>");
		buffer.append((new StringBuilder("<color>")).append("085585204201")
				.append("</color>").toString());	
		buffer.append((new StringBuilder("<size>")).append("03")
				.append("</size>").toString());		
		buffer.append((new StringBuilder("<num>")).append("50")
				.append("</num>").toString());						
		buffer.append("</ruleBean>");
		buffer.append("</rule>");
		buffer.append("</request>");
		buffer.append("</groupon>");
		OMElement requestSoapMessage = ObjBodyWriter.toOMElement(buffer
				.toString(), "UTF-8");
		Options options = new Options();
		options.setTo(new EndpointReference(
				"http://store.groupon.cn/services/BusinessProjectService"));
		options.setAction("updateSaleRule");
		options.setProperty("__CHUNKED__", Boolean.valueOf(false));
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
			sender.setOptions(options);
			System.out.println(requestSoapMessage);
			OMElement result = sender.sendReceive(requestSoapMessage);
			System.out.println(result);
		} catch (Exception axisFault) {
			axisFault.printStackTrace();
		}
	}
	private static void getBusinessProjectInfo() {
		OMFactory soapFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = soapFactory.createOMNamespace(
				"http://www.groupon.cn/", "");
		OMElement soapResponse = soapFactory.createOMElement("groupon", omNs);
		DisneyRequestBean requestBean = new DisneyRequestBean();
		requestBean.setCategoryid("68");
		String s = (new StringBuilder(String
				.valueOf(System.currentTimeMillis()))).toString();
		requestBean.setRequest_time(s);
		requestBean.setSign(MD5Util.MD5Encode((new StringBuilder("68")).append(
				s).append("28831102").toString()));
		soapResponse.addChild(ObjBodyWriter.convertBeanToXml(requestBean,
				"request"));
		Options options = new Options();
		options.setTo(new EndpointReference(
				"http://store.groupon.cn/services/BusinessProjectService"));
		options.setAction("getBusinessProjectInfo");
		options.setProperty("__CHUNKED__", Boolean.valueOf(false));
		ServiceClient sender = null;
//		ArrayList<String> list=new ArrayList<String>();
		try {
			sender = new ServiceClient();
			sender.setOptions(options);
			//System.out.println(soapResponse);
			OMElement result = sender.sendReceive(soapResponse);
			System.out.println(result.toString());
			/*
			Document doc = DOMHelper.newDocument(result.toString(), "GBK");
			Element urlset = doc.getDocumentElement();
			NodeList datanodes = urlset.getElementsByTagName("data");
			for (int i = 0; i < datanodes.getLength(); i++) {

				Element dataelement = (Element) datanodes.item(i);
				//list.add(dataelement.getElementsByTagName("grouponid").item(0)
				//		.getChildNodes().item(0).getNodeValue());
				/*
				System.out.println("groupid: "
						+ dataelement.getElementsByTagName("grouponid").item(0)
								.getChildNodes().item(0).getNodeValue());
				System.out.println("groupname: "
						+ dataelement.getElementsByTagName("grouponname").item(
								0).getChildNodes().item(0).getNodeValue());
				NodeList datachildnodes = dataelement.getChildNodes();
				for (int j = 0; j < datachildnodes.getLength(); j++) {
					Node childnode = datachildnodes.item(j);
					System.out.println(childnode.getChildNodes().item(0)
							.getNodeValue());
				}
				
			}
			*/
		} catch (Exception axisFault) {
			axisFault.printStackTrace();
		}
		//return list;
	}

	private static void getBusinessProjectList(String grouponid) throws ParseException {
	
			OMFactory soapFactory = OMAbstractFactory.getOMFactory();
			OMNamespace omNs = soapFactory.createOMNamespace(
					"http://www.groupon.cn/", "");
			OMElement soapResponse = soapFactory.createOMElement("groupon", omNs);
			DisneyRequestBean requestBean = new DisneyRequestBean();
			String s = (new StringBuilder(String
					.valueOf(System.currentTimeMillis()))).toString();
			requestBean.setRequest_time(s);
			// 3670447246047540015
			requestBean.setSign(MD5Util.MD5Encode((new StringBuilder(
					grouponid)).append(s).append("28831102")
					.toString()));
			requestBean.setGrouponid(grouponid);
			requestBean.setLimit("0");
			requestBean.setTotal("700");
			requestBean.setStartTime(String.valueOf(System.currentTimeMillis()-3*24*60*60*1000L));
			requestBean.setEndTime(String.valueOf(System.currentTimeMillis()));
			/*
			requestBean.setStartTime(String.valueOf(Formatter.parseDate(
					"2011-07-04 09:00:00", "yyyy-MM-dd HH:mm:ss").getTime()));
			requestBean.setEndTime(String.valueOf(Formatter.parseDate(
					"2011-07-04 12:44:50", "yyyy-MM-dd HH:mm:ss").getTime()));
			*/
			soapResponse.addChild(ObjBodyWriter.convertBeanToXml(requestBean,
					"request"));
			Options options = new Options();
			options.setTo(new EndpointReference(
					"http://store.groupon.cn/services/BusinessProjectService"));
			options.setAction("getBusinessProjectList");
			options.setProperty("__CHUNKED__", Boolean.valueOf(false));
			ServiceClient sender = null;
			try {				
				sender = new ServiceClient();
				sender.setOptions(options);							
				OMElement result = sender.sendReceive(soapResponse);
				System.out.println(result.toString());
				/*
				Document doc = DOMHelper.newDocument(result.toString(), Params.encoding);
				Element urlset = doc.getDocumentElement();
				NodeList orderinfonodes = urlset.getElementsByTagName("order_info");
				System.out.println(orderinfonodes.getLength());
				*/
				//System.out.println(result);
			} catch (Exception axisFault) {
				axisFault.printStackTrace();
			}	
	
	}

	private static void getBusinessProjectUserstatus() {
		StringBuffer buffer = new StringBuffer();
		String key = "28831102";
		String orderId1 = "BJ13085686862806321";
		String orderId2 = "BJ13087112706926573";
		String time = "201104061623";
		buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		buffer.append("<request>");
		buffer.append((new StringBuilder("<order_id>")).append(orderId1)
				.append("</order_id>").toString());
		buffer.append((new StringBuilder("<order_id>")).append(orderId2)
				.append("</order_id>").toString());
		buffer.append((new StringBuilder("<request_time>")).append(time)
				.append("</request_time>").toString());
		buffer.append((new StringBuilder("<sign>")).append(
				MD5Util.MD5Encode((new StringBuilder(String.valueOf(time)))
						.append(key).toString())).append("</sign>").toString());
		buffer.append("</request>");
		OMElement requestSoapMessage = ObjBodyWriter.toOMElement(buffer
				.toString(), "UTF-8");
		Options options = new Options();
		options.setTo(new EndpointReference(
				"http://store.groupon.cn/services/BusinessProjectService"));
		options.setAction("getBusinessProjectUserstatus");
		options.setProperty("__CHUNKED__", Boolean.valueOf(false));
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
			sender.setOptions(options);
			System.out.println(requestSoapMessage);
			OMElement result = sender.sendReceive(requestSoapMessage);
			System.out.println(result);
		} catch (Exception axisFault) {
			axisFault.printStackTrace();
		}
	}

	private static void getBusinessProjectOrderDelivery() {
		StringBuffer buffer = new StringBuffer();
		String key = "28831102";
		String orderId1 = "BJ13107949600382693";
		String time = String.valueOf(System.currentTimeMillis());
		buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		buffer.append("<request>");
		buffer.append("<order>");
		buffer.append((new StringBuilder("<order_id>")).append(orderId1)
				.append("</order_id>").toString());
		buffer.append("<post_company>SF</post_company>");
		buffer.append("<post_no>102108675285</post_no>");
		buffer.append("</order>");
		buffer.append((new StringBuilder("<request_time>")).append(time)
				.append("</request_time>").toString());
		buffer.append((new StringBuilder("<sign>")).append(
				MD5Util.MD5Encode((new StringBuilder(String.valueOf(time)))
						.append(key).toString())).append("</sign>").toString());
		buffer.append("</request>");
		OMElement requestSoapMessage = ObjBodyWriter.toOMElement(buffer
				.toString(), "UTF-8");
		Options options = new Options();
		options.setTo(new EndpointReference(
				"http://store.groupon.cn/services/BusinessProjectService"));
		options.setAction("getBusinessProjectOrderDelivery");
		options.setProperty("__CHUNKED__", Boolean.valueOf(false));
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
			sender.setOptions(options);
			System.out.println(requestSoapMessage);
			OMElement result = sender.sendReceive(requestSoapMessage);
			System.out.println(result);
		} catch (Exception axisFault) {
			axisFault.printStackTrace();
		}
	}
}