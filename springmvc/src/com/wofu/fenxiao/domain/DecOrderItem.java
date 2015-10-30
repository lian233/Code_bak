package com.wofu.fenxiao.domain;

import java.util.Date;

public class DecOrderItem {
	private int id;
	private String sheetID;
	private String outerSkuID;
	private String title;
	private String skuPropertiesName;
	private double basePrice;
	private double customPrice;
	private double distributePrice;
	private int purQty;
	private String oID;
	private double salePrice;
	private String outerID;
	private int payPresentID;
	private String picPath;
	private String note;
	private String front ;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSheetID() {
		return sheetID;
	}
	public void setSheetID(String sheetID) {
		this.sheetID = sheetID;
	}
	public String getOuterSkuID() {
		return outerSkuID;
	}
	public void setOuterSkuID(String outerSkuID) {
		this.outerSkuID = outerSkuID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSkuPropertiesName() {
		return skuPropertiesName;
	}
	public void setSkuPropertiesName(String skuPropertiesName) {
		this.skuPropertiesName = skuPropertiesName;
	}
	public double getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(double basePrice) {
		this.basePrice = basePrice;
	}
	public double getCustomPrice() {
		return customPrice;
	}
	public void setCustomPrice(double customPrice) {
		this.customPrice = customPrice;
	}
	public double getDistributePrice() {
		return distributePrice;
	}
	public void setDistributePrice(double distributePrice) {
		this.distributePrice = distributePrice;
	}
	public int getPurQty() {
		return purQty;
	}
	public void setPurQty(int purQty) {
		this.purQty = purQty;
	}
	public String getOID() {
		return oID;
	}
	public void setOID(String oid) {
		oID = oid;
	}
	public double getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}
	public String getOuterID() {
		return outerID;
	}
	public void setOuterID(String outerID) {
		this.outerID = outerID;
	}
	public int getPayPresentID() {
		return payPresentID;
	}
	public void setPayPresentID(int payPresentID) {
		this.payPresentID = payPresentID;
	}
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	public void setFront(String front) {
		this.front = front;
	}
	public String getFront() {
		return front;
	}	
	
	
}
