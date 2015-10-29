package com.wofu.ecommerce.threeg.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.wofu.ecommerce.threeg.Params;
import com.wofu.common.tools.util.log.Log;

public class CommonHelper {

	public static String SendRequest(String urlstr,String data) throws Exception
	{		
		StringBuffer responsecontent=new StringBuffer();
		URL url = new URL(urlstr);
		URLConnection rulConnection = url.openConnection();
		HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;   
		httpUrlConnection.setDoOutput(true); 
		httpUrlConnection.setDoInput(true);
		httpUrlConnection.setUseCaches(false);

		httpUrlConnection.setRequestMethod("POST");
		httpUrlConnection.connect();
		
		OutputStream outStrm = httpUrlConnection.getOutputStream();
		DataOutputStream dataoutstream = new DataOutputStream(outStrm); 
		dataoutstream.write(data.getBytes("UTF-8"));
		dataoutstream.flush();
		dataoutstream.close();
		
		
		InputStream inStrm = httpUrlConnection.getInputStream();
		BufferedReader reader =new BufferedReader(new InputStreamReader(inStrm,"UTF-8"));
			
		String line = "";
		while ((line = reader.readLine()) != null)
		{			
			
			responsecontent.append(line);
		}
		
		inStrm.close();	
		return responsecontent.toString();		
		
	}
	
	public static String getXML(String customerprivatekeypath,String agentid,String cmdcode,String body) 
	throws Exception
	{
		String signcode=Utility.Sign(body,customerprivatekeypath);
		
		StringBuffer requestbuffer = new StringBuffer();
	
		requestbuffer.append("msg=<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		requestbuffer.append("<msg>");
		requestbuffer.append("<ctrl>");
		requestbuffer.append("<agentID>").append(agentid).append("</agentID>");
		requestbuffer.append("<md>").append(signcode).append("</md>");
		requestbuffer.append("<cmd>").append(cmdcode).append("</cmd>");
		requestbuffer.append("</ctrl>");	
		requestbuffer.append(body);
		requestbuffer.append("</msg>");
		
		return requestbuffer.toString();
	}
}
