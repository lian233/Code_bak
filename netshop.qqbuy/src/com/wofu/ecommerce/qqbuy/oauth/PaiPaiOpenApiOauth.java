package com.wofu.ecommerce.qqbuy.oauth;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import com.wofu.common.tools.util.log.Log;

public class PaiPaiOpenApiOauth{
	
	private long uin;
	private String appOAuthID;
    private String appOAuthkey;
    private String accessToken;
    private String hostName="api.buy.qq.com";
    private String format="xml";
    private String charset="gbk";
    private String method = "get";	//���󷽷�
    private HashMap<String, Object> params;
    

    private static PaiPaiOpenApiOauth oauth;
	private String apiPath;
    
    public PaiPaiOpenApiOauth(String appOAuthID,String appOAuthkey,String accessToken,long uin) {
		this.appOAuthID = appOAuthID;
		this.appOAuthkey = appOAuthkey;
		this.accessToken = accessToken;
		this.uin = uin;
	}
    
	private PaiPaiOpenApiOauth(){
    }
    
    public static PaiPaiOpenApiOauth initial(String filePath){
    	if(oauth==null){
    		oauth = new PaiPaiOpenApiOauth();
			try {
				Properties pro = new Properties();
				FileInputStream stream = new FileInputStream(filePath);
				pro.load(stream);
				oauth.appOAuthID = pro.getProperty("appid");
				oauth.appOAuthkey = pro.getProperty("appkey");
				oauth.accessToken = pro.getProperty("accessToken");
				oauth.uin = Long.parseLong(pro.getProperty("uin"));
				oauth.hostName = pro.getProperty("hostName");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				oauth = null;
				throw new RuntimeException(e);
			} catch (IOException e) {
				e.printStackTrace();
				oauth = null;
				throw new RuntimeException(e);
			}
    	}
    	return oauth;
    }
    
    
	public HashMap<String, Object> getParams(String apiPath) {
		this.apiPath = apiPath;
		params = new HashMap<String, Object>();
		params.put("randomValue", String.valueOf(((long)(Math.random()*100000+11229))));
		params.put("timeStamp", System.currentTimeMillis() + "");
		return params;
	}
    
    /**
     * ִ��API����
     * 
     * @param apiName OpenApi CGI, ���� /deal/sellerSearchDealList.xhtml
     * @param protocol HTTP����Э�� "http" / "https"
     * @return ���ط�������Ӧ����
     */
    private String invokeOpenApi() throws OpenApiException{
		// Ĭ�ϲ���ϵͳ�Զ�����
    	params.put("appOAuthID", appOAuthID);
		params.put("accessToken", accessToken);
		params.put("uin", String.valueOf(uin));
		params.put("format", format);
		params.put("charset", charset);
		
    	// ָ��HTTP����Э������: "http" / "https"
		String protocol = "http";
        // ǩ����Կ
        String secret = appOAuthkey + "&";
        // ����ǩ��
        String sig = makeSign(method, secret);
        //System.out.println("\n\n������signֵ��\n"+sig);
        //System.out.println("\n\n��URL��signֵ��\n"+encodeUrl(sig));
        //sign,�Զ�����
        params.put("sign", sig);
        //ƴ��URL ���������ַ
        String url = protocol+"://"+hostName+apiPath; 
        // cookie
        HashMap<String, String> cookies = null;
        // ��������
        String resp = null;
        if(method.equals("post")){
        	resp = postRequest(url, cookies, protocol);        	
        }else{
        	resp = getRequest(url, cookies, protocol);
        }
        //�����ص������
        return resp;
    }

    public String invoke() throws OpenApiException{
    	String res = invokeOpenApi();
    	if(format.equals("xml")){
    		
    	}else if(format.equals("json")){
    		
    	}else{
    		throw new RuntimeException("fromat["+format+"]error!");
    	}
    	return res;
    }


