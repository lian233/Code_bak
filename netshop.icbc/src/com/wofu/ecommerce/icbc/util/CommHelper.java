package com.wofu.ecommerce.icbc.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.CertStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.sun.net.ssl.SSLContext;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.icbc.Params;
/**
 *发送http请求帮助类
 */
public class CommHelper {

    /**
     * API请求
     */
	public static String doRequest(Map<String,Object> map,String url,String method){
		return doPost(map,url);
	}
    
    //https证书认证
    public static String doPost(Map<String, Object> map,String url) {
    	StringBuffer strBuff= new StringBuffer();
        try {
          HttpClient client = new DefaultHttpClient();
          KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
          FileInputStream is = new FileInputStream(new File(System.getProperty("user.dir")+"\\"+Params.trustStore));
          trustStore.load(is,Params.OUT_PASSWORD.toCharArray());
          is.close();
          SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
          socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
          Scheme sch = new Scheme("https",Params.staticOUT_HOST_PORT,socketFactory);
          client.getConnectionManager().getSchemeRegistry().register(sch);
          HttpPost post = new HttpPost(url);
          List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
          for(Iterator it = map.keySet().iterator();it.hasNext();){
        	  String name= (String)it.next();
        	  String value = (String)map.get(name);
        	  params.add(new BasicNameValuePair(name,value));
          }
          HttpEntity entity = new UrlEncodedFormEntity(params,"utf-8");
          post.setEntity(entity);
          HttpResponse response = client.execute(post);
            //Log.info("状态: "+i);
            	BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));
            	for(String line =bufferedreader.readLine();line!=null;strBuff.append(line),line=bufferedreader.readLine());
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.info("发送http请求出错");
        	Log.error("post请求出错", e.getMessage());
        }
        return strBuff.toString();
    }
    //取得请求流水号
    public static String getReq_sid(){
    	String time = Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,10).replaceAll(":","");
    	return time+getNumber();
    }
    
    private static String getNumber(){
		char[] array = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','z','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
		Random rand = new Random();
		for (int i = 62; i > 1; i--) {
		    int index = rand.nextInt(i);
		    char tmp = array[index];
		    array[index] = array[i - 1];
		    array[i - 1] = tmp;
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 8; i++)
		    sb.append( array[i]);
		return sb.toString();
	}
    //取得sign
    public static String getSign(String sign_type,String app_key,String auth_code,String app_secret,String req_data) throws Exception{
    	Encrypt encrypt=EncryptFactory.getEncrypt(sign_type);
		
        return encrypt.sign(app_key, auth_code,req_data.toString(), app_secret);//sign
    }

}
