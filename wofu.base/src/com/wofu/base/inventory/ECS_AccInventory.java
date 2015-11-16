package com.wofu.base.inventory;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class ECS_AccInventory extends BusinessObject {
	private int busid;
	private int bustype;
	private int orgid;
	private int directflag;
	private int skuid;
	private int itemid;
	private int qty;
	private double price;
	private double cost;
	private String notes;
	private int placeid;
	private Date busidate;

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
	public int getOrgid() {
		return orgid;	
	}
	public void setOrgid(int orgid) {
		this.orgid = orgid;
	}
	public int getDirectflag() {
		return directflag;	
	}
	public void setDirectflag(int directflag) {
		this.directflag = directflag;
	}
	public int getSkuid() {
		return skuid;	
	}
	public void setSkuid(int skuid) {
		this.skuid = skuid;
	}
	public int getItemid() {
		return itemid;	
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public int getQty() {
		return qty;	
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public double getPrice() {
		return price;	
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getCost() {
		return cost;	
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public String getNotes() {
		return notes;	
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getPlaceid() {
		return placeid;
	}
	public void setPlaceid(int placeid) {
		this.placeid = placeid;
	}
	public Date getBusidate() {
		return busidate;	
	}
	public void setBusidate(Date busidate) {
		this.busidate = busidate;
	}
}
