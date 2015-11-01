package com.wofu.ecommerce.huasheng.util; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.huasheng.Params;

public class Utils {
	
	
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
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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
	
	/**
	 * 发送请求
	 * @param service	服务名
	 * @param ParamData	参数(除service和vcode外,第一个参数前不需加&)
	 * @return
	 */
	public static String doRequest(String service, String ParamData, boolean UsePublicVcode)
	{
		try {
			String methodParam = "service=" + service + "&vcode=" + (UsePublicVcode ? Params.VCODE : Params.vcode) + "&" + ParamData;
			if(ParamData.equals(""))
			{
				return "";
			}
			//进行HTTP Post提交
//			Log.info("请求地址:" + Params.apiurl);
//			Log.info("请求参数:" + methodParam);
//			String result = sendByPost(Params.apiurl,methodParam);
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(Params.apiurl);
			Log.info("请求地址:" + post.getURI().toString());
			post.setHeader("accept", "*/*");
			post.setHeader("connection", "Keep-Alive");
			post.setHeader("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			StringEntity strentity = new StringEntity(methodParam,HTTP.UTF_8);						//构造的时候要加上HTTP.UTF_8,解决中文乱码问题
			strentity.setContentType("application/x-www-form-urlencoded");
			strentity.setContentEncoding("UTF-8");
			post.setEntity(strentity);
			
			Log.info("请求参数:" + InputStream2String(post.getEntity().getContent()));
			HttpResponse res = client.execute(post);
			HttpEntity entity = res.getEntity();
			//返回结果
			String result = InputStream2String(entity.getContent());
			
			Log.info("返回结果:" + result);
			return result;
		} catch (Exception e) {
			Log.info("发送POST出错");
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * InputStream 转 字符串
	 * @param is
	 * @return
	 */
	public static String InputStream2String(InputStream is)  {  
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf8"));		//源字符串为utf8
			String temp = null;
			while ((temp = br.readLine()) != null) {
				result += temp + "\n";
			}
			result = new String(result.getBytes(),"gbk");	//输出为gbk
		}catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * GBK转UTF8编码
	 * @param content GBK字符串
	 * @return UTF8字符串
	 */
	public static byte[] gbk2utf8(String content) {
        char c[] = content.toCharArray();
        
        byte[] fullByte = new byte[3 * c.length];
        
        for (int i = 0; i < c.length; i++) {
            int m = (int) c[i];
            String word = Integer.toBinaryString(m);
            
            StringBuffer sb = new StringBuffer();
            int len = 16 - word.length();
            for (int j = 0; j < len; j++) {
                sb.append("0");
            }
            
            sb.append(word);
            
            sb.insert(0, "1110");
            sb.insert(8, "10");
            sb.insert(16, "10");
            
            String s1 = sb.substring(0, 8);
            String s2 = sb.substring(8, 16);
            String s3 = sb.substring(16);

            byte b0 = Integer.valueOf(s1, 2).byteValue();
            byte b1 = Integer.valueOf(s2, 2).byteValue();
            byte b2 = Integer.valueOf(s3, 2).byteValue();
            
            byte[] bf = new byte[3];
            bf[0] = b0;
            bf[1] = b1;
            bf[2] = b2;
            
            fullByte[i * 3] = bf[0];            
            fullByte[i * 3 + 1] = bf[1];            
            fullByte[i * 3 + 2] = bf[2];
        }
        return fullByte;
    }
} 