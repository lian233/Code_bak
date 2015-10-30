package com.wofu.ecommerce.dangdang.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.ecommerce.dangdang.util.CommHelper;
import com.wofu.common.tools.conv.Coded;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.util.DOMHelper;

public class test1 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		String shopid="6888";
		String key="yongjunit.818";
		String validateString="";
		String url="http://api.dangdang.com/v2/searchItems.php";
		
		String gbkstr=Coded.getEncode(shopid, "GBK").concat(Coded.getEncode(key, "GBK"));
		
		validateString=MD5Util.getMD5Code(gbkstr.getBytes());
		
		String requesturl=url;
		
		Hashtable<String, String> params = new Hashtable<String, String>() ;
		params.put("gShopID", shopid) ;
		params.put("validateString", validateString) ;

		String responsetext=CommHelper.sendRequest(requesturl,"GET",params,"");
		
		System.out.println(requesturl);
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
