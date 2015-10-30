package com.wofu.netshop.alibaba.fenxiao;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;

/**
 * 避免HttpClient的”SSLPeerUnverifiedException: peer not authenticated”异常
 * 不用导入SSL证书
 * 
 *
 */
public class WebClientDevWrapper {

    public static org.apache.http.client.HttpClient wrapClient(org.apache.http.client.HttpClient base) {
        try {
        	SSLContext ctx = SSLContext.getInstance("TLS");
        	X509TrustManager tm = new X509TrustManager() {
        	public void checkClientTrusted(X509Certificate[] xcs,
        	String string) {
        	}
        	public void checkServerTrusted(X509Certificate[] xcs,
        	String string) {
        	}
        	public X509Certificate[] getAcceptedIssuers() {
        	return null;
        	}
        	};
        	ctx.init(null, new TrustManager[] { tm }, null);
        	SSLSocketFactory ssf = new SSLSocketFactory(ctx);
        	ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        	ClientConnectionManager ccm = base.getConnectionManager();
        	SchemeRegistry sr = ccm.getSchemeRegistry();
        	sr.register(new Scheme("https", ssf, 443));
        	return new DefaultHttpClient(ccm, base.getParams());
        	} catch (Exception ex) {
        	ex.printStackTrace();
        	return null;
        	}
    }
}