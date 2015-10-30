package com.wofu.intf.sf;

import java.sql.Connection;

public abstract class BizProcessor {
	
	private String bizData;
	private Connection connection;
	private Connection extconnection;
	private String interfaceSystem;
	private String customerCode;
	private String vertifycode;
	
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

	public String getInterfaceSystem() {
		return interfaceSystem;
	}

	public void setInterfaceSystem(String interfaceSystem) {
		this.interfaceSystem = interfaceSystem;
	}

	public Connection getExtconnection() {
		return extconnection;
	}

	public void setExtconnection(Connection extconnection) {
		this.extconnection = extconnection;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getVertifycode() {
		return vertifycode;
	}

	public void setVertifycode(String vertifycode) {
		this.vertifycode = vertifycode;
	}
	
	


	


}
