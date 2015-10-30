package com.wofu.retail.baseinformation;

import com.wofu.base.util.BusinessObject;

public class ECS_ItemOrg extends BusinessObject {
	private int itemid;
	private int orgid;
	private double price;

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
	public double getPrice() {
		return price;	
	}
	public void setPrice(double price) {
		this.price = price;
	}


}
