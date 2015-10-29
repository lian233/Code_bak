package com.wofu.intf.dtc;

import java.sql.Connection;

public abstract class DtcProcess {
	private String msgId;
	private String ReceiverId;
	private String servicetype;
	private String bizdata;
	private Connection conn;
	private int extConnId;
	public abstract void process() throws Exception;
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getReceiverId() {
		return ReceiverId;
	}
	public void setReceiverId(String receiverId) {
		ReceiverId = receiverId;
	}
	public String getServicetype() {
		return servicetype;
	}
	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}
	public String getBizdata() {
		return bizdata;
	}
	public void setBizdata(String bizdata) {
		this.bizdata = bizdata;
	}
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public int getExtConnId() {
		return extConnId;
	}
	public void setExtConnId(int extConnId) {
		this.extConnId = extConnId;
	}


	
	
	
}
