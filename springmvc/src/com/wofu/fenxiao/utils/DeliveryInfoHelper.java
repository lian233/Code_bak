package com.wofu.fenxiao.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.util.DOMHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 快递路由帮助类
 * @author Administrator
 *
 */
public class DeliveryInfoHelper {
	//汇通快递xml转json
	public static JSONArray hktyXmlToJson(String xml)throws Exception{
		JSONArray array = new JSONArray();
		JSONObject item =null;
		JSONArray traceArray =null;
		JSONObject tr =null;
		Document doc = DOMHelper.newDocument(xml, "gbk");
		Element docment = doc.getDocumentElement();
		//快递集合
		Element[] traceLogs =DOMHelper.getSubElementsByName(docment, "traceLogs");
		for(int i=0;i<traceLogs.length;i++){
			item = new JSONObject();
			String mailNo = DOMHelper.getSubElementVauleByName(traceLogs[i], "mailNo");
			item.put("mailNo", mailNo);
			Element[] traces = DOMHelper.getSubElementsByName(traceLogs[i], "traces");
			traceArray=new JSONArray();
			for(Element e:traces){
				tr =new JSONObject();
				tr.put("acceptTime", DOMHelper.getSubElementVauleByName(e, "acceptTime"));
				tr.put("scanType", DOMHelper.getSubElementVauleByName(e, "scanType"));
				tr.put("acceptAddress", DOMHelper.getSubElementVauleByName(e, "acceptAddress"));
				tr.put("remark", DOMHelper.getSubElementVauleByName(e, "remark"));
				traceArray.add(tr);
			}
			item.put("traces", traceArray);
			array.add(item);
		}
		return array;
	}
	
}
