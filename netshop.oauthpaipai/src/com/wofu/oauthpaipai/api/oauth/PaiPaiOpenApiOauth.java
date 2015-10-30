package com.wofu.oauthpaipai.api.oauth;

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





public class PaiPaiOpenApiOauth{
	
	private long uin;
	private String appOAuthID;
    private String appOAuthkey;
    private String accessToken;
    private String hostName="api.paipai.com";
    private String format="xml";
    private String charset="gbk";
    private String method = "get";	//请求方法
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
     * 执行API调用
     * 
     * @param apiName OpenApi CGI, 例如 /deal/sellerSearchDealList.xhtml
     * @param protocol HTTP请求协议 "http" / "https"
     * @return 返回服务器响应内容
     */
    private String invokeOpenApi() throws OpenApiException{
		// 默认参数系统自动设置
    	params.put("appOAuthID", appOAuthID);
		params.put("accessToken", accessToken);
		params.put("uin", String.valueOf(uin));
		params.put("format", format);
		params.put("charset", charset);
		
    	// 指定HTTP请求协议类型: "http" / "https"
		String protocol = "http";
        // 签名密钥
        String secret = appOAuthkey + "&";
        // 计算签名
        String sig = makeSign(method, secret);
        //System.out.println("\n\n计算后的sign值：\n"+sig);
        //System.out.println("\n\n在URL中sign值：\n"+encodeUrl(sig));
        //sign,自动生成
        params.put("sign", sig);
        //拼成URL
        String url = protocol+"://"+hostName+apiPath; 
        // cookie
        HashMap<String, String> cookies = null;
        // 发送请求
        String resp = null;
        if(method.equals("post")){
        	resp = postRequest(url, cookies, protocol);        	
        }else{
        	resp = getRequest(url, cookies, protocol);
        }
        //处理返回的请求包
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
//				if(obj instanceof Arrays){
				if(obj.getClass().isArray()){
					String arr[] = (String[]) obj;
					for (String value : arr) {
						pair = new BasicNameValuePair(key, value);
						parameters.add(pair);
					}
				}else if(obj instanceof String){
					pair = new BasicNameValuePair(key, (String)obj);
					parameters.add(pair);
				}else{
					//System.out.println("key="+key);
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

			//System.out.println("\n\n生成的完成get方法的URL:\n"+url);
			HttpGet get = new HttpGet(url);
	    	get.addHeader("User-Agent", "PaiPai API Invoker/Java " + System.getProperty("java.version"));
	    	get.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			
		    // 发送请求并接收XML响应包
	        HttpResponse response = httpClient.execute(get);
	        
	        if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
	        	return "HTTP协议出错：" + response.getStatusLine() + "。";
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	
		return null;
	}

	/* 生成签名
     * @param method HTTP请求方法 "get" / "post"
     * @param url_path CGI名字, 
     * @param params URL请求参数
     * @param secret 密钥
     * @return 签名值
     * @throws OpensnsException 不支持指定编码以及不支持指定的加密方法时抛出异常。
     */
	private String makeSign(String method, String secret)
			throws OpenApiException {
		String sig = "";
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec secretKey = new SecretKeySpec(
					secret.getBytes(charset), mac.getAlgorithm());
			mac.init(secretKey);
			String mk = makeSource(method, apiPath);
			//System.out.println("\n\n用于计算sign的源串：\n" + mk);
			byte[] hash = mac.doFinal(mk.getBytes(charset));
			sig = new String(Base64Coder.encode(hash));
			// sig = encodeUrl(sig);
		} catch (Exception e) {
			throw new OpenApiException(OpenApiException.MAKE_SIGNATURE_ERROR, e);
		}

		return sig;
	}

    /* 
     * URL编码 (符合FRC1738规范)
     * @param input 待编码的字符串
     * @return 编码后的字符串
     * @throws OpenApiException 不支持指定编码时抛出异常。
     */
    private String encodeUrl(String input) throws OpenApiException{
        try{
            return URLEncoder.encode(input, charset).replace("+", "%20").replace("*", "%2A");
        }catch(UnsupportedEncodingException e){
            throw new OpenApiException(OpenApiException.MAKE_SIGNATURE_ERROR, e);
        }
    }

    /* 生成签名所需源串
     * @param method HTTP请求方法 "get" / "post"
     * @param url_path CGI名字, 
     * @param params URL请求参数
     * @return 签名所需源串
     */
    private String makeSource(String method, String url_path) throws OpenApiException, UnsupportedEncodingException{
        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);  
        StringBuilder buffer = new StringBuilder(128);
        buffer.append(method.toUpperCase()).append("&").append(encodeUrl(url_path)).append("&");
        StringBuilder buffer2= new StringBuilder();
        for(int i=0; i<keys.length; i++){  
            buffer2.append(keys[i]).append("=").append(getValue(params.get(keys[i])));//new String(params.get(keys[i]).getBytes(),charset));
            if (i!=keys.length-1){
                buffer2.append("&");
            }
        }   
        //System.out.println("\n\n请求的所有参数：\n"+buffer2);
        buffer.append(encodeUrl(buffer2.toString()));
        
        //oidb按1024最大长度截取
		//buffer = buffer.length() > 1024 ? buffer.delete(1024, buffer.length())
		//		: buffer;

		return buffer.toString();
    }
    
	private String getValue(Object value) {
		if (value.getClass().isArray()) {
			String valueStr = "";
			String[] items = (String[]) value;
			Arrays.sort(items);

			for (String item : items) {
				valueStr += item;
			}

			return valueStr;
		} else {
			return value.toString();
		}
	}

	/*
	 * 发送POST请求
	 * @param url 请求URL地址
	 * @param params 请求参数
	 * @param protocol 请求协议 "http" / "https"
	 * @return 服务器响应的请求结果
	 * @throws OpenApiException 网络故障时抛出异常。
	 */
	private String postRequest(String url, HashMap<String, String> cookies, String protocol) throws OpenApiException {
//		url=url+"?charset="+charset;
//		System.out.println(url);
		
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
//				if(obj instanceof Arrays){
				if(obj.getClass().isArray()){
					String arr[] = (String[]) obj;
					for (String value : arr) {
						pair = new BasicNameValuePair(key, value);
						parameters.add(pair);
					}
//					pair = new BasicNameValuePair(key, getValue(obj));
//					parameters.add(pair);
				}else if(obj instanceof String){
					pair = new BasicNameValuePair(key, (String)obj);
					parameters.add(pair);
				}else{
					//System.out.println(obj.toString());
					throw new RuntimeException("http get not support parameter");
				}
			}
			HttpEntity entity = new UrlEncodedFormEntity(parameters, charset);
			post.setEntity(entity);
			
	        HttpResponse response = httpClient.execute(post);
	        
	        if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
	        	return "HTTP协议出错：" + response.getStatusLine() + "。";
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
