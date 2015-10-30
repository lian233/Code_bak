package com.wofu.retail.customer;

import com.wofu.base.util.BusinessObject;

public class ECS_DeliveryItem extends BusinessObject{
	

	private int deliveryid;
	private String refsubordercode;
	private int itemid;
	private String itemname;
	private int skuid;
	private String custombc;
	private String colorname;
	private String sizename;
	private double baseprice;
	private double customprice;
	private double distprice;
	private int orderqty;
	private int purqty;
	private int outqty;
	private int inqty;
	
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
	public String getCustombc() {
		return custombc;
	}
	public void setCustombc(String custombc) {
		this.custombc = custombc;
	}
	public double getCustomprice() {
		return customprice;
	}
	public void setCustomprice(double customprice) {
		this.customprice = customprice;
	}
	public int getDeliveryid() {
		return deliveryid;
	}
	public void setDeliveryid(int deliveryid) {
		this.deliveryid = deliveryid;
	}
	public double getDistprice() {
		return distprice;
	}
	public void setDistprice(double distprice) {
		this.distprice = distprice;
	}
	public int getInqty() {
		return inqty;
	}
	public void setInqty(int inqty) {
		this.inqty = inqty;
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
	public int getOrderqty() {
		return orderqty;
	}
	public void setOrderqty(int orderqty) {
		this.orderqty = orderqty;
	}
	public int getOutqty() {
		return outqty;
	}
	public void setOutqty(int outqty) {
		this.outqty = outqty;
	}
	public int getPurqty() {
		return purqty;
	}
	public void setPurqty(int purqty) {
		this.purqty = purqty;
	}
	public String getRefsubordercode() {
		return refsubordercode;
	}
	public void setRefsubordercode(String refsubordercode) {
		this.refsubordercode = refsubordercode;
	}
	public String getSizename() {
		return sizename;
	}
	public void setSizename(String sizename) {
		this.sizename = sizename;
	}
	public int getSkuid() {
		return skuid;
	}
	public void setSkuid(int skuid) {
		this.skuid = skuid;
	}




}
