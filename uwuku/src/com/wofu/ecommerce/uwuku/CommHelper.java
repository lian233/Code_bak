package com.wofu.ecommerce.uwuku;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class CommHelper {
	
	public static String sendRequest(String url, Map params) throws Exception {
		String responsetext="";
		
		url=url+"?";
		for (Iterator it = params.keySet().iterator(); it.hasNext();) {
			String paramname = (String) it.next();
			String paramvalue = (String) params.get(paramname);
			url=url+paramname+"="+paramvalue+"&";				
		}
		url=url.substring(0, url.length()-1);
		responsetext= getData(url);

		return responsetext;
	}
	
	private static String getData(String urlstr)
			throws Exception {
		StringBuffer responsecontent = new StringBuffer();
		URL url = new URL(urlstr);
		URLConnection rulConnection = url.openConnection();
		HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
		//GET方法不需要此设置
		//httpUrlConnection.setDoOutput(true);
		httpUrlConnection.setDoInput(true);
		httpUrlConnection.setUseCaches(false);

		httpUrlConnection.setRequestMethod("POST");
		httpUrlConnection.connect();


		InputStream inStrm = httpUrlConnection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStrm, "GBK"));

		String line = "";
		while ((line = reader.readLine()) != null) {
			responsecontent.append(line);
		}

		inStrm.close();
		return responsecontent.toString();
	}

	


}
