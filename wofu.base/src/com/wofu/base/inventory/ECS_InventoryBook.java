package com.wofu.base.inventory;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class ECS_InventoryBook extends BusinessObject {
	private int serialid;
	private int itemid;
	private int orgid;
	private int directflag;
	private int qty;
	private int closeqty;
	private double pricevalue;
	private double costvalue;
	private double closecostvalue;
	private int busid;
	private int bustype;
	private String notes;
	private Date busidate;
	private Date sdate;
	private Date stime;
	private int placeid;
	
	public void account() throws Exception
	{
		
	}

	public int getSerialid() {
		return serialid;	
	}
	public void setSerialid(int serialid) {
		this.serialid = serialid;
	}
	public int getItemid() {
		return itemid;	
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
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
	public int getQty() {
		return qty;	
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public int getCloseqty() {
		return closeqty;	
	}
	public void setCloseqty(int closeqty) {
		this.closeqty = closeqty;
	}
	public double getPricevalue() {
		return pricevalue;	
	}
	public void setPricevalue(double pricevalue) {
		this.pricevalue = pricevalue;
	}
	public double getCostvalue() {
		return costvalue;	
	}
	public void setCostvalue(double costvalue) {
		this.costvalue = costvalue;
	}
	public double getClosecostvalue() {
		return closecostvalue;	
	}
	public void setClosecostvalue(double closecostvalue) {
		this.closecostvalue = closecostvalue;
	}
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
	public String getNotes() {
		return notes;	
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Date getBusidate() {
		return busidate;	
	}
	public void setBusidate(Date busidate) {
		this.busidate = busidate;
	}
	public Date getSdate() {
		return sdate;	
	}
	public void setSdate(Date sdate) {
		this.sdate = sdate;
	}
	public Date getStime() {
		return stime;	
	}
	public void setStime(Date stime) {
		this.stime = stime;
	}
	public int getPlaceid() {
		return placeid;	
	}
	public void setPlaceid(int placeid) {
		this.placeid = placeid;
	}
}
