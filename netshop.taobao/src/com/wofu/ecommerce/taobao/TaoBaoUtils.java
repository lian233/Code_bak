package com.wofu.ecommerce.taobao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.sql.Connection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;

public class TaoBaoUtils {
	
	/*
	public static String getToken(Connection conn,String tradecontactid,String appkey,
			String appsecret) throws Exception
	{
		
		String accesstoken="";
		
		String sql="select count(*) from ecs_token "
			+"where tradecontactid="+tradecontactid+" and validtime<=getdate()";
		
		if (SQLHelper.intSelect(conn,sql)==0)
		{
			sql="select accesstoken from ecs_token "
				+"where tradecontactid="+tradecontactid;
			accesstoken=SQLHelper.strSelect(conn, sql);
		}
		else
			accesstoken=makeNewToken(conn,tradecontactid,appkey,
					appsecret);
		
		return accesstoken;
		
	}
	*/
	
	private static String makeNewToken(Connection conn,String tradecontactid,String appkey,
			String appsecret) throws Exception
	{

		TrustManager[] trustAllCerts = new TrustManager[] {
		   new X509TrustManager() {
		      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		        return null;
		      }

		      public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

		      public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

			public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) 
				throws CertificateException {
				
				
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
				// TODO Auto-generated method stub
				
			}

		   }
		};

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		
		HostnameVerifier allHostsValid = new HostnameVerifier() {
		    public boolean verify(String hostname, SSLSession session) {
		      return true;
		    }
		};
		//6201210d5428a06708ZZfdb547c3192dc22c421b0524878673637734
		
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		
		String sql="select refreshtoken from ecs_token where tradecontactid="+tradecontactid;
		
		String refreshtoken=SQLHelper.strSelect(conn,sql);
		
		String urlstr="https://oauth.taobao.com/token?client_id="+appkey+"&client_secret="+appsecret+"&grant_type=refresh_token&refresh_token="+refreshtoken;

		System.out.println(urlstr);
		
		URL url = new URL(urlstr);
		
		HttpsURLConnection httpsURLConnection=(HttpsURLConnection)url.openConnection();
		httpsURLConnection.setConnectTimeout(30000);
		httpsURLConnection.setReadTimeout(30000);
		httpsURLConnection.setDoOutput(true);
		httpsURLConnection.setDoInput(true); 
		httpsURLConnection.setUseCaches(false);   
		httpsURLConnection.setRequestMethod("POST");

		httpsURLConnection.connect();

		int responseCode=httpsURLConnection.getResponseCode();
		InputStream input=null;
		if(responseCode==200){
			input=httpsURLConnection.getInputStream();
		}else{
			input=httpsURLConnection.getErrorStream();
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(input));
		StringBuilder result=new StringBuilder();
		String line=null; 
		while((line=in.readLine())!=null){
			result.append(line);
		}
		
		System.out.println(result.toString());
		
		JSONObject json=new JSONObject(result.toString());
		String newrefreshtoken=json.getString("refresh_token");
		String accesstoken=json.getString("access_token");
		
		sql="update ecs_token set refreshtoken='"+newrefreshtoken+"',accesstoken='"+accesstoken
			+"',maketime=getdate(),validtime=dateadd(day,1,getdate()) where tradecontactid="+tradecontactid;
		
		SQLHelper.executeSQL(conn, sql);
		
		return accesstoken;
	}
}
