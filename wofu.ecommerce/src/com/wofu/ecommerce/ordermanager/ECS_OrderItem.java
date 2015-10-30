package com.wofu.ecommerce.ordermanager;

import com.wofu.base.util.BusinessObject; 

public class ECS_OrderItem extends  BusinessObject{

	private int orderid;
	private String refsubordercode="";
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
	private double customprice;
	private double distprice;
	private int stockqty;
	private int orderqty;
	private int purqty;
	private int outqty;
	private String notes="";
	

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getOrderid() {
		return orderid;
	}

	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}


	public int getOrderqty() {
		return orderqty;
	}



	public String getRefsubordercode() {
		return refsubordercode;
	}

	public void setRefsubordercode(String refsubordercode) {
		this.refsubordercode = refsubordercode;
	}

	public int getStockqty() {
		return stockqty;
	}



	public int getPurqty() {
		return purqty;
	}



	public void setPurqty(int purqty) {
		this.purqty = purqty;
	}



	public void setOrderqty(int orderqty) {
		this.orderqty = orderqty;
	}



	public void setStockqty(int stockqty) {
		this.stockqty = stockqty;
	}



	public int getItemid() {
		return itemid;
	}

	public void setItemid(int itemid) {
		this.itemid = itemid;
	}

	public int getSkuid() {
		return skuid;
	}

	public void setSkuid(int skuid) {
		this.skuid = skuid;
	}



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



	public String getItemname() {
		return itemname;
	}



	public void setItemname(String itemname) {
		this.itemname = itemname;
	}



	public String getSizename() {
		return sizename;
	}



	public void setSizename(String sizename) {
		this.sizename = sizename;
	}

	public double getDistprice() {
		return distprice;
	}

	public void setDistprice(double distprice) {
		this.distprice = distprice;
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

	public String getSkucustomid() {
		return skucustomid;
	}

	public void setSkucustomid(String skucustomid) {
		this.skucustomid = skucustomid;
	}

	public int getOutqty() {
		return outqty;
	}

	public void setOutqty(int outqty) {
		this.outqty = outqty;
	}
		
}
