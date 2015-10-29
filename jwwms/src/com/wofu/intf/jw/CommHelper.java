package com.wofu.intf.jw;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
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
}
