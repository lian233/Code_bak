package com.wofu.ecommerce.groupon;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import meta.MD5Util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.groupon.domain.model.ws.DisneyRequestBean;
import com.groupon.ws.ObjBodyWriter;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class ProjectUtils {

	/*
	 * 获取所有项目信息
	 */
	public static List<String> getBusinessProjectInfo(String modulename,Hashtable htwsinfo) {
		OMFactory soapFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = soapFactory.createOMNamespace(htwsinfo.get("namespace").toString(), "");
		OMElement soapResponse = soapFactory.createOMElement("groupon", omNs);
		DisneyRequestBean requestBean = new DisneyRequestBean();
		requestBean.setCategoryid(htwsinfo.get("categoryid").toString());
		String s = (new StringBuilder(String
				.valueOf(System.currentTimeMillis()))).toString();
		requestBean.setRequest_time(s);
		requestBean.setSign(MD5Util.MD5Encode((new StringBuilder(
				htwsinfo.get("categoryid").toString())).append(s).append(htwsinfo.get("key").toString()).toString()));
		soapResponse.addChild(ObjBodyWriter.convertBeanToXml(requestBean,
				"request"));
		Options options = new Options();
		options.setTo(new EndpointReference(htwsinfo.get("wsurl").toString()));
		options.setAction("getBusinessProjectInfo");
		options.setProperty("__CHUNKED__", Boolean.valueOf(false));
		ServiceClient sender = null;
		ArrayList<String> plist = new ArrayList<String>();
		try {
			sender = new ServiceClient();
			sender.setOptions(options);
			OMElement result = sender.sendReceive(soapResponse);
			Document doc=null;
			try
			{
				doc = DOMHelper.newDocument(result.toString(), htwsinfo.get("encoding").toString());
			}catch (JException e) {
				Log.error(modulename, "解析XML出错!,XML文件内容:"+result.toString()+" 错误信息:"+e.getMessage());
			}
			
			Element urlset = doc.getDocumentElement();
			NodeList datanodes = urlset.getElementsByTagName("data");
			for (int i = 0; i < datanodes.getLength(); i++) {

				Element dataelement = (Element) datanodes.item(i);
				plist.add(dataelement.getElementsByTagName("grouponid").item(0)
						.getChildNodes().item(0).getNodeValue());	
			}
		}catch (Exception e) {
				Log.error(modulename, "远程访问失败!"+e.getMessage());
		}
		
		return plist;
	}
	
	public static String getItemIDBySku(String modulename,Hashtable htwsinfo,String sku)
	{
		String itemid="";
		boolean is_find=false;
		OMFactory soapFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = soapFactory.createOMNamespace(htwsinfo.get("namespace").toString(), "");
		OMElement soapResponse = soapFactory.createOMElement("groupon", omNs);
		DisneyRequestBean requestBean = new DisneyRequestBean();
		requestBean.setCategoryid(htwsinfo.get("categoryid").toString());
		String s = (new StringBuilder(String
				.valueOf(System.currentTimeMillis()))).toString();
		requestBean.setRequest_time(s);
		requestBean.setSign(MD5Util.MD5Encode((new StringBuilder(
				htwsinfo.get("categoryid").toString())).append(s).append(htwsinfo.get("key").toString()).toString()));
		soapResponse.addChild(ObjBodyWriter.convertBeanToXml(requestBean,
				"request"));
		Options options = new Options();
		options.setTo(new EndpointReference(htwsinfo.get("wsurl").toString()));
		options.setAction("getBusinessProjectInfo");
		options.setProperty("__CHUNKED__", Boolean.valueOf(false));
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
			sender.setOptions(options);
			OMElement result = sender.sendReceive(soapResponse);
			Document doc = DOMHelper.newDocument(result.toString(),
					htwsinfo.get("encoding").toString());
			Element urlset = doc.getDocumentElement();
			NodeList datanodes = urlset.getElementsByTagName("data");
			for (int i = 0; i < datanodes.getLength(); i++) {

				Element dataelement = (Element) datanodes.item(i);
				itemid = dataelement.getElementsByTagName("itemId").item(0)
						.getChildNodes().item(0).getNodeValue();
				NodeList itemlist = dataelement.getElementsByTagName("item");

				for (int j = 0; j < itemlist.getLength(); j++) {
					Element itemelement = (Element) itemlist.item(j);
					if (itemelement.getElementsByTagName("sku").item(0)
							.getChildNodes().item(0).getNodeValue().equals(sku)) {
						is_find = true;
						break;
					}
				}
				if (is_find)
					break;
			}
		} catch (Exception e) {
			Log.error(modulename, "取商品明细失败!" + e.getMessage());
		}
		
		if (!is_find) itemid="";
		
		return itemid;
	}
}
