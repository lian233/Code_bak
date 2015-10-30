package com.wofu.retail.baseinformation;


import com.wofu.base.util.BusinessObject;

public class ECS_ItemSupplier extends BusinessObject {
	
	private int itemid;
	private int sid;
	private int cost;
	private int flag;
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getItemid() {
		return itemid;
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	
}
