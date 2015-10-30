package com.wofu.ecommerce.jumei;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;


import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class CommHelper {
	
	public static String sendRequest(String url, 
			Map params, String requestData,String charset) throws Exception {
		String responsetext="";
		
		HttpClient httpClient=new HttpClient();
		httpClient.getParams().setParameter("http.socket.timeout", 5000);
		PostMethod postMethod = new PostMethod(url);
		

		for (Iterator it = params.keySet().iterator(); it.hasNext();) {
			String paramname = (String) it.next();
			String paramvalue = (String) params.get(paramname);
			//Log.info("key: "+paramname+"value: "+paramvalue);
			postMethod.addParameter(paramname, paramvalue);	

			
		}

		int statusCode = httpClient.executeMethod(postMethod);  
		
		if(statusCode==HttpStatus.SC_OK){
			BufferedReader reader =  new  BufferedReader(   
	                new  InputStreamReader(postMethod.getResponseBodyAsStream(),   
	                		charset)); 
			 String line = null;  
	         while ((line = reader.readLine()) != null)  {
	        	 responsetext += line;
	         }
		} 
		
	
		return responsetext;
	}
	

}
