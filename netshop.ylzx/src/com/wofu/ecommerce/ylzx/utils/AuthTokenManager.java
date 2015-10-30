package com.wofu.ecommerce.ylzx.utils;
import java.util.Date;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.common.tools.util.DOMHelper;
/**
 * 认证类
 * 一：取得requestToken
 * 二：认证requestToken
 * 三：取得accessToken
 * @author Administrator
 *
 */
public class AuthTokenManager {
	private String oauth_consumer_key;
	private String oauth_consumer_secert;
	private String oauth_signature_method="HMAC-SHA1";
	private String oauth_timestamp;
	private String oauth_nonce;
	private String oauth_version="1.0";
	private String username="";
	private String password="";
	private String token;
	private String oauth_token_secret;
	public AuthTokenManager(String oauth_consumer_key,String oauth_consumer_secert,String oauth_version
			,String username,String password){
		this.oauth_consumer_key= oauth_consumer_key;
		this.oauth_consumer_secert= oauth_consumer_secert;
		this.oauth_version = oauth_version;
		this.username = username;
		this.password = password;
	}
	
	public void init() throws Exception{
		getRequestToken();
		if(null!=token || null !=oauth_token_secret){
			authorizeToken();
			if(null!=token || null !=oauth_token_secret){
				getRealToken();
			}else{
				throw new Exception("认证请求token出错");
			}
		}else{
			throw new Exception("取请求token出错");
		}
		
	}
	
	//第一步：获取请求requestToken
	private void getRequestToken() throws Exception{
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("oauth_consumer_key", oauth_consumer_key);
		map.put("oauth_consumer_secert", oauth_consumer_secert);
		map.put("oauth_signature_method", oauth_signature_method);
		map.put("oauth_timestamp", String.valueOf(new Date().getTime()/1000L));
		map.put("oauth_nonce", String.valueOf(System.currentTimeMillis()));
		map.put("oauth_version", oauth_version);
		String result = Utils.sendByGet(Content.getRequestToken_url,
				map,"GET",oauth_consumer_secert,null);
		//Log.info("resutl: "+result);
		setToken(result);
		
	}
	
	
	private void setToken(String result) throws Exception{
		Document doc = DOMHelper.newDocument(result);
		Element ele = doc.getDocumentElement();
		String status = DOMHelper.getSubElementVauleByName(ele, "status").trim();
		if("200".equals(status)){
			Element eleTemp = DOMHelper.getSubElementsByName(ele, "body")[0];
			token =  DOMHelper.getSubElementVauleByName(eleTemp, "oauth_token");
			oauth_token_secret = DOMHelper.getSubElementVauleByName(eleTemp, "oauth_token_secret");
		}
	}
	
	//第二步：获取请求requestToken
	private void authorizeToken() throws Exception{
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("oauth_consumer_key", oauth_consumer_key);
		map.put("oauth_token", token);
		map.put("username", username);
		map.put("password", password);
		map.put("status", "1");
		map.put("oauth_signature_method", oauth_signature_method);
		map.put("oauth_timestamp", String.valueOf(new Date().getTime()/1000L));
		map.put("oauth_nonce", String.valueOf(System.currentTimeMillis()));
		map.put("oauth_version", oauth_version);
		String result = Utils.sendByGet(Content.authorizeToken_url,
				map,"get",oauth_consumer_secert,oauth_token_secret);
		//Log.info("resutl: "+result);
		setToken(result);
	}
	//第三步：获取请求accessToken
	private void getRealToken() throws Exception{
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("oauth_consumer_key", oauth_consumer_key);
		map.put("oauth_token", token);
		map.put("oauth_signature_method", oauth_signature_method);
		map.put("oauth_timestamp", String.valueOf(new Date().getTime()/1000L));
		map.put("oauth_nonce", String.valueOf(System.currentTimeMillis()));
		map.put("oauth_version", oauth_version);
		String result = Utils.sendByPost(Content.getRealToken_url,
				map,"POST",oauth_consumer_secert,oauth_token_secret);
		//Log.info("resutl: "+result);
		setToken(result);
	}

	public String getOauth_consumer_key() {
		return oauth_consumer_key;
	}

	public void setOauth_consumer_key(String oauth_consumer_key) {
		this.oauth_consumer_key = oauth_consumer_key;
	}

	public String getOauth_consumer_secert() {
		return oauth_consumer_secert;
	}

	public void setOauth_consumer_secert(String oauth_consumer_secert) {
		this.oauth_consumer_secert = oauth_consumer_secert;
	}

	public String getOauth_signature_method() {
		return oauth_signature_method;
	}

	public void setOauth_signature_method(String oauth_signature_method) {
		this.oauth_signature_method = oauth_signature_method;
	}

	public String getOauth_timestamp() {
		return oauth_timestamp;
	}

	public void setOauth_timestamp(String oauth_timestamp) {
		this.oauth_timestamp = oauth_timestamp;
	}

	public String getOauth_nonce() {
		return oauth_nonce;
	}

	public void setOauth_nonce(String oauth_nonce) {
		this.oauth_nonce = oauth_nonce;
	}

	public String getOauth_version() {
		return oauth_version;
	}

	public void setOauth_version(String oauth_version) {
		this.oauth_version = oauth_version;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOauth_token_secret() {
		return oauth_token_secret;
	}

	public void setOauth_token_secret(String oauth_token_secret) {
		this.oauth_token_secret = oauth_token_secret;
	}

	public String getToken() {
		return token;
	}
	
}
