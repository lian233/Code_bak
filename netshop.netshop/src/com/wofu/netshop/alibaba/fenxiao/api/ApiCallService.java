package com.wofu.netshop.alibaba.fenxiao.api;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.alibaba.fenxiao.WebClientDevWrapper;
import com.wofu.netshop.alibaba.fenxiao.util.CommonUtil;
/**
 * api调用的服务类
 */
public class ApiCallService {

    /**
     * 调用api测试
     * @param urlHead 请求的url到openapi的部分，如http://gw.open.1688.com/openapi/
     * @param urlPath protocol/version/namespace/name/appKey
     * @param appSecretKey 测试的app密钥，如果为空表示不需要签名
     * @param params api请求参数map。如果api需要用户授权访问，那么必须完成授权流程，params中必须包含access_token参数
     * @return json格式的调用结果
     */
    public static String callApiTest(String urlHead, String urlPath, 
    		String appSecretKey, Map<String, String> params) throws Exception{
    	  HttpClient httpClient = new DefaultHttpClient();
    	if(urlHead.indexOf("https")==0){//peer not authenticated
    		httpClient = WebClientDevWrapper.wrapClient(httpClient);
    	}
        HttpPost method = new HttpPost(urlHead + urlPath);
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        if(params != null){
            for (Map.Entry<String, String> entry : params.entrySet()) {
            	param.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        if(appSecretKey != null){
        	param.add(new BasicNameValuePair("_aop_signature", CommonUtil.signatureWithParamsAndUrlPath(urlPath, params, appSecretKey)));
        }
        String response = "";
        InputStream is=null;
        BufferedReader br=null;
        StringBuilder sb=null;
        try{
        	UrlEncodedFormEntity urlEntity  = new UrlEncodedFormEntity(param,"utf-8");
        	method.setEntity(urlEntity);
            HttpResponse resp = httpClient.execute(method);
            HttpEntity entity = resp.getEntity();
            is = entity.getContent();
            sb = new StringBuilder();
            String readLine = null;
            br = new BufferedReader(new InputStreamReader(is,"utf-8"));
            while(null!=(readLine= br.readLine())) sb.append(readLine);
            httpClient.getConnectionManager().shutdown();
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
            if(is!=null){
            	is.close();
            }
            if(br!=null){
            	br.close();
            }
        }
        return sb.toString();
    }
    
}
