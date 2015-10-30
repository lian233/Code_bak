package com.wofu.ecommerce.dangdang.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class CommHelper {
	
	public static String sendRequest(String url, String requestmethod,
			Map params, String requestData) throws Exception {
		String responsetext="";
		if(requestmethod.equalsIgnoreCase("GET"))
		{
			url=url+"&";
			//拼装请求参数
			for (Iterator it = params.keySet().iterator(); it.hasNext();) {
				String paramname = (String) it.next();
				String paramvalue = (String) params.get(paramname);
				url=url+paramname+"="+paramvalue+"&";				
			}
			url=url.substring(0, url.length()-1);
			//Log.info("url: "+url);
			responsetext= getData(url);
		}
		else if (requestmethod.equalsIgnoreCase("POST"))
		{
			String formfieldname=requestData.substring(0, requestData.indexOf("="));
			String xmldata=requestData.substring(requestData.indexOf("=")+1, requestData.length());
			responsetext= postData(url,params,formfieldname,xmldata);
		}
		else
		{
			throw new JException("未知的请求方法:"+requestmethod);
		}
		return responsetext;
	}

	private static String getData(String urlstr)
			throws Exception {
		StringBuffer responsecontent = new StringBuffer();
		URL url = new URL(urlstr);
		URLConnection rulConnection = url.openConnection();
		HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
		//GET方法不需要此设置
		//httpUrlConnection.setDoOutput(true);
		httpUrlConnection.setDoInput(true);
		httpUrlConnection.setUseCaches(false);

		httpUrlConnection.setRequestMethod("GET");
		httpUrlConnection.connect();

		//OutputStream outStrm = httpUrlConnection.getOutputStream();
		//DataOutputStream dataoutstream = new DataOutputStream(outStrm);
		//dataoutstream.flush();
		//dataoutstream.close();

		InputStream inStrm = httpUrlConnection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStrm, "GBK"));

		String line = "";
		while ((line = reader.readLine()) != null) {
			responsecontent.append(line);
		}

		inStrm.close();
		return responsecontent.toString();
	}

	private static String postData(String urlstr,
			Map params, String formfieldname,String xmldata) throws Exception {
		
		StringBuffer responsecontent = new StringBuffer();
		StringBuilder sb = new StringBuilder();
		String BOUNDARY = "---------7d4a6d158c9"; // 定义数据分隔线
		URL url = new URL(urlstr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 发送POST请求必须设置如下两行
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("Charsert", "GBK");
		conn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + BOUNDARY);
		OutputStream out = new DataOutputStream(conn.getOutputStream());
		byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线

		for (Iterator it = params.keySet().iterator(); it.hasNext();) {
			String paramname = (String) it.next();

			String paramvalue = (String) params.get(paramname);

			sb.append("--");
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"" + paramname
					+ "\"\r\n\r\n");
			sb.append(paramvalue);
			sb.append("\r\n");
		}

		String filename=formfieldname+".xml";
		
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"" + formfieldname
				+ "\";filename=\"" + filename + "\"\r\n");
		sb.append("Content-Type:text/xml\r\n\r\n");
		byte[] data = sb.toString().getBytes();
		out.write(data);
	
		out.write(xmldata.getBytes());
		
		//out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
	
		out.write(end_data);
		out.flush();
		out.close();
		
		// 定义BufferedReader输入流来读取URL的响应
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn
				.getInputStream(),"gbk"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			responsecontent.append(line);
		}
		return responsecontent.toString();
	}
	
	/**
	 * 2013.12.13
	 * 生成签名
	 * @param app_Secret  app_Secret
	 * @param app_key     app_key
	 * @param methodName  方法名
	 * @param session     session
	 * @return            加密过的签名
	 */
	public static String getSign(String app_Secret,String app_key,String methodName,String session,Date temp){
		StringBuffer str= new StringBuffer();
		String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(temp);
		str.append(app_Secret)
		   .append("app_key")
		   .append(app_key)
		   .append("formatxmlmethod")
		   .append(methodName)
		   .append("session").append(session)
		   .append("sign_methodmd5")
		   .append("timestamp")
		   .append(date)
		   .append("v1.0")
		   .append(app_Secret);
		String gbkValueStr=str.toString();
		//生成验证码 --md5;加密
		String sign = MD5Util.getMD5Code(gbkValueStr.getBytes()).toUpperCase() ;
		return sign;
	}
	
	public static String filterChar(String xml) throws Exception{
		StringBuilder sb = new StringBuilder();
		char[] arr = xml.toCharArray();
		for(char e:arr){
			if((int)e<=0x1f) e='*';
			sb.append(e);
		}
		return sb.toString();
	}

}
