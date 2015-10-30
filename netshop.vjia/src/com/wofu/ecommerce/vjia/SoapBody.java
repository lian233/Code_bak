package com.wofu.ecommerce.vjia;

import java.util.Map;

public class SoapBody {
	
	private String uri;
	private String requestname;
	private Map bodyparams=null;
	
	public SoapBody()
	{
		
	}
	public SoapBody(String uri,String requestname,Map bodyparams)
	{
		this.setBodyParams(bodyparams);
		this.setRequestname(requestname);
		this.setUri(uri);
	}
	
	public void setUri(String uri)
	{
		this.uri=uri;
	}
	
	public String getUri()
	{
		return this.uri;
	}
	
	public void setBodyParams(Map bodyparams)
	{
		this.bodyparams=bodyparams;
	}
	
	public Map getBodyParams()
	{
		return this.bodyparams;
	}

	public String getRequestname() {
		return requestname;
	}

	public void setRequestname(String requestname) {
		this.requestname = requestname;
	}

}
