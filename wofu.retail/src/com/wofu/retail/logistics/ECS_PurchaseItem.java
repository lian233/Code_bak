package com.wofu.retail.logistics;

import com.wofu.base.util.BusinessObject;

public class ECS_PurchaseItem extends BusinessObject {
	private int pid;
	private int itemid;
	private String barcodeid;
	private double cost;
	private double qty;

	public int getPid() {
		return pid;	
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getItemid() {
		return itemid;	
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public String getBarcodeid() {
		return barcodeid;	
	}
	public void setBarcodeid(String barcodeid) {
		this.barcodeid = barcodeid;
	}
	public double getCost() {
		return cost;	
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public double getQty() {
		return qty;	
	}
	public void setQty(double qty) {
		this.qty = qty;
	}

}
