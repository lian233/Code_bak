package com.wofu.ecommerce.sf.webServiceClient.util;

import com.wofu.common.tools.util.log.Log;

public class webServiceclientUtils {
	public static String filterChar(String xml) throws Exception{
		StringBuilder sb = new StringBuilder();
		char[] arr = xml.toCharArray();
		for(char e:arr){
			
			if((int)e<=0x1f){
			}else if((int)e==0xdde2){
				Log.info("e:"+Integer.toHexString((int)(e)));
			}else if((int)e==38)
				sb.append("&amp;");
			else{
				sb.append(e);
			}
			
		}
		return sb.toString();
	}
	
	public static String filterChar2(String str) throws Exception{
		StringBuilder sb = new StringBuilder();
		char[] arr = str.toCharArray();
		for(char e:arr){
			
			if(e=='<'){
				sb.append("&lt;");
			}else if(e=='>'){
				sb.append("&gt;");
			}else{
				sb.append(e);
			}
			
		}
		return sb.toString();
	}
}
