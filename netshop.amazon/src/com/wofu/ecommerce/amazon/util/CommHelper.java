package com.wofu.ecommerce.amazon.util;

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
			//ƴװ�������
			for (Iterator it = params.keySet().iterator(); it.hasNext();) {
				String paramname = (String) it.next();
				String paramvalue = (String) params.get(paramname);
				url=url+paramname+"="+paramvalue+"&";				
			}
			url=url.substring(0, url.length()-1);
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
			throw new JException("δ֪�����󷽷�:"+requestmethod);
		}
		return responsetext;
	}

	private static String getData(String urlstr)
			throws Exception {
		StringBuffer responsecontent = new StringBuffer();
		URL url = new URL(urlstr);
		URLConnection rulConnection = url.openConnection();
		HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
		//GET��������Ҫ������
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
		String BOUNDARY = "---------7d4a6d158c9"; // �������ݷָ���
		URL url = new URL(urlstr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// ����POST�������������������
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("Charsert", "GBK");
		conn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + BOUNDARY);
		OutputStream out = new DataOutputStream(conn.getOutputStream());
		byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// ����������ݷָ���

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
		
		//out.write("\r\n".getBytes()); // ����ļ�ʱ�������ļ�֮��������
	
		out.write(end_data);
		out.flush();
		out.close();
		
		// ����BufferedReader����������ȡURL����Ӧ
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn
				.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			responsecontent.append(line);
		}
		return responsecontent.toString();
	}
	
	/**
	 * ����ǩ��
	 * @param str
	 * @return
	 */
	public String getSign(String str){
		return null;
	}

}
