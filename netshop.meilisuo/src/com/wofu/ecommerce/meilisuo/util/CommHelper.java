package com.wofu.ecommerce.meilisuo.util;
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
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;

/**
 *����http���������
 */
public class CommHelper {

    /**
     * API����
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
            	if(paramname.equals("skuid")){
            		str.append(paramvalue).append("&");
            	}else{
            		str.append(paramname).append("=").append(paramvalue).append("&");
            	}
            	
            }
            GetMethod method = new GetMethod(url+(String)map.get("apimethod")+str.substring(0,str.length()-1).toString());
           // Log.info("url: "+url+(String)map.get("apimethod")+str.substring(0,str.length()-1).toString());
            int i= client.executeMethod(method);
            //Log.info("״̬: "+i);
            	BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            	for(String line =null;(line = bufferedreader.readLine())!=null;){
            		strBuff.append(line);
            	}
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.info("����http�������");
        }
        return strBuff.toString();
    }
    
    
    public static String doPost(Map<String, Object> map,String url) {
    	StringBuffer strBuff= new StringBuffer();
        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(url+map.get("apimethod"));
            for(Iterator iterator=map.keySet().iterator();iterator.hasNext();){
            	String paramname = (String)iterator.next();
            	
            	if(paramname.equals("apimethod")) continue;
            	Object paramvalue =map.get(paramname);
            	Log.info(paramname+": "+paramvalue);
            	method.addParameter(paramname, (String)paramvalue);  //addParameter
            }
            int i= client.executeMethod(method);
            //Log.info("״̬: "+i);
            	BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            	for(String line =null;(line = bufferedreader.readLine())!=null;){
            		strBuff.append(line);
            	}
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.info("����http�������");
        }
        return strBuff.toString();
    }


    /**
     * ��װAPI����ͷ
     */
    public static void mergeHttpHead(HttpURLConnection con, Map<String, Object> map) {

        try {
            // �ϴ�ͼƬ��һЩ��������
            con.setRequestProperty(
                    "Accept",
                    "image/gif,   image/x-xbitmap,   image/jpeg,   image/pjpeg,   application/vnd.ms-excel,   application/vnd.ms-powerpoint,   application/msword,   application/x-shockwave-flash,   application/x-quickviewplus,   */*");
            con.setRequestProperty("Accept-Language", "zh-cn");
            con.setRequestProperty("Content-type",
                    "multipart/form-data;   boundary=---------------------------7d318fd100112");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setUseCaches(false);
            con.setRequestProperty("appMethod", (String) map.get("appMethod"));
            con.setRequestProperty("format", (String) map.get("format"));
            con.setRequestProperty("appKey", (String) map.get("appKey"));
            con.setRequestProperty("appRequestTime", map.get("appRequestTime").toString());
            con.setRequestProperty("signInfo", paramsSign(map));
            con.setRequestProperty("versionNo", map.get("versionNo").toString());
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

    }

    /**
     * ��װhttp������
     */
    public static void mergeHttpBody(HttpURLConnection con, String params) {
        try {

            OutputStream out = con.getOutputStream();
            // д��ҵ������
            out.write(params.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * ������Ӧ
     */
    public static String doResponse(HttpURLConnection con) {
        int responseCode;
        String result="";
        try {
            responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream urlStream = con.getInputStream();
                BufferedReader br = new BufferedReader( new InputStreamReader(urlStream,"utf-8")) ;
               // byte[] contents = new byte[1024];
                //int byteRead = 0;
              try {
            	  result=br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                br.close();
                
            }
            con.disconnect();
            return result;
        } catch (IOException e) {
            Log.info("����http�������");
            return "";
        }

    }

    /**
     * ��ȡ��ǰʱ��
     */
    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());

    }

    /**
     * ����ǩ����Ϣ
     */
    @SuppressWarnings("static-access")
    public static String paramsSign(Map<String, Object> map) {

        String resparams = map.get("resparams").toString();
        String baseStr = EncryptMessage.base64Encode(resparams.getBytes()).replaceAll("\r|\n", "");
        StringBuffer signStr = new StringBuffer();
        signStr.append(map.get("appSecret").toString()).append(map.get("appMethod").toString()).append(map.get("appRequestTime").toString())
                .append(map.get("appKey").toString()).append(map.get("versionNo").toString()).append(baseStr);
        return EncryptMessage.encryptMessage(EncryptMessage.MD5_CODE, signStr.toString());
    }
    

    
    /**
     * ����json��ʽ�ַ���
     * @param map
     * @return
     */
    public static String getJsonStr(HashMap<String,String> requestMap,String requestStr){
		HashMap<String,Object> map1 = new HashMap<String,Object>();
		map1.put(requestStr, requestMap);
		HashMap<String,Object> map2 = new HashMap<String,Object>();
		map2.put("sn_body", map1);
		HashMap<String,Object> map3 = new HashMap<String,Object>();
		map3.put("sn_request", map2);
		return new JSONObject(map3).toString();
    }
    
    
    public static void main(String[] args) {
        CommHelper client = new CommHelper();
        //client.doRequest(map);

    }

}
