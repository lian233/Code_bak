package com.wofu.ecommerce.weipinhui.util; 
  
import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.PrintWriter;  
import java.net.URL;  
import java.net.URLConnection;  
import java.util.List;  
import java.util.Map;  

public class ConnUtil {
    /** 
     * ��ָ��URL����GET���������� 
     * 
     * @param url 
     *            ���������URL 
     * @param param 
     *            ����������������Ӧ����name1=value1&name2=value2����ʽ�� 
     * @return URL������Զ����Դ����Ӧ 
     */  
   
    public static String sendGet(String url, String param) {  
        String result = "";  
        BufferedReader in = null;  
        try {  
            String urlName = url + "?" + param;  
            //Log.i("ConnUtil", urlName);  
            System.out.println(urlName);  
            URL realUrl = new URL(urlName);  
            // �򿪺�URL֮�������  
            URLConnection conn = realUrl.openConnection();  
            // ����ͨ�õ���������  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("user-agent",  
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
            // ���ó�ʱʱ��  
            conn.setConnectTimeout(10000);  
            conn.setReadTimeout(10000);  
            // ����ʵ�ʵ�����  
            conn.connect();  
            // ��ȡ������Ӧͷ�ֶ�  
            Map< String,List< String>> map = conn.getHeaderFields();  
            // �������е���Ӧͷ�ֶ�  
            for (String key : map.keySet()) {  
                System.out.println(key + "--->" + map.get(key));  
            }  
            // ����BufferedReader����������ȡURL����Ӧ  
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
            String line;  
            while ((line = in.readLine()) != null) {  
                result += "\n" + line;  
            }  
        } catch (Exception e) {  
            result = "Get:����������ʧ�ܣ�";  
            e.printStackTrace();  
        }  
        // ʹ��finally�����ر�������  
        finally {  
            try {  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }  
        return result;  
    }  
   
    /**  
     * ��ָ��URL����POST����������  
     *  
     * @param url  
     *            ���������URL  
     * @param param  
     *            ����������������Ӧ����name1=value1&name2=value2����ʽ��  
     * @return URL������Զ����Դ����Ӧ  
     */  
    public static String sendPost(String url, String param) {  
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
            conn.setRequestProperty("user-agent",  
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
            // ���ó�ʱʱ��  
            conn.setConnectTimeout(10000);  
            conn.setReadTimeout(10000);  
            // ����POST�������������������  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            // ��ȡURLConnection�����Ӧ�������  
            out = new PrintWriter(conn.getOutputStream());  
            // �����������  
            out.print(param);  
            // flush������Ļ���  
            out.flush();  
            // ����BufferedReader����������ȡURL����Ӧ  
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
            String line;  
            while ((line = in.readLine()) != null) {  
                result += "\n" + line;  
            }  
        } catch (Exception e) {  
            result = "Post:����������ʧ�ܣ�";  
            e.printStackTrace();  
        }  
        // ʹ��finally�����ر��������������  
        finally {  
            try {  
                if (out != null) {  
                    out.close();  
                }  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }  
        return result;  
    }  
} 