package com.wofu.ecommerce.alibaba;

import com.wofu.base.util.BusinessObject;

public class Children extends BusinessObject {
	private long fid;
	private String specId;
	private String cargoNumber;
	private String value;
	private String price;
	private String retailPrice;
	private int canBookCount;
	private int saleCount;
	
	public Children(){
		this.fid=1;
		this.specId="";
		this.cargoNumber="";
		this.value="";
		this.price="";
		this.retailPrice="";
		this.canBookCount=1;
		this.saleCount=1;
	}
	
	public long getFid() {
		return fid;
	}
	public void setFid(long fid) {
		this.fid = fid;
	}
	public String getSpecId() {
		return specId;
	}
	public void setSpecId(String specId) {
		this.specId = specId;
	}
	public String getCargoNumber() {
		return cargoNumber;
	}
	public void setCargoNumber(String cargoNumber) {
		this.cargoNumber = cargoNumber;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(String retailPrice) {
		this.retailPrice = retailPrice;
	}
	public int getCanBookCount() {
		return canBookCount;
	}
	public void setCanBookCount(int canBookCount) {
		this.canBookCount = canBookCount;
	}
	public int getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(int saleCount) {
		this.saleCount = saleCount;
	}
	
}
