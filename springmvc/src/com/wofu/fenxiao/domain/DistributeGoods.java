package com.wofu.fenxiao.domain;

import java.sql.Date;

public class DistributeGoods {
	private Integer ProductLineID;
	private Integer GoodsID;
	private String Title;
	private String ImaUrl;
	private String GoodsUrl;
	private double BasePrice;
	private double Price;	
	private Integer Status;
	private String Note;
	
	public Integer getProductLineID() {
		return ProductLineID;
	}
	public void setProductLineID(Integer productLineID) {
		ProductLineID = productLineID;
	}
	public Integer getGoodsID() {
		return GoodsID;
	}
	public void setGoodsID(Integer goodsID) {
		GoodsID = goodsID;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getImaUrl() {
		return ImaUrl;
	}
	public void setImaUrl(String imaUrl) {
		ImaUrl = imaUrl;
	}
	public String getGoodsUrl() {
		return GoodsUrl;
	}
	public void setGoodsUrl(String goodsUrl) {
		GoodsUrl = goodsUrl;
	}
	public Integer getStatus() {
		return Status;
	}
	public void setStatus(Integer status) {
		Status = status;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}

	public double getBasePrice() {
		return BasePrice;
	}
	public void setBasePrice(double basePrice) {
		BasePrice = basePrice;
	}
	public double getPrice() {
		return Price;
	}
	public void setPrice(double price) {
		Price = price;
	}
	
	
}
