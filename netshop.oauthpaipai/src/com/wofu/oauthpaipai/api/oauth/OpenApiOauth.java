package com.wofu.oauthpaipai.api.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class OpenApiOauth {

    private String appOAuthID;
    private String appOAuthkey;
    private String accessToken;
    private long   uin;
    private String hostName = "api.paipai.com";
    private String format   = "xml";
    private String charset  = "gbk";
    // 请求方法
    private String method   = "get";

    public OpenApiOauth(String appOAuthID, String appOAuthkey, String accessToken, long uin){
        this.appOAuthID = appOAuthID;
        this.appOAuthkey = appOAuthkey;
        this.accessToken = accessToken;
        this.uin = uin;
    }

    @SuppressWarnings("unused")
    private OpenApiOauth(){
    }

    /**
     * 执行API调用
     * 
     * @param apiName OpenApi CGI, 例如 /deal/sellerSearchDealList.xhtml
     * @param params OpenApi的参数列表
     * @param protocol HTTP请求协议 "http" / "https"
     * @return 返回服务器响应内容
     */
    public String invokeOpenApi(String apiName, HashMap<String, String> params) throws OpenApiException {
        // 默认参数系统自动设置
        params.put("appOAuthID", appOAuthID);
        params.put("accessToken", accessToken);
        params.put("uin", String.valueOf(uin));

        params.put("format", format);
        if (format.equalsIgnoreCase("json")) {
            params.put("pureData", "1");
        }
        params.put("charset", charset);

        // 指定HTTP请求协议类型: "http" / "https"
        String protocol = "http";
        // 签名密钥
        String secret = appOAuthkey + "&";
        // 计算签名
        String sig = makeSign(method, apiName, params, secret);
        // sign,自动生成
        params.put("sign", sig);
        // 拼成URL
        String url = protocol + "://" + hostName + apiName;
        // cookie
        HashMap<String, String> cookies = null;
        // 发送请求
        String resp = null;
        if (method.equals("post")) {
            resp = postRequest(url, params, cookies, protocol);
        } else {
            resp = getRequest(url, params, cookies, protocol);
        }
        // 处理返回的请求包
        return resp;
    }

    /*
     * 生成签名
     * @param method HTTP请求方法 "get" / "post"
     * @param url_path CGI名字,
     * @param params URL请求参数
     * @param secret 密钥
     * @return 签名值
     * @throws OpensnsException 不支持指定编码以及不支持指定的加密方法时抛出异常。
     */
    private String makeSign(String method, String url_path, HashMap<String, String> params, String secret)
                                                                                                          throws OpenApiException {
        String sig = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(charset), mac.getAlgorithm());
            mac.init(secretKey);
            String mk = makeSource(method, url_path, params);
            System.out.println(mk);
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
    private String encodeUrl(String input) throws OpenApiException {
        try {
            return URLEncoder.encode(input, charset).replace("+", "%20").replace("*", "%2A");
        } catch (UnsupportedEncodingException e) {
            throw new OpenApiException(OpenApiException.MAKE_SIGNATURE_ERROR, e);
        }
    }

    /*
     * 生成签名所需源串
     * @param method HTTP请求方法 "get" / "post"
     * @param url_path CGI名字,
     * @param params URL请求参数
     * @return 签名所需源串
     */
    private String makeSource(String method, String url_path, HashMap<String, String> params) throws OpenApiException,
                                                                                             UnsupportedEncodingException {
        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);
        StringBuilder buffer = new StringBuilder(128);
        buffer.append(method.toUpperCase()).append("&").append(encodeUrl(url_path)).append("&");
        StringBuilder buffer2 = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            buffer2.append(keys[i]).append("=").append(params.get(keys[i]));// new
                                                                            // String(params.get(keys[i]).getBytes(),charset));
            if (i != keys.length - 1) {
                buffer2.append("&");
            }
        }
        System.out.println(buffer2);
        buffer.append(encodeUrl(buffer2.toString()));
        return buffer.toString();
    }

    /*
     * 发送Get请求
     * @param url 请求URL地址
     * @param params 请求参数
     * @param protocol 请求协议 "http" / "https"
     * @return 服务器响应的请求结果
     * @throws OpenApiException 网络故障时抛出异常。
     */
    private String getRequest(String url, HashMap<String, String> params, HashMap<String, String> cookies,
                              String protocol) throws OpenApiException {
        StringBuffer showUrl = new StringBuffer(url);
        showUrl.append("?");
        // 设置请求参数
        if (params != null && !params.isEmpty()) {
            Iterator<Entry<String, String>> iter = params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                showUrl.append(entry.getKey()).append("=").append(encodeUrl(entry.getValue())).append("&");
            }
        }
        showUrl.delete(showUrl.length() - 1, showUrl.length());
        System.out.println(showUrl);

        GetMethod getMethod = new GetMethod(showUrl.toString());
        HttpMethodParams methodParams = getMethod.getParams();
        // 设置User-Agent
        getMethod.setRequestHeader("User-Agent", "Java OpenApiOauth SDK Client");

        // 设置cookie
        if (cookies != null && !cookies.isEmpty()) {
            Iterator<Entry<String, String>> iter = cookies.entrySet().iterator();
            StringBuilder buffer = new StringBuilder(128);
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                buffer.append((String) entry.getKey()).append("=").append((String) entry.getValue()).append("; ");
            }
            // 设置cookie策略
            methodParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            // 设置cookie内容
            getMethod.setRequestHeader("Cookie", buffer.toString());
        }

        // 设置编码
        methodParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
        // 使用系统提供的默认的恢复策略
        methodParams.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

        try {
            try {
                HttpClient httpClient = new HttpClient();
                HttpConnectionManager connectionManager = httpClient.getHttpConnectionManager();
                HttpConnectionManagerParams connectionManagerParams = connectionManager.getParams();
                connectionManagerParams.setConnectionTimeout(3000);// 设置建立连接超时时间
                connectionManagerParams.setSoTimeout(3000);// 设置读数据超时时间

                int statusCode = httpClient.executeMethod(getMethod);

                if (statusCode != HttpStatus.SC_OK) {
                    throw new OpenApiException(OpenApiException.NETWORK_ERROR, "Request [" + url + "] failed:"
                                                                               + getMethod.getStatusLine());
                }
                // 读取内容
                byte[] responseBody = getMethod.getResponseBody();
                return new String(responseBody, charset);
            } finally {
                // 释放链接
                getMethod.releaseConnection();
            }
        } catch (HttpException e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            throw new OpenApiException(OpenApiException.NETWORK_ERROR, "Request [" + url + "] failed:" + e.getMessage());
        } catch (IOException e) {
            // 发生网络异常
            throw new OpenApiException(OpenApiException.NETWORK_ERROR, "Request [" + url + "] failed:" + e.getMessage());
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
    private String postRequest(String url, HashMap<String, String> params, HashMap<String, String> cookies,
                               String protocol) throws OpenApiException {
        // url=url+"?charset="+charset;

        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);

        // 设置请求参数
        if (params != null && !params.isEmpty()) {
            NameValuePair[] data = new NameValuePair[params.size()];
            Iterator<Entry<String, String>> iter = params.entrySet().iterator();
            int i = 0;
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                // if("charset".equalsIgnoreCase(entry.getKey())){
                // continue;
                // }
                data[i] = new NameValuePair(entry.getKey(), entry.getValue());
                ++i;
            }
            postMethod.setRequestBody(data);
        }

        // 设置cookie
        if (cookies != null && !cookies.isEmpty()) {
            Iterator<Entry<String, String>> iter = cookies.entrySet().iterator();
            StringBuilder buffer = new StringBuilder(128);
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                buffer.append((String) entry.getKey()).append("=").append((String) entry.getValue()).append("; ");
            }
            // 设置cookie策略
            postMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            // 设置cookie内容
            postMethod.setRequestHeader("Cookie", buffer.toString());
        }

        // 设置User-Agent
        postMethod.setRequestHeader("User-Agent", "Java OpenApiOauth SDK Client");
        // 设置建立连接超时时间
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
        // 设置读数据超时时间
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(3000);
        // 设置编码
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
        // 使用系统提供的默认的恢复策略
        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

        try {
            try {
                int statusCode = httpClient.executeMethod(postMethod);
                if (statusCode != HttpStatus.SC_OK) {
                    throw new OpenApiException(OpenApiException.NETWORK_ERROR, "Request [" + url + "] failed:"
                                                                               + postMethod.getStatusLine());
                }
                // 读取内容
                byte[] responseBody = postMethod.getResponseBody();
                return new String(responseBody, charset);
            } finally {
                // 释放链接
                postMethod.releaseConnection();
            }
        } catch (HttpException e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            throw new OpenApiException(OpenApiException.NETWORK_ERROR, "Request [" + url + "] failed:" + e.getMessage());
        } catch (IOException e) {
            // 发生网络异常
            throw new OpenApiException(OpenApiException.NETWORK_ERROR, "Request [" + url + "] failed:" + e.getMessage());
        }
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

}
