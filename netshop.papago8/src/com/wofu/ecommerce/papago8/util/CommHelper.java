package com.wofu.ecommerce.papago8.util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import com.wofu.common.tools.util.log.Log;

/**
 *发送http请求帮助类
 */
public class CommHelper {

    /**
     * API请求
     */
	public static String doRequest(Map<String,Object> map,String url,String method){
		return method.equals("get")?doGet(map,url):doPost(map,url);
	}
	
    public static String doGet(Map<String, Object> map,String url) {
    	StringBuffer strBuff= new StringBuffer();
        try {
            HttpClient client = new HttpClient();
            StringBuilder str = new  StringBuilder();
            for(Iterator iterator=map.keySet().iterator();iterator.hasNext();){
            	String paramname = (String)iterator.next();
            	if(paramname.equals("apimethod")) continue;
            	
            	String paramvalue =(String)map.get(paramname);
            		str.append(paramname).append("=").append(paramvalue).append("&");
            	
            }
            GetMethod method = new GetMethod(url+(String)map.get("apimethod")+str.substring(0,str.length()-1).toString());
           //Log.info("url: "+url+(String)map.get("apimethod")+str.substring(0,str.length()-1).toString());
            int i= client.executeMethod(method);
            //Log.info("状态: "+i);
            	BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),"utf-8"));
            	for(String line =null;(line = bufferedreader.readLine())!=null;){
            		strBuff.append(line);
            	}
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.info("发送http请求出错");
        }
        return strBuff.toString();
    }
    
    
    public static String doPost(Map<String, Object> map,String url) {
    	StringBuffer strBuff= new StringBuffer();
        try {
            HttpClient client = new HttpClient();
            Log.info(url+map.get("apimethod"));
            PostMethod method = new PostMethod(url+map.get("apimethod"));
            Log.info("test");
            for(Iterator iterator=map.keySet().iterator();iterator.hasNext();){
            	String paramname = (String)iterator.next();
            	
            	if(paramname.equals("apimethod")) continue;
            	Object paramvalue =map.get(paramname);
            	Log.info(paramname+": "+paramvalue);
            	if(paramvalue.getClass()==Long.class)
            	method.addParameter(paramname, String.valueOf(paramvalue));  //addParameter
            	else
            	method.addParameter(paramname, (String)paramvalue);  //addParameter
            }
            int i= client.executeMethod(method);
            //Log.info("状态: "+i);
            	BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            	for(String line =null;(line = bufferedreader.readLine())!=null;){
            		strBuff.append(line);
            	}
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.info("发送http请求出错");
        }
        return strBuff.toString();
    }

}
