package com.wofu.intf.fedex;

import java.sql.Connection;

public abstract class BizProcessor {
	
	private String bizData;
	private Connection connection;
	private Connection extconnection;
	private String interfaceSystem;
	private Boolean isBarcodeId;
	private String vertifycode;
	private boolean warehouseMulti;

	
	public abstract void process() throws Exception;

	public String getBizData() {
		return bizData;
	}

	public void setBizData(String bizData) {
		this.bizData = bizData;
	}

	public String getVertifycode() {
		return vertifycode;
	}

	public void setVertifycode(String vertifycode) {
		this.vertifycode = vertifycode;
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

	public Boolean getIsBarcodeId() {
		return isBarcodeId;
	}

	public void setIsBarcodeId(Boolean isBarcodeId) {
		this.isBarcodeId = isBarcodeId;
	}

	public boolean getWarehouseMulti() {
		return warehouseMulti;
	}

	public void setWarehouseMulti(boolean warehouseMulti) {
		this.warehouseMulti = warehouseMulti;
	}
	
	


}
