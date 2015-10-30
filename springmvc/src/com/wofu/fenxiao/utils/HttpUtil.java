package com.wofu.fenxiao.utils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import com.wofu.common.tools.util.log.Log;
public class HttpUtil {
	public static String sendRequest(String url, Map params, String s1)
	throws Exception
{
	String result="";
	try{
		
		HttpClient httpclient = new HttpClient();
		httpclient.getParams().setIntParameter("http.socket.timeout",30000);
		PostMethod postmethod = new PostMethod(url);
		postmethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,30000); 
		if(null!=s1){//汇通
			String digest =DeliveryInfoUtil.makeSign(params,s1);
			params.put("digest", digest);
		}
		for (Iterator iterator = params.keySet().iterator(); iterator.hasNext(); )
		{
			String paramname = (String)iterator.next();
			String paramvalue;
			//if(null!=s1)
				paramvalue= (String)params.get(paramname);
			//else
				//paramvalue = URLEncoder.encode((String)params.get(paramname),"utf-8");
			//Log.info(paramname+" "+paramvalue);
			
			postmethod.addParameter(paramname, paramvalue);
		}
		
		postmethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		int i = httpclient.executeMethod(postmethod);
		//if (i == 200)
		//{
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(postmethod.getResponseBodyAsStream(), "UTF-8"));
			for (String line = null; (line = bufferedreader.readLine()) != null;)
				result = result.concat(line);
		//}
		result = URLDecoder.decode(result, "UTF-8");
	}catch(Exception ex){
		ex.printStackTrace();
	}
	return result;
	
}
	
	
	public static String sendRequest(String url, Map params, String s1,String encoding)
	throws Exception
{
	String result="";
	try{
		
		HttpClient httpclient = new HttpClient();
		httpclient.getParams().setIntParameter("http.socket.timeout",30000);
		PostMethod postmethod = new PostMethod(url);
		postmethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,30000); 
		if(null!=s1){//
			String digest =DeliveryInfoUtil.makeSign(params,s1);
			params.put("digest", digest);
		}
		for (Iterator iterator = params.keySet().iterator(); iterator.hasNext(); )
		{
			String paramname = (String)iterator.next();
			String paramvalue;
			//if(null!=s1)
				paramvalue= (String)params.get(paramname);
			//else
				//paramvalue = URLEncoder.encode((String)params.get(paramname),"utf-8");
			//Log.info(paramname+" "+paramvalue);
			
			postmethod.addParameter(paramname, paramvalue);
		}
		
		postmethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		int i = httpclient.executeMethod(postmethod);
		//if (i == 200)
		//{
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(postmethod.getResponseBodyAsStream(), encoding));
			for (String line = null; (line = bufferedreader.readLine()) != null;)
				result = result.concat(line);
		//}
		result = URLDecoder.decode(result, "UTF-8");
	}catch(Exception ex){
		ex.printStackTrace();
	}
	return result;
	
} 
	
	//发送get请求
	public static String sendByGet(String url,HashMap<String,String> params)throws Exception{
		String result = "";
		HttpClient client = new HttpClient();
		StringBuilder requestUrl = new StringBuilder(url).append("?");
		for(Iterator it = params.keySet().iterator();it.hasNext();){
			String name = (String)it.next();
			requestUrl.append(name).append("=").append(params.get(name).toString()).append("&");
		}
		GetMethod  get = new GetMethod(requestUrl.deleteCharAt(requestUrl.length()-1).toString());
		int statusCode = client.executeMethod(get);
		if(200==statusCode){
			result = get.getResponseBodyAsString();
		}
		System.out.println("result: "+result);
		return result;
	}
	
	public static String sendByGetT(Map params,String url,boolean needSign,String sercetKey) throws Exception{
		InputStream in=null;
		BufferedReader sr=null;
		String line =null;
		StringBuilder result =new StringBuilder();
		HttpClient client=null;
		if(needSign){
			String sign = getSign(params,sercetKey);
			params.put("sign", sign);
		}
		try{
			client = new HttpClient();
			StringBuilder sender = new StringBuilder(url+"?");
			Iterator it = params.keySet().iterator();
			for(;it.hasNext();){
				String name = (String)it.next();
				String value = URLEncoder.encode((String)params.get(name),"utf-8");
				sender.append(name).append("=").append(value).append("&");
			}
			Log.info("s: "+sender.toString());
			GetMethod get = new GetMethod(sender.toString().substring(0,sender.toString().length()-1));
			int responseCode = client.executeMethod(get);
			in = get.getResponseBodyAsStream();
			sr = new BufferedReader(new InputStreamReader(in));
			for(line=sr.readLine();line!=null;result.append(line),line=sr.readLine());
			return result.toString();
		}catch(Exception e){
			Log.error("发送http请求出错", e.getMessage());
		}finally{
			if(sr!=null) sr.close();
			if(in!=null) in.close();
			client=null;
			
		}
		return result.toString();
		
		
	}

	private static String getSign(Map params,String sercetKey) throws Exception {
		TreeMap map = new TreeMap();
		map.putAll(params);
		StringBuilder sb = new StringBuilder(sercetKey);
		for(Iterator it = map.keySet().iterator();it.hasNext();){
			String name = (String)it.next();
			String value = (String)map.get(name);
			sb.append(name).append(value);
		}
		sb.append(sercetKey);
		return md5Tools(sb.toString());
	}
	
	private static String md5Tools(String str) throws Exception{
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(str.getBytes("utf-8"));
		return ByeToHex(digest.digest());
	}

	//二进制转16进制
	private static String ByeToHex(byte[] digest) {
		StringBuilder sb = new StringBuilder();
		for(byte e:digest){
			if(e>=0 && e<16){
				sb.append("0");
			}
			sb.append(Integer.toHexString(e & 0xFF).toUpperCase());
		}
		return sb.toString();
	}
	
}
