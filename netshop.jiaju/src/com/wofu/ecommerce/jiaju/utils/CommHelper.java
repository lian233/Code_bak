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
	
	/**����Post����
	 * @param URL:��ַ
	 * @param Content:Ҫ���͵�����
	 * @return ���ص�����
	 **/
	public static String sendByPost(String url, String content) throws Exception
	{
		PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // �򿪺�URL֮�������
            URLConnection conn = realUrl.openConnection();
            // ����ͨ�õ���������
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // ����POST�������������������
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // ��ȡURLConnection�����Ӧ�������
            out = new PrintWriter(conn.getOutputStream());
            // �����������
            out.print(content);
            // flush������Ļ���
            out.flush();
            // ����BufferedReader����������ȡURL����Ӧ
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result = result + line;
            }
        } catch (Exception e) {
            Log.warn("���� POST ��������쳣��");
            //e.printStackTrace();
        }
        //ʹ��finally�����ر��������������
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
	
	/**Key����
	 * @param Post_data:Ҫ���͵�HashMap
	 * @return �����Ѿ�����õ�query_string
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
	
	/**��������ǩ��
	 * @param query_string:����õĲ�������
	 * @param Partner_pwd:�̼�˽����Կ
	 * @return �����Ѿ���������ǩ����query_string
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
             md.update(txt.getBytes("GBK"));    //������Ҫ�������Java���ַ�����unicode���룬����Դ���ļ��ı���Ӱ�죻��PHP�ı����Ǻ�Դ���ļ��ı���һ�£���Դ�����Ӱ�졣
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
