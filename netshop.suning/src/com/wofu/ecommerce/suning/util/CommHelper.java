package com.wofu.ecommerce.suning.util;
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
import java.util.Map;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;

/**
 *����http���������
 */
public class CommHelper {

    /**
     * API����
     */
    public static String doRequest(Map<String, Object> map,String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            mergeHttpHead(con, map);
            mergeHttpBody(con, (String) map.get("resparams"));
            return doResponse(con);
        } catch (MalformedURLException e) {
        	Log.info("����http�������");
            return "";
        } catch (IOException e) {
        	Log.info("����http�������");
            return "";
        }
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
