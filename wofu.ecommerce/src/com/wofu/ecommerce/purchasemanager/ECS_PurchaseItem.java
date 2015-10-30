package com.wofu.ecommerce.purchasemanager;

import com.wofu.base.util.BusinessObject; 

public class ECS_PurchaseItem extends  BusinessObject{

	private int pid;
	private int itemid;
	private String itemcustomid;
	private String customcode;
	private String itemname;
	private int skuid;
	private String skucustomid;
	private String custombc;
	private String colorname;
	private String sizename;
	private double baseprice;
	private double cost;
	private int purqty;
	private int qty;
	private int stockqty;
	
	public double getBaseprice() {
		return baseprice;
	}
	public void setBaseprice(double baseprice) {
		this.baseprice = baseprice;
	}
	public String getColorname() {
		return colorname;
	}
	public void setColorname(String colorname) {
		this.colorname = colorname;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public String getCustombc() {
		return custombc;
	}
	public void setCustombc(String custombc) {
		this.custombc = custombc;
	}
	public String getCustomcode() {
		return customcode;
	}
	public void setCustomcode(String customcode) {
		this.customcode = customcode;
	}
	public String getItemcustomid() {
		return itemcustomid;
	}
	public void setItemcustomid(String itemcustomid) {
		this.itemcustomid = itemcustomid;
	}
	public int getItemid() {
		return itemid;
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public String getItemname() {
		return itemname;
	}
	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getPurqty() {
		return purqty;
	}
	public void setPurqty(int purqty) {
		this.purqty = purqty;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public String getSizename() {
		return sizename;
	}
	public void setSizename(String sizename) {
		this.sizename = sizename;
	}
	public String getSkucustomid() {
		return skucustomid;
	}
	public void setSkucustomid(String skucustomid) {
		this.skucustomid = skucustomid;
	}
	public int getSkuid() {
		return skuid;
	}
	public void setSkuid(int skuid) {
		this.skuid = skuid;
	}
	public int getStockqty() {
		return stockqty;
	}
	public void setStockqty(int stockqty) {
		this.stockqty = stockqty;
	}
	
		
}
