package com.wofu.fenxiao.domain;

import java.util.Date;

public class DecShop {
	private int Id;
	private String Name;
	private int CustomerID;
	private int ChannelID;
	private String Code;
	private String NetAddr;
	private String Tele;
	private String LinkMan;
	private int CanMerge;
	private int CanSeparate;
	private int SynFlag;
	private String Nick;
	private String AppKey;
	private String Session;
	private String Token;
	private int IsGetOrder;
	private int GetOrderSpan;
	private Date LastOrderTime;
	private Date LastRefundTime;
	private Date LastItemTime;
	private Date lastTokenTime;
	private int isNeedDelivery;
	private int isgenCustomerRet;
	private int Status;
	private int isUpdateStock;	
	private String Note;
	private int user_id;
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getName() {
		return Name!=null?Name.trim():null;
	}
	public void setName(String name) {
		Name = name.trim();
	}
	public int getCustomerID() {
		return CustomerID;
	}
	public void setCustomerID(int customerID) {
		CustomerID = customerID;
	}
	public int getChannelID() {
		return ChannelID;
	}
	public void setChannelID(int channelID) {
		ChannelID = channelID;
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public String getNetAddr() {
		return NetAddr;
	}
	public void setNetAddr(String netAddr) {
		NetAddr = netAddr;
	}
	public int getCanMerge() {
		return CanMerge;
	}
	public void setCanMerge(int canMerge) {
		CanMerge = canMerge;
	}
	public int getCanSeparate() {
		return CanSeparate;
	}
	public void setCanSeparate(int canSeparate) {
		CanSeparate = canSeparate;
	}
	public int getSynFlag() {
		return SynFlag;
	}
	public void setSynFlag(int synFlag) {
		SynFlag = synFlag;
	}
	public String getNick() {
		return Nick;
	}
	public void setNick(String nick) {
		Nick = nick;
	}
	public String getAppKey() {
		return AppKey;
	}
	public void setAppKey(String appKey) {
		AppKey = appKey;
	}
	public String getSession() {
		return Session;
	}
	public void setSession(String session) {
		Session = session;
	}
	public String getToken() {
		return Token;
	}
	public void setToken(String token) {
		Token = token;
	}
	public int getIsGetOrder() {
		return IsGetOrder;
	}
	public void setIsGetOrder(int isGetOrder) {
		IsGetOrder = isGetOrder;
	}
	public int getGetOrderSpan() {
		return GetOrderSpan;
	}
	public void setGetOrderSpan(int getOrderSpan) {
		GetOrderSpan = getOrderSpan;
	}
	public Date getLastOrderTime() {
		return LastOrderTime;
	}
	public void setLastOrderTime(Date lastOrderTime) {
		LastOrderTime = lastOrderTime;
	}

	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}

	public Date getLastRefundTime() {
		return LastRefundTime;
	}
	public void setLastRefundTime(Date lastRefundTime) {
		LastRefundTime = lastRefundTime;
	}
	public Date getLastItemTime() {
		return LastItemTime;
	}
	public void setLastItemTime(Date lastItemTime) {
		LastItemTime = lastItemTime;
	}
	public Date getLastTokenTime() {
		return lastTokenTime;
	}
	public void setLastTokenTime(Date lastTokenTime) {
		this.lastTokenTime = lastTokenTime;
	}
	public String getTele() {
		return Tele;
	}
	public void setTele(String tele) {
		Tele = tele;
	}
	public String getLinkMan() {
		return LinkMan;
	}
	public void setLinkMan(String linkMan) {
		LinkMan = linkMan;
	}

	public int getIsNeedDelivery() {
		return isNeedDelivery;
	}
	public void setIsNeedDelivery(int isNeedDelivery) {
		this.isNeedDelivery = isNeedDelivery;
	}

	public int getIsgenCustomerRet() {
		return isgenCustomerRet;
	}
	public void setIsgenCustomerRet(int isgenCustomerRet) {
		this.isgenCustomerRet = isgenCustomerRet;
	}

	public int getIsUpdateStock() {
		return isUpdateStock;
	}
	public void setIsUpdateStock(int isUpdateStock) {
		this.isUpdateStock = isUpdateStock;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	
}
