package com.wofu.ecommerce.dangdang.util;


import java.util.Hashtable;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.common.tools.conv.Coded;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.util.DOMHelper;

public class test1 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
				
	
		//searchItems();
		sendGoods();
		
		

	}
	
	private static void sendGoods() throws Exception
	{
		String shopid="6888";
		String key="yongjunit.818";
		String validateString="";
		String url="http://api.dangdang.com/v2/sendGoods.php";
		String xmlData="<?xml version=\"1.0\" encoding=\"GBK\"?><request><functionID>sendGoods</functionID><time>2009-03-2015:10:50</time><OrdersList><OrderInfo><orderID>1719544796</orderID><logisticsName>ÉêÍ¨¿ìµÝ</logisticsName><logisticsTel>010-45675233</logisticsTel><logisticsOrderID>3784938759</logisticsOrderID><SendGoodsList><ItemInfo><itemID>1719544796</itemID><sendGoodsCount>1</sendGoodsCount></ItemInfo></SendGoodsList></OrderInfo></OrdersList></request>";

		String gbkstr=Coded.getEncode(shopid, "GBK").concat(Coded.getEncode(key, "GBK"));
		
		validateString=MD5Util.getMD5Code(gbkstr.getBytes());


		Hashtable params=new Hashtable();
		params.put("gShopID", shopid);
		params.put("validateString", validateString);

		String requestData="sendGoods="+xmlData;

		String responsetext=CommHelper.sendRequest(url,"POST",params,requestData);
		
		System.out.println(responsetext);
	}
	
	private static void searchItems() throws Exception
	{
		String shopid="6888";
		String key="yongjunit.818";
		String validateString="";
		String url="http://api.dangdang.com/v2/searchItems.php";

		String gbkstr=Coded.getEncode(shopid, "GBK").concat(Coded.getEncode(key, "GBK"));
		
		validateString=MD5Util.getMD5Code(gbkstr.getBytes());

		Hashtable params=new Hashtable();
		params.put("gShopID", shopid);
		params.put("validateString", validateString);

		
		String responsetext=CommHelper.sendRequest(url,"GET",params,null);
		
		System.out.println(responsetext);
		
		Document doc = DOMHelper.newDocument(responsetext, "GBK");
		Element urlset = doc.getDocumentElement();
		
		NodeList itemlists=urlset.getElementsByTagName("ItemsList");
		for (int i=0;i<itemlists.getLength();i++)
		{
			Element itemlist=(Element) itemlists.item(i);
			NodeList items=itemlist.getElementsByTagName("ItemInfo");
			for (int j=0;j<items.getLength();j++)
			{
				Element item=(Element) items.item(j);
				System.out.println(DOMHelper.getSubElementVauleByName(item, "itemName"));
				System.out.println(DOMHelper.getSubElementVauleByName(item, "itemSubhead"));
			}
		}
	}
	
	
	

}
