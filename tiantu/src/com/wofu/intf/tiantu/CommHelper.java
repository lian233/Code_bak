package com.wofu.intf.tiantu;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.wofu.common.tools.util.log.Log;

public class CommHelper
{

	public CommHelper()
	{
	}

	public static String sendRequest(String url, Map params, String s1)
		throws Exception
	{
		String result="";
		try{
			
			HttpClient httpclient = new HttpClient();
			httpclient.getParams().setIntParameter("http.socket.timeout",30000);
			PostMethod postmethod = new PostMethod(url);
			postmethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,30000); 
			
			for (Iterator iterator = params.keySet().iterator(); iterator.hasNext(); )
			{
				String paramname = (String)iterator.next();
				String paramvalue = (String)params.get(paramname);
				
				postmethod.addParameter(paramname, paramvalue);
			}
			
			postmethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			int i = httpclient.executeMethod(postmethod);
			//if (i == 200)
			//{
				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(postmethod.getResponseBodyAsStream(), "UTF-8"));
				for (String line = null; (line = bufferedreader.readLine()) != null;)
					result = result.concat(line);

			//}
			
			result = URLDecoder.decode(result, "UTF-8");
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;
		
	}
	
	//发送二进制流
	public static String sendRequestT(String url, Map params, String s1)
	throws Exception
{
	String result="";
	try{
		Log.info("url: "+url);
		HttpClient httpclient = new HttpClient();
		httpclient.getParams().setIntParameter("http.socket.timeout",30000);
		PostMethod postmethod = new PostMethod(url);
		postmethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,30000); 
		StringBuilder sb = new StringBuilder();
		for (Iterator iterator = params.keySet().iterator(); iterator.hasNext(); )
		{
			String paramname = (String)iterator.next();
			String paramvalue = (String)params.get(paramname);
			sb.append(paramname).append("=").append(paramvalue).append("&");
		}
		sb.delete(sb.length()-1,sb.length());
		Log.info("data:　"+sb.toString());
		byte[] datas = sb.toString().getBytes("utf-8");
		InputStream is = new ByteArrayInputStream(datas,0,datas.length);
		RequestEntity entity = new InputStreamRequestEntity(is,datas.length,"text/xml;charset=utf-8");
		postmethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		postmethod.setRequestEntity(entity);
		int i = httpclient.executeMethod(postmethod);
		//if (i == 200)
		//{
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(postmethod.getResponseBodyAsStream(), "UTF-8"));
			for (String line = null; (line = bufferedreader.readLine()) != null;)
				result = result.concat(line);

		//}
		
		result = URLDecoder.decode(result, "UTF-8");
		
	}catch(Exception ex){
		ex.printStackTrace();
	}
	return result;
	
}
}
