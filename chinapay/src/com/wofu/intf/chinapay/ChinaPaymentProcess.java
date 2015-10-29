package com.wofu.intf.chinapay;

import java.sql.Connection;

import chinapay.PrivateKey;

public abstract class ChinaPaymentProcess {
	private String msgId;
	private String transtype;
	private String merid;
	private String orderno;
	private String status;
	private String amount;
	private String transdate;
	private String currencycode;
	private String GateId;
	private String checkvalue;
	private String Priv1;
	private Connection conn;
	private Connection extConn;
	private PrivateKey privateKey;
	private String notes;
	//业务处理方法
	public abstract void process() throws Exception;
	//验证数字签名
	protected abstract boolean verify() throws Exception;
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getTranstype() {
		return transtype;
	}
	public void setTranstype(String transtype) {
		this.transtype = transtype;
	}
	public String getMerid() {
		return merid;
	}
	public void setMerid(String merid) {
		this.merid = merid;
	}
	public String getOrderno() {
		return orderno;
	}
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTransdate() {
		return transdate;
	}
	public void setTransdate(String transdate) {
		this.transdate = transdate;
	}
	public String getCurrencycode() {
		return currencycode;
	}
	public void setCurrencycode(String currencycode) {
		this.currencycode = currencycode;
	}
	public String getGateId() {
		return GateId;
	}
	public void setGateId(String gateId) {
		GateId = gateId;
	}
	public String getCheckvalue() {
		return checkvalue;
	}
	public void setCheckvalue(String checkvalue) {
		this.checkvalue = checkvalue;
	}
	public String getPriv1() {
		return Priv1;
	}
	public void setPriv1(String priv1) {
		Priv1 = priv1;
	}
	public PrivateKey getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}
	public Connection getExtConn() {
		return extConn;
	}
	public void setExtConn(Connection extConn) {
		this.extConn = extConn;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
	
	
	
	
}
