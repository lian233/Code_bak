package com.wofu.ecommerce.s.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.ecommerce.s.Params;



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
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
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
//		
////		try {
////			HttpPost httpPost = new HttpPost(url);
////			StringEntity myEntity = new StringEntity(param);
////			httpPost.setEntity(myEntity);
////			HttpClient httpClient = new DefaultHttpClient();
////			HttpResponse response = httpClient.execute(httpPost);
////			return EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
////		} catch (UnsupportedEncodingException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		} catch (ClientProtocolException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////		return param;
//		
	}
	
//	public static String sendByPost_back(Map<String, String> appParamMap, String secretKey, String urlStr ) {
//		BufferedReader reader=null;
//		InputStream inputStream=null;
//		HttpClient httpClient=null;
//		try {
//			httpClient = new DefaultHttpClient();
//			
//			HttpPost httpPost = new HttpPost(urlStr);
//
//			TreeMap<String, String> treeMap = new TreeMap<String, String>();
//			if (appParamMap != null) {
//				treeMap.putAll(appParamMap);
//			}
//
//			String sign = Md5Util.md5Signature(treeMap, secretKey);   //MD5签名例子
//			treeMap.put("sign", sign);
//			Iterator<String> iterator = treeMap.keySet().iterator();
//			
//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			
//			while (iterator.hasNext()) {
//				String key = iterator.next();
//				params.add(new BasicNameValuePair(key, treeMap.get(key)));
//			}
//
//			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
//			httpPost.setEntity(uefEntity);
//
//			HttpResponse response = httpClient.execute(httpPost);
//			
//			HttpEntity httpEntity = response.getEntity();
//			inputStream = httpEntity.getContent();
//			//获取返回的数据信息
//			StringBuffer postResult = new StringBuffer();
//			String readLine = null ;
//			reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//			while ((readLine = reader.readLine()) != null) {
//				postResult.append(readLine);
//			}
//
//			httpClient.getConnectionManager().shutdown();
//			return postResult.toString();
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					if (reader != null) {
//						reader.close();
//					}
//					if (inputStream != null) {
//						inputStream.close();
//					}
//				} catch (IOException e) {
//				}
//				httpClient.getConnectionManager().shutdown();
//			}
//			return null;
//	}
/**@author 前人写的，我修改过的
 * @return **/	
//	public static String sendByPost(String url, String param) {
//		BufferedReader reader = null;
//		InputStream inputStream = null;
//		HttpClient httpClient = null;
//		try {
//			httpClient = new DefaultHttpClient();
//			HttpPost httpPost = new HttpPost(url);
//			StringEntity myEntity = new StringEntity(param);
//			httpPost.setEntity(myEntity);
//
//			HttpResponse response = httpClient.execute(httpPost);
//
//			HttpEntity httpEntity = response.getEntity();
//			inputStream = httpEntity.getContent();
//			// 获取返回的数据信息
//			StringBuffer postResult = new StringBuffer();
//			String readLine = null;
//			reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
//			while ((readLine = reader.readLine()) != null) 
//			{
//				postResult.append(readLine);
//			}
//			httpClient.getConnectionManager().shutdown();
//			return postResult.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (reader != null) {
//					reader.close();
//				}
//				if (inputStream != null) {
//					inputStream.close();
//				}
//			} catch (IOException e) {
//			}
//			httpClient.getConnectionManager().shutdown();
//		}
//		return null;
//	}

	public static StringBuffer post_data_process(String method,JSONObject data,Date now,String sign)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		StringBuffer buffer = new StringBuffer();
		try 
		{
			buffer.append("data=");
			buffer.append(URLEncoder.encode(data.toString(), "utf-8"));
			buffer.append("&");
			buffer.append("method=");
			buffer.append(URLEncoder.encode(method,"utf-8"));
			buffer.append("&");
			buffer.append("v=");
			buffer.append(URLEncoder.encode(Params.ver,"utf-8"));
			buffer.append("&");
			buffer.append("app_key=");
			buffer.append(URLEncoder.encode(Params.app_key,"utf-8"));
			buffer.append("&");
			buffer.append("format=");
			buffer.append(URLEncoder.encode("json","utf-8"));
			buffer.append("&");
			buffer.append("timestamp=");
			buffer.append(URLEncoder.encode(df.format(now),"utf-8"));
			buffer.append("&");
			buffer.append("sign=");
			buffer.append(URLEncoder.encode(sign.toUpperCase(),"utf-8"));
			System.out.println(buffer.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return buffer;
	}
	
	public static String get_sign(JSONObject data,String method,Date now)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String sign=Params.app_Secret
		+"app_key"+Params.app_key
		+"data"+data.toString()
		+"format"+Params.format
		+"method"+method
		+"timestamp"+df.format(now)
		+"v"+Params.ver;
		sign=MD5Util.getMD5Code(sign.getBytes());
		return sign;
	}

}
