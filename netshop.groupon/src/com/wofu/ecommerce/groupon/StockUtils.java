package com.wofu.ecommerce.groupon;

import java.util.Hashtable;

import meta.MD5Util;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

import com.groupon.ws.ObjBodyWriter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class StockUtils {

	public static void updateStock(String modulename,Hashtable htwsinfo,String tid,String itemid,Hashtable skuinfo)
		throws JException
	{
		StringBuffer buffer = new StringBuffer();
	
			
		String sku=skuinfo.get("sku").toString().trim();
		String customno=skuinfo.get("customno").toString().trim();
		String sizecode=skuinfo.get("sizecode").toString().trim();
		String colorcode=skuinfo.get("colorcode").toString().trim();
		String qty=skuinfo.get("qty").toString();
		//String tid=skuinfo.get("tid").toString();

		if (itemid.equals(""))
			itemid=ProjectUtils.getItemIDBySku(modulename,htwsinfo,sku);

		//如果SKU不存在，不忽略处理
		if (itemid.equals(""))
			throw new JException("SKU不存在,SKU:【"+sku+"】");

				
		String time = String.valueOf(System.currentTimeMillis());
		buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		buffer.append("<groupon>");
		buffer.append("<request>");
		buffer.append((new StringBuilder("<request_time>")).append(time)
				.append("</request_time>").toString());
		buffer.append((new StringBuilder("<sign>")).append(
				MD5Util.MD5Encode((new StringBuilder(itemid).append(time))
						.append(htwsinfo.get("key").toString()).toString())).append("</sign>").toString());
		buffer.append((new StringBuilder("<itemId>")).append(itemid)
				.append("</itemId>").toString());
		buffer.append((new StringBuilder("<addFlag>")).append(skuinfo.get("addflag").toString())
				.append("</addFlag>").toString());		
		buffer.append("<rule>");
		buffer.append("<ruleBean>");
		buffer.append((new StringBuilder("<color>")).append(customno.trim()+colorcode.trim())
				.append("</color>").toString());	
		buffer.append((new StringBuilder("<size>")).append(sizecode.trim())
				.append("</size>").toString());		
		buffer.append((new StringBuilder("<num>")).append(qty)
				.append("</num>").toString());						
		buffer.append("</ruleBean>");
		buffer.append("</rule>");
		buffer.append("</request>");
		buffer.append("</groupon>");
		OMElement requestSoapMessage = ObjBodyWriter.toOMElement(buffer
				.toString(), "UTF-8");
		Options options = new Options();
		options.setTo(new EndpointReference(htwsinfo.get("wsurl").toString()));
		options.setAction("updateSaleRule");
		options.setProperty("__CHUNKED__", Boolean.valueOf(false));
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
			sender.setOptions(options);
	
			OMElement result = sender.sendReceive(requestSoapMessage);
		
			if (skuinfo.get("addflag").equals("1"))
				Log.info(modulename,"更新库存成功,SKU【"+sku+"】,调整数量:"+qty);
			else
				Log.info(modulename,"更新库存成功,SKU【"+sku+"】,数量:"+qty);
		}
		catch(Exception e)
		{
			throw new JException("更新库存失败,SKU【"+sku+"】,错误信息:"+e.getMessage());
		}	
	}
}
