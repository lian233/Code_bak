package com.wofu.fire.deliveryservice;

import java.sql.Connection;

public abstract class CProcessor {
	private String bizData;
	private Connection connection;
	private Connection extconnection;
	private String username;  //µÍ√˚
	private String tradecontactid;
	private int userId;//ecshop÷–µƒ’ ∫≈id
	public abstract void process() throws Exception;

	public String getBizData() {
		return bizData;
	}

	public void setBizData(String bizData) {
		this.bizData = bizData;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}



	public Connection getExtconnection() {
		return extconnection;
	}

	public void setExtconnection(Connection extconnection) {
		this.extconnection = extconnection;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTradecontactid() {
		return tradecontactid;
	}

	public void setTradecontactid(String tradecontactid) {
		this.tradecontactid = tradecontactid;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
