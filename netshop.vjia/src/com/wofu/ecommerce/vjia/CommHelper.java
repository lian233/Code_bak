package com.wofu.ecommerce.vjia;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.wofu.common.tools.util.log.Log;

public class CommHelper {

	public static String getResponseData(String url,String lcData)
	{
		String responsexml = "";
		try
		{
			URL dataUrl = new URL(url);
			HttpURLConnection con = (HttpURLConnection) dataUrl.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Proxy-Connection", "Keep-Alive");
			con.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8") ;
			
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setReadTimeout(10*1000) ;
			OutputStream os=con.getOutputStream();
			DataOutputStream dos=new DataOutputStream(os);	
			dos.write(lcData.getBytes("UTF-8"));
			dos.flush();
			dos.close(); 
			InputStream is=con.getInputStream();
			DataInputStream dis=new DataInputStream(is);
			byte d[]=new byte[dis.available()];
			dis.read(d);
			responsexml=new String(d);
			con.disconnect();


		} catch (Exception e) {
			Log.error("", "更新订单配送结果失败,错误信息:"+e.getMessage()) ;
			//e.printStackTrace() ;
		}
		return responsexml ;
	}
	
}
