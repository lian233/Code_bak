package com.wofu.ecommerce.ming_xie_ku;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UTF8_transformer 
{
	/**
	 * GetString of utf-8
	 * 
	 * @return XML-Formed string
	 */
	public String getUTF8String(String xml) {
		// A StringBuffer Object
		StringBuffer sb = new StringBuffer();
		sb.append(xml);
		String xmString = "";
		String xmlUTF8 = "";
		try {
			xmString = new String(sb.toString().getBytes("UTF-8"));
			xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
			//System.out.println("utf-8 ±àÂë£º" + xmlUTF8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return to String Formed
		return xmlUTF8;
	}  	
}
