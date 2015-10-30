package com.wofu.ecommerce.ylw.util;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;

/**
 *发送http请求帮助类
 */
public class CommHelper {

    /**
     * API请求
     */
    public static String doRequest(Map<String, String> map,String url) {
        try {
           DefaultHttpClient client = new DefaultHttpClient();
           HttpPost httpPost = new HttpPost(url+map.get("apiMethod"));
           httpPost.setHeader("appkey",map.get("appkey"));
           httpPost.setHeader("version_no",map.get("version_no"));
           List<NameValuePair> arr = new ArrayList<NameValuePair>();
           for(Iterator it=map.keySet().iterator();it.hasNext();){
        	   String name= (String)it.next();
        	   if("appkey".equals(name) || "version_no".equals(name)) continue;
        	   String value = (String)map.get(name);
        	   arr.add(new BasicNameValuePair(name,value));
        	   
           }
           httpPost.setEntity(new UrlEncodedFormEntity(arr,"UTF-8"));
           HttpResponse response = client.execute(httpPost);
           HttpEntity entity = response.getEntity();
           String responseDate = EntityUtils.toString(entity);
           Log.info("请求返回数据为: "+responseDate);
           return responseDate;
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.info("发送http请求出错");
            return "";
        } 
    }


    
    public static void main(String[] args) {
        CommHelper client = new CommHelper();
        //client.doRequest(map);

    }

}
