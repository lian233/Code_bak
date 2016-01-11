package com.wofu.ecommerce.ecshop.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ecshop.Params;

/**
 *发送http请求帮助类
 */
public class CommHelper {

    /**
     * API请求
     */
    public static String doRequest(Map<String, Object> map,String url) {
        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(url);
            //method.setRequestHeader("Content-type","application/x-www-form-urlencodeed; charset=UTF-8");
            StringBuilder str = new StringBuilder();
            String ac = getAc(map);
            map.put("ac", ac);
            for(Iterator it=map.keySet().iterator();it.hasNext();){
            	String name =(String)it.next();
            	Object value = map.get(name);
            	if(value.getClass()==Integer.class){
            		//str.append(name).append("=").append((Integer)value).append("&");
            		method.addParameter(name, String.valueOf(value));
            	}else if(value.getClass()==Long.class){
            		method.addParameter(name, String.valueOf(value));
            	}else{
            		method.addParameter(name,(String)value);
            	}
            	
            }
            int i = client.executeMethod(method);
            Log.info("result:　"+i);
            StringBuilder result = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            for(String temp = br.readLine();temp!=null;){
            	result.append(temp);
            	temp=br.readLine();
            }
            System.out.println("具体数据"+result.toString());
            return result.toString();
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.info("发送http请求出错");
            return "";
        }
    }


    private static String getAc(Map map){
    	String result ="";
    	TreeMap treeMap = new TreeMap();
    	treeMap.putAll(map);
    	StringBuilder str=new StringBuilder();
    	for(Iterator it= treeMap.keySet().iterator();it.hasNext();){
    		String name= (String)it.next();
    		Object value= treeMap.get(name);
    		if(value.getClass()== Integer.class){
    			str.append(name).append("=").append((Integer)value).append("&");
    		}else{
    			str.append(name).append("=").append(value).append("&");
    		}
    		
    	}
    	//连接中心会话密钥
    	str.append("center_key").append("=").append(Params.center_key);
    	result = MD5Util.getMD5Code(str.toString().getBytes());
    	return result;
    }
    
    public static void main(String[] args) {
        CommHelper client = new CommHelper();
        //client.doRequest(map);

    }

}
