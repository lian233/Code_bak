package com.wofu.ecommerce.maisika.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
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
import com.wofu.ecommerce.maisika.util.CommHelper;
import com.wofu.ecommerce.maisika.util.EncryptMessage;

/**
 *发送http请求帮助类
 */
public class CommHelper {

    /**
     * API请求
     */
	//GET
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
            	String paramvalue =(String)map.get(paramname);
            	str.append(paramname).append("=").append(URLEncoder.encode(paramvalue, "UTF-8")).append("&");
            }
            GetMethod method = new GetMethod(url+str.substring(0,str.length()-1).toString());//get
           Log.info("参数:"+url+str.substring(0,str.length()-1).toString());
            
            int i= client.executeMethod(method);//200表示成功
//            Log.info("状态: "+i);
            	BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),"UTF-8"));
            	for(String line =null;(line = bufferedreader.readLine())!=null;){
            		strBuff.append(line);
            	}
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.info("发送http请求出错");
        }
//        System.out.println("sss: "+(int)strBuff.charAt(0));
//        int B=(int)strBuff.charAt(0);
//        System.out.println("测试"+B+"测试");
//        if(B==65279){
//        	return strBuff.toString().substring(1);
//        }
//        else
//        {
//        	return strBuff.toString();
//        }	
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


    /**
     * 封装API请求头
     */
    public static void mergeHttpHead(HttpURLConnection con, Map<String, Object> map) {

        try {
            // 上传图片的一些参数设置
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
     * 封装http请求体
     */
    public static void mergeHttpBody(HttpURLConnection con, String params) {
        try {

            OutputStream out = con.getOutputStream();
            // 写入业务数据
            out.write(params.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 反馈响应
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
            Log.info("发送http请求出错");
            return "";
        }

    }

    /**
     * 获取当前时间
     */
    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());

    }

    /**
     * 生成签名信息
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
     * 生成json格式字符串
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
