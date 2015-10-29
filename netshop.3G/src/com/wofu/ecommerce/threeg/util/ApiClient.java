/**
 * Copyright (C) 1998-2009 TENCENT Inc.All Rights Reserved.		
 * 																	
 * FileName：ApiClient.java					
 *			
 * Description：API客户端，可使用该类型方便地操作API协议
 * History：
 *  2.0  2010-02-26        修改API协议的调用方式以及sign校验方式。不兼容1.0的接口。
 *  1.2  2009-05-27        新增getLastResponseBody方法
 *  1.2  2009-05-25        修正了invoke方法的一个bug，该bug导致读取带换行的response时，校验sign失败
 *  1.1  2009-04-02        增加debug开关，以便关闭Debug信息
 *  1.0  2009-04-02        Create
 */

package com.wofu.ecommerce.threeg.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import com.wofu.common.tools.util.log.Log;



/** 
 * API客户端，用于操作拍拍API协议。<br>
 * ApiClient不作多线程安全保证，多线程编程建议不要使用同一个ApiClient对象。
 * 
 * @author hokyhu
 * @version 2.0
 */

public class ApiClient
{
	private String charset = "gbk";

	private HttpClient httpClient = new DefaultHttpClient();

    byte[] lastResponseContent = null;	// 最后一个成功返回的响应包内容
    String lastResponseContentType;

	private long lastErrNum = 0;
    private String lastErrMsg = "";
    
    private String requestData="";
    
    public String getLastErrMsg()
    {
    	return lastErrMsg;
    }
    
    public long getLastErNum()
    {
    	return lastErrNum;
    }

    public void setCharset(String charset) {
    	if(!Charset.isSupported(charset)) {
    		throw new UnsupportedCharsetException(charset);
    	}
    	this.charset = charset;
    }  
    
    public void setRequestData(String data)
    {
    	requestData=data;
    }

    /**
     * 取得前一次成功的API调用返回的响应包的内容
     * @return 前一次成功的API调用返回的响应包的内容
     */
    public byte[] getLastResponseContent()
    {
    	return lastResponseContent;
    }
    
    public String getLastResponseContentType()
    {
    	return lastResponseContentType;
    }

    public ApiClient()
    {

    }

    /**
     * 运行API协议，提交请求到服务器，并获得响应包<br>
     * @param apiUrl	API协议文档指定的提交目标URL<br>
     * @param params	请求参数<br>
     * @return API执行成功，则返回true；失败，则返回false。<br>
     * 可通过getLastErrMsg()函数查询失败原因。<br>
     * getLastErrNum()函数可查到对应的错误码。但如果是是客户端错误导致异常中断，此函数总是返回0.
     */
    public boolean invokeApi(String apiUrl)
    	throws UnsupportedEncodingException, IOException
    {
    	lastErrNum = 0;
    	
    	int pos = apiUrl.indexOf('?');
    	if(pos > 0) {
    		apiUrl = apiUrl.substring(0, pos);	// 不要带参数的URL
    	}
		//apiUrl += "?charset=" + charset;	// charset参数必须带在url里面以GET方法传递
		
    	HttpPost post = new HttpPost(apiUrl);
    	
    	post.addHeader("User-Agent", "3G API Invoker/Java " + System.getProperty("java.version"));
    	post.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);

 
    	//Charset charsetObj = Charset.forName(charset);
    	//MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, charsetObj);
    	//entity.addPart("msg", new StringBody(requestData, charsetObj));
    	
    	//post.setEntity(new StringEntity(requestData)); 
    	//post.setEntity(entity);
    
	    //post.addHeader(ReservedName.SIGN.toString(), makeSign(signParams, secretKey, charset, debug));
    	//post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=GBK");
       //post.addHeader("Connection", "Keep-Alive");
        //post.addHeader("Content-Length", String.valueOf(requestData.length()));	  
        
       	List<NameValuePair> params=new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("msg",requestData));

    	HttpEntity httpEntity=new UrlEncodedFormEntity(params,"gb2312");
    	post.setEntity(httpEntity);
    	
    	
        // 发送请求并接收XML响应包	   
       
        HttpResponse response = httpClient.execute(post);
     
        if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        	lastErrMsg = "HTTP协议出错：" + response.getStatusLine() + "。";
        	return false;
        }

  
        // Get hold of the response entity
        HttpEntity responseEntity = response.getEntity();

        // If the response does not enclose an entity, there is no need
        // to worry about connection release
        if (responseEntity != null) {
        	if(responseEntity.getContentType()!=null)
        		lastResponseContentType = responseEntity.getContentType().getValue();
            InputStream stream = responseEntity.getContent();
            try {
            
            	ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = stream.read(buffer)) != -1)
                {
            
                	byteStream.write(buffer, 0, len);
              
                }
                lastResponseContent = byteStream.toByteArray();
            }
            catch (RuntimeException e) {
                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying 
                // connection and release it back to the connection manager.
                post.abort();
                throw e;
                
            }
            finally {
                // Closing the input stream will trigger connection release
                stream.close();
            }
        }
        else {
        	lastResponseContentType = null;
        	Header header = response.getFirstHeader("Content-Type");
        	if(header != null) {
        		lastResponseContentType = header.getValue();
        	}
        	lastResponseContent = null;
        }

        return true;
    }
   	
}