    private String getRequest(String url,HashMap<String, String> cookies, String protocol) {
		
		HttpClient httpClient = new ContentEncodingHttpClient();
		try {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			NameValuePair pair;
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				Object obj = params.get(key);
				if(obj instanceof Arrays){
					String arr[] = (String[]) obj;
					for (String value : arr) {
						pair = new BasicNameValuePair(key, value);
						parameters.add(pair);
					}
				}else if(obj instanceof String){
					pair = new BasicNameValuePair(key, (String)obj);
					parameters.add(pair);
				}else{
					throw new RuntimeException("http get not support parameter");
				}
			}
			HttpEntity entity = new UrlEncodedFormEntity(parameters, charset);
			
			try {
				InputStream stream = entity.getContent();
            	ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = stream.read(buffer)) != -1)
                {
                	byteStream.write(buffer, 0, len);
                }
                
                url = url +"?"+ new String(byteStream.toByteArray());
            }catch (RuntimeException e) {
                throw e;
            }

			//System.out.println("���ɵ����get������URL:\n"+url);
            
			HttpGet get = new HttpGet(url);
	    	get.addHeader("User-Agent", "PaiPai API Invoker/Java " + System.getProperty("java.version"));
	    	get.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			
		    // �������󲢽���XML��Ӧ��
	        HttpResponse response = httpClient.execute(get);
	        
	        if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
	        	return "HTTPЭ�����" + response.getStatusLine() + "��";
	        }

	        HttpEntity responseEntity = response.getEntity();
	        String lastResponseContentType;
	        byte[] lastResponseContent;
			if (responseEntity != null) {
	        	Header header = responseEntity.getContentType();
	        	if(header!=null)
	        		lastResponseContentType = header.getValue();
	        	else
	        		lastResponseContentType = "text/html";
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
	                
	                return (new String(lastResponseContent, charset));
	            }catch (RuntimeException e) {
	                get.abort();
	                throw e;
	            }finally {
	                stream.close();
	            }
	        }else{
	        	lastResponseContentType = null;
	        	Header header = response.getFirstHeader("Content-Type");
	        	if(header != null) {
	        		lastResponseContentType = header.getValue();
	        	}
	        	lastResponseContent = null;
	        }
			return (lastResponseContentType);
			
			
		} catch (ClientProtocolException e) {
			Log.error("getRequest", "��������ʧ��,������Ϣ:"+e.getMessage()) ;
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
			Log.error("getRequest", "��������ʧ��,������Ϣ:"+e.getMessage()) ;
		}
    	
    	
		return null;
	}

	/* ����ǩ��
     * @param method HTTP���󷽷� "get" / "post"
     * @param url_path CGI����, 
     * @param params URL�������
     * @param secret ��Կ
     * @return ǩ��ֵ
     * @throws OpensnsException ��֧��ָ�������Լ���֧��ָ���ļ��ܷ���ʱ�׳��쳣��
     */
    private String makeSign(String method, String secret) throws OpenApiException{
    	String sig ="";
        try{
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(charset), mac.getAlgorithm());
            mac.init(secretKey);
            String mk = makeSource(method, apiPath);
           // System.out.println("\n\n���ڼ���sign��Դ����\n"+mk);
            byte[] hash = mac.doFinal(mk.getBytes(charset));
            sig = new String(Base64Coder.encode(hash));
//            sig = encodeUrl(sig);
        }catch(Exception e){
            throw new OpenApiException(OpenApiException.MAKE_SIGNATURE_ERROR, e);
        }
        return sig;
    }

    /* 
     * URL���� (����FRC1738�淶)
     * @param input ��������ַ���
     * @return �������ַ���
     * @throws OpenApiException ��֧��ָ������ʱ�׳��쳣��
     */
    private String encodeUrl(String input) throws OpenApiException{
        try{
            return URLEncoder.encode(input, charset).replace("+", "%20").replace("*", "%2A");
        }catch(UnsupportedEncodingException e){
            throw new OpenApiException(OpenApiException.MAKE_SIGNATURE_ERROR, e);
        }
    }

    /* ����ǩ������Դ��
     * @param method HTTP���󷽷� "get" / "post"
     * @param url_path CGI����, 
     * @param params URL�������
     * @return ǩ������Դ��
     */
    private String makeSource(String method, String url_path) throws OpenApiException, UnsupportedEncodingException{
        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);  
        StringBuilder buffer = new StringBuilder(128);
        buffer.append(method.toUpperCase()).append("&").append(encodeUrl(url_path)).append("&");
        StringBuilder buffer2= new StringBuilder();
        for(int i=0; i<keys.length; i++){  
            buffer2.append(keys[i]).append("=").append(params.get(keys[i]));//new String(params.get(keys[i]).getBytes(),charset));
            if (i!=keys.length-1){
                buffer2.append("&");
            }
        }   
       // System.out.println("\n\n��������в�����\n"+buffer2);
        buffer.append(encodeUrl(buffer2.toString()));
        return buffer.toString();
    }
    
    

	/*
	 * ����POST����
	 * @param url ����URL��ַ
	 * @param params �������
	 * @param protocol ����Э�� "http" / "https"
	 * @return ��������Ӧ��������
	 * @throws OpenApiException �������ʱ�׳��쳣��
	 */
	private String postRequest(String url, HashMap<String, String> cookies, String protocol) throws OpenApiException {
//		url=url+"?charset="+charset;
		System.out.println("==================url="+url);
		
		HttpClient httpClient = new ContentEncodingHttpClient();
    	HttpPost post = new HttpPost(url);
    	post.addHeader("User-Agent", "PaiPai API Invoker/Java " + System.getProperty("java.version"));
    	post.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
    	
    	
		try {
			
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			NameValuePair pair;
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
//				if("charset".equalsIgnoreCase(key)){
//					continue;
//				}
//				pair = new BasicNameValuePair(key, params.get(key));
//				parameters.add(pair);
				Object obj = params.get(key);
				if(obj instanceof Arrays){
					String arr[] = (String[]) obj;
					for (String value : arr) {
						pair = new BasicNameValuePair(key, value);
						parameters.add(pair);
					}
				}else if(obj instanceof String){
					pair = new BasicNameValuePair(key, (String)obj);
					parameters.add(pair);
				}else{
					throw new RuntimeException("http get not support parameter");
				}
			}
			HttpEntity entity = new UrlEncodedFormEntity(parameters, charset);
			post.setEntity(entity);
			
	        HttpResponse response = httpClient.execute(post);
	        
	        if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
	        	return "HTTPЭ�����" + response.getStatusLine() + "��";
	        }

	        HttpEntity responseEntity = response.getEntity();
	        String lastResponseContentType;
	        byte[] lastResponseContent;
			if (responseEntity != null) {
	        	Header header = responseEntity.getContentType();
	        	if(header!=null)
	        		lastResponseContentType = header.getValue();
	        	else
	        		lastResponseContentType = "text/html";
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
	                
	                return (new String(lastResponseContent, charset));
	            }catch (RuntimeException e) {
	                post.abort();
	                throw e;
	            }finally {
	                stream.close();
	            }
	        }else{
	        	lastResponseContentType = null;
	        	Header header = response.getFirstHeader("Content-Type");
	        	if(header != null) {
	        		lastResponseContentType = header.getValue();
	        	}
	        	lastResponseContent = null;
	        }
			return (lastResponseContentType);
			
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		
		return "";
	}
    
    
    
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}


	public String getFormat() {
		return format;
	}


	public void setFormat(String format) {
		this.format = format;
	}


	public String getCharset() {
		return charset;
	}


	public void setCharset(String charset) {
		this.charset = charset;
	}


	public String getMethod() {
		return method;
	}


	public void setMethod(String method) {
		this.method = method;
	}

	public long getUin(){
		return uin;
	}
	
    
}
