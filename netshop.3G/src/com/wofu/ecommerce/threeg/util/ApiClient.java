/**
 * Copyright (C) 1998-2009 TENCENT Inc.All Rights Reserved.		
 * 																	
 * FileName��ApiClient.java					
 *			
 * Description��API�ͻ��ˣ���ʹ�ø����ͷ���ز���APIЭ��
 * History��
 *  2.0  2010-02-26        �޸�APIЭ��ĵ��÷�ʽ�Լ�signУ�鷽ʽ��������1.0�Ľӿڡ�
 *  1.2  2009-05-27        ����getLastResponseBody����
 *  1.2  2009-05-25        ������invoke������һ��bug����bug���¶�ȡ�����е�responseʱ��У��signʧ��
 *  1.1  2009-04-02        ����debug���أ��Ա�ر�Debug��Ϣ
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
 * API�ͻ��ˣ����ڲ�������APIЭ�顣<br>
 * ApiClient�������̰߳�ȫ��֤�����̱߳�̽��鲻Ҫʹ��ͬһ��ApiClient����
 * 
 * @author hokyhu
 * @version 2.0
 */

public class ApiClient
{
	private String charset = "gbk";

	private HttpClient httpClient = new DefaultHttpClient();

    byte[] lastResponseContent = null;	// ���һ���ɹ����ص���Ӧ������
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
     * ȡ��ǰһ�γɹ���API���÷��ص���Ӧ��������
     * @return ǰһ�γɹ���API���÷��ص���Ӧ��������
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
     * ����APIЭ�飬�ύ���󵽷��������������Ӧ��<br>
     * @param apiUrl	APIЭ���ĵ�ָ�����ύĿ��URL<br>
     * @param params	�������<br>
     * @return APIִ�гɹ����򷵻�true��ʧ�ܣ��򷵻�false��<br>
     * ��ͨ��getLastErrMsg()������ѯʧ��ԭ��<br>
     * getLastErrNum()�����ɲ鵽��Ӧ�Ĵ����롣��������ǿͻ��˴������쳣�жϣ��˺������Ƿ���0.
     */
    public boolean invokeApi(String apiUrl)
    	throws UnsupportedEncodingException, IOException
    {
    	lastErrNum = 0;
    	
    	int pos = apiUrl.indexOf('?');
    	if(pos > 0) {
    		apiUrl = apiUrl.substring(0, pos);	// ��Ҫ��������URL
    	}
		//apiUrl += "?charset=" + charset;	// charset�����������url������GET��������
		
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
    	
    	
        // �������󲢽���XML��Ӧ��	   
       
        HttpResponse response = httpClient.execute(post);
     
        if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        	lastErrMsg = "HTTPЭ�����" + response.getStatusLine() + "��";
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


