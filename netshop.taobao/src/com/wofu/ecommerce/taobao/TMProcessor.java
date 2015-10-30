package com.wofu.ecommerce.taobao;

import java.io.InputStream;

import com.taobao.api.TaobaoResponse;

public abstract class TMProcessor {
	
	private String url;
	private String appkey;
	private String appsecret;
	private String token;
	private long refund_id;
	private String refund_phase;
	private long refund_version;
	private String message;
	private long seller_logistics_address_id;
	private InputStream refuse_proof;
	private String refuse_message;
	private String operator;
	
	public abstract boolean process() throws Exception;

	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getAppsecret() {
		return appsecret;
	}



	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}



	public String getToken() {
		return token;
	}



	public void setToken(String token) {
		this.token = token;
	}



	public long getRefund_id() {
		return refund_id;
	}

	public void setRefund_id(long refund_id) {
		this.refund_id = refund_id;
	}

	public String getRefund_phase() {
		return refund_phase;
	}

	public void setRefund_phase(String refund_phase) {
		this.refund_phase = refund_phase;
	}

	public long getRefund_version() {
		return refund_version;
	}

	public void setRefund_version(long refund_version) {
		this.refund_version = refund_version;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getSeller_logistics_address_id() {
		return seller_logistics_address_id;
	}

	public void setSeller_logistics_address_id(long seller_logistics_address_id) {
		this.seller_logistics_address_id = seller_logistics_address_id;
	}

	public String getRefuse_message() {
		return refuse_message;
	}

	public void setRefuse_message(String refuse_message) {
		this.refuse_message = refuse_message;
	}

	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}

	public InputStream getRefuse_proof() {
		return refuse_proof;
	}

	public void setRefuse_proof(InputStream refuse_proof) {
		this.refuse_proof = refuse_proof;
	}
	
}
