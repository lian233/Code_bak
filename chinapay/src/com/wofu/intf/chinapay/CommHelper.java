package com.wofu.intf.chinapay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import chinapay.PrivateKey;
import chinapay.SecureLink;

import com.wofu.common.tools.util.log.Log;

public class CommHelper
{

	public CommHelper()
	{
	}

	public static String sendRequest(String url, Map params, String s1)
		throws Exception
	{
		String result="";
		try{
			
			HttpClient httpclient = new HttpClient();
			httpclient.getParams().setIntParameter("http.socket.timeout",30000);
			PostMethod postmethod = new PostMethod(url);
			postmethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,30000); 
			
			for (Iterator iterator = params.keySet().iterator(); iterator.hasNext(); )
			{
				String paramname = (String)iterator.next();
				String paramvalue = (String)params.get(paramname);
				
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
	// MerId+OrdId+TransAmt+CuryId+TransDate+TransType+Version+PageRetUrl+BgRetUrl+GateId+Priv1+ BusiType+
	public static String sign(PayData d){
		//工程路径 
		//String path = System.getProperty("user.dir");
		//Log.info(path);
		//创建钥对象
		PrivateKey privateKey= new PrivateKey();
		//先写固定
		privateKey.buildKey(d.getMerId(),0,"c:\\ecommerce\\jdk1.6.0_22\\MerPrK.key");
		StringBuilder sb = new StringBuilder();
		sb.append(d.getMerId()).append(d.getOrdId()).append(d.getTransAmt())
			.append(d.getCuryId()).append(d.getTransDate()).append(d.getTransType())
			.append(d.getVersion()).append(d.getPageRetUrl()).append(d.getBgRetUrl())
			.append(d.getGateId()).append(d.getPriv1()).append(d.getBusiType())
			.append(d.getExtParam1()).append(d.getExtParam2()).append(d.getExtParam3())
			.append(d.getExtParam4()).append(d.getExtParam5()).append(d.getExtParam6())
			.append(d.getExtParam7());
		//Log.info("sb: "+sb.toString());
		SecureLink secureLink = new SecureLink(privateKey);
		//加密
		return secureLink.Sign(sb.toString());
	}
	//生成unicode字符串
	public static String UnicodeString(String str){
		StringBuilder sb = new StringBuilder();
		char[] chars = str.toCharArray();
		String temp="";
		for(char e:chars){
			sb.append("\\u");
			temp = Integer.toHexString(e>>>8);
			if(temp.length()==1)
				sb.append("0");
			sb.append(temp);
			temp = Integer.toHexString(e &0XFF);
			if(temp.length()==1)
				sb.append("0");
			sb.append(temp);
			
		}
		return sb.toString();
	}
}
