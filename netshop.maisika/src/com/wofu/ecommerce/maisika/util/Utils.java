package com.wofu.ecommerce.maisika.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Map;

public class Utils
{
	/*public static String sendbypost(String params) throws ClientProtocolException, IOException, JException
	{
		HttpPost httpPost=new HttpPost(params);
		HttpResponse httpResponse=new DefaultHttpClient().execute(httpPost);
		String result=EntityUtils.toString(httpResponse.getEntity());
		System.out.println(result);
		return result;
	}*/
	
	public static String sendByPost(String appParamMap, String urlStr)
	{
        System.out.println("����:"+appParamMap);
		PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(urlStr);
            // �򿪺�URL֮�������
            URLConnection conn = realUrl.openConnection();
            // ����ͨ�õ���������
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            //conn.setRequestProperty("user-agent",
            //        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // ����POST�������������������
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // ��ȡURLConnection�����Ӧ�������
            out = new PrintWriter(conn.getOutputStream());
            // �����������
            out.print(appParamMap);
            // flush������Ļ���
            out.flush();
            // ����BufferedReader����������ȡURL����Ӧ
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result = result + line;
            }
        } catch (Exception e) {
            System.out.println("���� POST ��������쳣��"+e);
            e.printStackTrace();
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
	
	public static String md5(String txt)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(txt.getBytes("UTF-8")); // ������Ҫ�������Java���ַ�����unicode���룬����Դ���ļ��ı���Ӱ�죻��PHP�ı����Ǻ�Դ���ļ��ı���һ�£���Դ�����Ӱ�졣
			StringBuffer buf = new StringBuffer();
			for (byte b : md.digest())
			{
				buf.append(String.format("%02x", b & 0xff));
			}
			return buf.toString();
		} catch (Exception e)
		{
			e.printStackTrace();

			return null;
		}
	}
}
