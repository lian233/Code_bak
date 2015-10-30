package com.wofu.ecommerce.ming_xie_ku.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.util.Formatter;
import com.wofu.ecommerce.ming_xie_ku.Params;



public class Utils 
{
	/***
	 * @author 陈杰柱改写
	 * （url,param）
	 * **/
	public static String sendByPost(String url, String param)
	{
        //System.out.println(param);
		PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            //conn.setRequestProperty("user-agent",
            //        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result = result + line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
	}

	public static String get_sign(String app_Secret,String app_key, JSONObject data,String method,Date now,String ver,String format)
	{
		//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String sign;
		//Map<String, String> map=new HashMap<String, String>();
		//map.s
		StringBuffer stringBuffer=new StringBuffer();
		stringBuffer.append(app_Secret);
		stringBuffer.append("app_key");
		stringBuffer.append(app_key);
		stringBuffer.append("data");
		stringBuffer.append(data.toString());
		stringBuffer.append("format");
		stringBuffer.append(format);
		stringBuffer.append("method");
		stringBuffer.append(method);
		stringBuffer.append("timestamp");
		stringBuffer.append(Formatter.format(now, Formatter.DATE_TIME_FORMAT));
		stringBuffer.append("v");
		stringBuffer.append(ver);
		sign=stringBuffer.toString();
		//System.out.println("未签名的数据"+sign);
		try {
			sign=MD5Util.getMD5Code(sign.getBytes("UTF-8"));  //坑我坑了千百遍
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//System.out.println("签名后的数据"+sign);
		return sign;
	}
	
	public static StringBuffer post_data_process(String method,JSONObject data,String app_key,Date now,String sign)
	{
		StringBuffer buffer = new StringBuffer();
		try 
		{
			buffer.append("data=");
			buffer.append(URLDecoder.decode(URLEncoder.encode(URLEncoder.encode(data.toString(), "UTF-8"), "UTF-8"), "UTF-8"));//编码两次，解码一次
			buffer.append("&");
			buffer.append("method=");
			buffer.append(URLEncoder.encode(method,"UTF-8"));
			buffer.append("&");
			buffer.append("v=");
			buffer.append(URLEncoder.encode(Params.ver,"UTF-8"));
			buffer.append("&");
			buffer.append("app_key=");
			buffer.append(URLEncoder.encode(app_key,"UTF-8"));
			buffer.append("&");
			buffer.append("format=");
			buffer.append(URLEncoder.encode("json","UTF-8"));
			buffer.append("&");
			buffer.append("timestamp=");
			buffer.append(URLEncoder.encode(Formatter.format(now, Formatter.DATE_TIME_FORMAT),"UTF-8"));
			buffer.append("&");
			buffer.append("sign=");
			buffer.append(sign.toUpperCase());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return buffer;
	}
	
    
    public static String md5(String txt) {
        try{
             MessageDigest md = MessageDigest.getInstance("MD5");
             md.update(txt.getBytes("UTF-8"));    //问题主要出在这里，Java的字符串是unicode编码，不受源码文件的编码影响；而PHP的编码是和源码文件的编码一致，受源码编码影响。
             StringBuffer buf=new StringBuffer();            
             for(byte b:md.digest()){
                  buf.append(String.format("%02x", b&0xff));        
             }
            return  buf.toString();
          }catch( Exception e ){
              e.printStackTrace(); 

              return null;
           } 
   }
}
