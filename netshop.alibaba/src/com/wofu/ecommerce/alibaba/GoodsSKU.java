package com.wofu.ecommerce.alibaba;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class GoodsSKU extends BusinessObject{
	private String price;
	private int saleCount;
	private String specId;
	private int canBookCount;
	private String cargoNumber;  //sku
	private String retailPrice;
	private String value;
	private int fid;
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public int getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(int saleCount) {
		this.saleCount = saleCount;
	}
	public String getSpecId() {
		return specId;
	}
	public void setSpecId(String specId) {
		this.specId = specId;
	}
	public int getCanBookCount() {
		return canBookCount;
	}
	public void setCanBookCount(int canBookCount) {
		this.canBookCount = canBookCount;
	}
	public String getCargoNumber() {
		return cargoNumber;
	}
	public void setCargoNumber(String cargoNumber) {
		this.cargoNumber = cargoNumber;
	}
	public String getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(String retailPrice) {
		this.retailPrice = retailPrice;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
	
}
