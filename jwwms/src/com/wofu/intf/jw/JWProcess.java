package com.wofu.intf.jw;

import java.sql.Connection;

public abstract class JWProcess{
	private String msgid;
	private String appkey;
	private String bizData;
	private String content;
	private Connection conn;
	public abstract void process()throws Exception;
	public String getMsgid() {
		return msgid;
	}
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	public String getAppkey() {
		return appkey;
	}
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
	public String getBizData() {
		return bizData;
	}
	public void setBizData(String bizData) {
		this.bizData = bizData;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
}
