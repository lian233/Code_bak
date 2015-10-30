package com.wofu.ecommerce.vjia;

import java.util.Hashtable;
import java.util.Map;

public class SoapHeader {
	
	private String uri;
	private String headername="MySoapHeader";
	private String uname;
	private String password;
	private Map<String,String> header=null;
	 
	public SoapHeader()
	{
		header=new Hashtable<String,String>();
	}
	
	public SoapHeader(String uname,String password)
	{
		header=new Hashtable<String,String>();
		this.setUname(uname);
		this.setPassword(password);
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
		header.put("Password",password);
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
		header.put("Uname",uname);
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getHeadername() {
		return headername;
	}
	public void setHeadername(String headername) {
		this.headername = headername;
	}
	public Map<String,String> getHeader() {
		return header;
	}

}
