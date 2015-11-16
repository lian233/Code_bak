package com.wofu.base.inventory;

import com.wofu.base.util.BusinessObject;

public class ECS_InventoryBC extends BusinessObject {
	
	private int skuid;
	private int orgid;
	private int qty;
	private int badqty;
	private int lockqty;
	private int lockbadqty;
	private double avgcostvalue;
	private int lastdayqty;
	private int lastmpnthqty;
	private int placeid;
	public double getAvgcostvalue() {
		return avgcostvalue;
	}
	public void setAvgcostvalue(double avgcostvalue) {
		this.avgcostvalue = avgcostvalue;
	}
	public int getBadqty() {
		return badqty;
	}
	public void setBadqty(int badqty) {
		this.badqty = badqty;
	}

	public int getSkuid() {
		return skuid;
	}
	public void setSkuid(int skuid) {
		this.skuid = skuid;
	}
	public int getLastdayqty() {
		return lastdayqty;
	}
	public void setLastdayqty(int lastdayqty) {
		this.lastdayqty = lastdayqty;
	}
	public int getLastmpnthqty() {
		return lastmpnthqty;
	}
	public void setLastmpnthqty(int lastmpnthqty) {
		this.lastmpnthqty = lastmpnthqty;
	}
	public int getLockbadqty() {
		return lockbadqty;
	}
	public void setLockbadqty(int lockbadqty) {
		this.lockbadqty = lockbadqty;
	}
	public int getLockqty() {
		return lockqty;
	}
	public void setLockqty(int lockqty) {
		this.lockqty = lockqty;
	}
	public int getOrgid() {
		return orgid;
	}
	public void setOrgid(int orgid) {
		this.orgid = orgid;
	}
	public int getPlaceid() {
		return placeid;
	}
	public void setPlaceid(int placeid) {
		this.placeid = placeid;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	
}
