package com.wofu.base.inventory;

import com.wofu.base.dbmanager.DataCentre;

public abstract class InventoryProcessor {
	private DataCentre dao;
	private int busid;
	private int bustype;
	
	public abstract void execute() throws Exception;
	
	public int getBusid() {
		return busid;
	}
	public void setBusid(int busid) {
		this.busid = busid;
	}
	public int getBustype() {
		return bustype;
	}
	public void setBustype(int bustype) {
		this.bustype = bustype;
	}
	public DataCentre getDao() {
		return dao;
	}
	public void setDao(DataCentre dao) {
		this.dao = dao;
	}
}
