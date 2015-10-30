package com.wofu.ecommerce.jiaju.utils;

import com.wofu.common.tools.util.log.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.net.URLEncoder;

public class CommHelper {
	
	/**发送Post请求
	 * @param URL:地址
	 * @param Content:要发送的内容
	 * @return 返回的内容
	 **/
	public static String sendByPost(String url, String content) throws Exception
	{
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
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(content);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result = result + line;
            }
        } catch (Exception e) {
            Log.warn("发送 POST 请求出现异常！");
            //e.printStackTrace();
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
	
	/**Key排序
	 * @param Post_data:要发送的HashMap
	 * @return 返回已经排序好的query_string
	 * @throws Exception 
	 **/
	public static String sortKey(HashMap<String, String> Post_data) throws Exception
	{
		String query_string = "";
		String[] key_arr = Post_data.keySet().toArray(new String[Post_data.keySet().size()]);
		Arrays.sort(key_arr, String.CASE_INSENSITIVE_ORDER);
		
		for  (String key : key_arr) {   
		    String value = Post_data.get(key);
		    if(query_string.isEmpty())
		    {
		    	query_string = key + "=" + URLEncoder.encode(value,"UTF-8");
		    }
		    else
		    {
		    	query_string += "&" + key + "=" + URLEncoder.encode(value,"UTF-8");
		    }
		}
		return query_string;
	}
	
	/**加上数字签字
	 * @param query_string:排序好的参数内容
	 * @param Partner_pwd:商家私有密钥
	 * @return 返回已经加上数字签名的query_string
	 **/
	public static String makeSign(String query_string, String Partner_pwd)
	{
		//String Sign = MD5Util.getMD5Code((query_string + Partner_pwd).getBytes());
		String Sign = md5(query_string + Partner_pwd);
		return query_string + "&sign=" + Sign;
	}
	
	public static String md5(String txt) {
        try{
             MessageDigest md = MessageDigest.getInstance("MD5");
             md.update(txt.getBytes("GBK"));    //问题主要出在这里，Java的字符串是unicode编码，不受源码文件的编码影响；而PHP的编码是和源码文件的编码一致，受源码编码影响。
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
