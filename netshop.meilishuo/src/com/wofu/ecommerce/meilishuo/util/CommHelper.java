package com.wofu.ecommerce.meilishuo.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wolf.common.tools.util.Formatter;
//package com.wofu.ecommerce.meilishuo2.util;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.ProtocolException;
//import java.net.URL;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.methods.PostMethod;
//
//import com.wofu.common.json.JSONObject;
//import com.wofu.common.tools.util.log.Log;
//
///**
// *发送http请求帮助类
// */
public class CommHelper {
//
//    /**
//     * API请求
//     */
//	public static String doRequest(Map<String,Object> map,String url,String method){
//		return method.equals("get")?doGet(map,url):doPost(map,url);
//	}
//	
//    public static String doGet(Map<String, Object> map,String url) {
//    	StringBuffer strBuff= new StringBuffer();
//        try {
//            HttpClient client = new HttpClient();
//            StringBuilder str = new  StringBuilder();
//            for(Iterator iterator=map.keySet().iterator();iterator.hasNext();){
//            	String paramname = (String)iterator.next();
//            	if(paramname.equals("apimethod")) continue;
//            	
//            	String paramvalue =(String)map.get(paramname);
//            	if(paramname.equals("skuid")){
//            		str.append(paramvalue).append("&");
//            	}else{
//            		str.append(paramname).append("=").append(paramvalue).append("&");
//            	}
//            	
//            }
//            GetMethod method = new GetMethod(url+(String)map.get("apimethod")+str.substring(0,str.length()-1).toString());
//           // Log.info("url: "+url+(String)map.get("apimethod")+str.substring(0,str.length()-1).toString());
//            int i= client.executeMethod(method);
//            //Log.info("状态: "+i);
//            	BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
//            	for(String line =null;(line = bufferedreader.readLine())!=null;){
//            		strBuff.append(line);
//            	}
//        } catch (Exception e) {
//        	e.printStackTrace();
//        	Log.info("发送http请求出错");
//        }
//        return strBuff.toString();
//    }
//    
//    
//    public static String doPost(Map<String, Object> map,String url) {
//    	StringBuffer strBuff= new StringBuffer();
//        try {
//            HttpClient client = new HttpClient();
//            PostMethod method = new PostMethod(url+map.get("apimethod"));
//            for(Iterator iterator=map.keySet().iterator();iterator.hasNext();){
//            	String paramname = (String)iterator.next();
//            	
//            	if(paramname.equals("apimethod")) continue;
//            	Object paramvalue =map.get(paramname);
//            	Log.info(paramname+": "+paramvalue);
//            	method.addParameter(paramname, (String)paramvalue);  //addParameter
//            }
//            int i= client.executeMethod(method);
//            //Log.info("状态: "+i);
//            	BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
//            	for(String line =null;(line = bufferedreader.readLine())!=null;){
//            		strBuff.append(line);
//            	}
//        } catch (Exception e) {
//        	e.printStackTrace();
//        	Log.info("发送http请求出错");
//        }
//        return strBuff.toString();
//    }
//
//
//    /**
//     * 封装API请求头
//     */
//    public static void mergeHttpHead(HttpURLConnection con, Map<String, Object> map) {
//
//        try {
//            // 上传图片的一些参数设置
//            con.setRequestProperty(
//                    "Accept",
//                    "image/gif,   image/x-xbitmap,   image/jpeg,   image/pjpeg,   application/vnd.ms-excel,   application/vnd.ms-powerpoint,   application/msword,   application/x-shockwave-flash,   application/x-quickviewplus,   */*");
//            con.setRequestProperty("Accept-Language", "zh-cn");
//            con.setRequestProperty("Content-type",
//                    "multipart/form-data;   boundary=---------------------------7d318fd100112");
//            con.setDoInput(true);
//            con.setDoOutput(true);
//            con.setRequestMethod("POST");
//            con.setUseCaches(false);
//            con.setRequestProperty("appMethod", (String) map.get("appMethod"));
//            con.setRequestProperty("format", (String) map.get("format"));
//            con.setRequestProperty("appKey", (String) map.get("appKey"));
//            con.setRequestProperty("appRequestTime", map.get("appRequestTime").toString());
//            con.setRequestProperty("signInfo", paramsSign(map));
//            con.setRequestProperty("versionNo", map.get("versionNo").toString());
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 封装http请求体
//     */
//    public static void mergeHttpBody(HttpURLConnection con, String params) {
//        try {
//
//            OutputStream out = con.getOutputStream();
//            // 写入业务数据
//            out.write(params.getBytes());
//            out.flush();
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 反馈响应
//     */
//    public static String doResponse(HttpURLConnection con) {
//        int responseCode;
//        String result="";
//        try {
//            responseCode = con.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                InputStream urlStream = con.getInputStream();
//                BufferedReader br = new BufferedReader( new InputStreamReader(urlStream,"utf-8")) ;
//               // byte[] contents = new byte[1024];
//                //int byteRead = 0;
//              try {
//            	  result=br.readLine();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                br.close();
//                
//            }
//            con.disconnect();
//            return result;
//        } catch (IOException e) {
//            Log.info("发送http请求出错");
//            return "";
//        }
//
//    }
//
//    /**
//     * 获取当前时间
//     */
//    public static String getNowTime() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        return sdf.format(new Date());
//
//    }
//
//    /**
//     * 生成签名信息
//     */
//    @SuppressWarnings("static-access")
//    public static String paramsSign(Map<String, Object> map) {
//
//        String resparams = map.get("resparams").toString();
//        String baseStr = EncryptMessage.base64Encode(resparams.getBytes()).replaceAll("\r|\n", "");
//        StringBuffer signStr = new StringBuffer();
//        signStr.append(map.get("appSecret").toString()).append(map.get("appMethod").toString()).append(map.get("appRequestTime").toString())
//                .append(map.get("appKey").toString()).append(map.get("versionNo").toString()).append(baseStr);
//        return EncryptMessage.encryptMessage(EncryptMessage.MD5_CODE, signStr.toString());
//    }
//    
//
//  
	public static JSONObject refreshToken(String refresh_token,String appkey,String appsecret,String refreshTokenUrl) throws Exception{
		String access_token ="";
		Map<String, String> param = new HashMap<String, String>();
		param.put("grant_type", "refresh_token");
		param.put("refresh_token", refresh_token);
		param.put("client_id", appkey);
		param.put("client_secret", appsecret);
		//param.put("state", state);
		String responseJson=Utils.sendByPost2(param,refreshTokenUrl);
		System.out.println(responseJson);
		JSONObject object=new JSONObject(responseJson);
		if(object.getInt("error_code")!=0)
			throw new Exception("刷新美丽说token出错，错误代码: "+object.getInt("error_code"));
		return object.getJSONObject("data");
	}


}
