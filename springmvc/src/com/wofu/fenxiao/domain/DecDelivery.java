package com.wofu.fenxiao.domain;
public class DecDelivery {
	private int id;
	private String name;
	private String code;
	private String localCode;
	private int orderNo;
	private int deliveryNoType;
	private int status;//状态
	private String note;//备注
	private String clientID;//圆通参数
	private String partnerkey;//圆通参数
	private String UserId;//圆通参数
	private String appkey;//圆通参数
	private String v;//api版本
	private String url;//请求url
	private String SecretKey;//备注
	private String queryurl;//圆通目的地查询参数
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name.trim();
	}
	public void setName(String name) {
		this.name = name.trim();
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLocalCode() {
		return localCode;
	}
	public void setLocalCode(String localCode) {
		this.localCode = localCode;
	}
	public int getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}
	public int getDeliveryNoType() {
		return deliveryNoType;
	}
	public void setDeliveryNoType(int deliveryNoType) {
		this.deliveryNoType = deliveryNoType;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getClientID() {
		return clientID;
	}
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	public String getPartnerkey() {
		return partnerkey;
	}
	public void setPartnerkey(String partnerkey) {
		this.partnerkey = partnerkey;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getAppkey() {
		return appkey;
	}
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
	public String getV() {
		return v;
	}
	public void setV(String v) {
		this.v = v;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSecretKey() {
		return SecretKey;
	}
	public void setSecretKey(String secretKey) {
		SecretKey = secretKey;
	}
	public String getQueryurl() {
		return queryurl;
	}
	public void setQueryurl(String queryurl) {
		this.queryurl = queryurl;
	}
	

	
}
