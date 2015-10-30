package com.wofu.intf.sf;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.wofu.common.tools.util.Formatter;
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
	
	
	//取得请求流水号
    public static String getMsgid(){
    	String time = Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).replaceAll(":","").replaceAll(" ","-");
    	return time+getNumber();
    }
    
    private static String getNumber(){
		char[] array = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','z','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
		Random rand = new Random();
		for (int i = 62; i > 1; i--) {
		    int index = rand.nextInt(i);
		    char tmp = array[index];
		    array[index] = array[i - 1];
		    array[i - 1] = tmp;
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 8; i++)
		    sb.append( array[i]);
		return sb.toString();
	}
}
