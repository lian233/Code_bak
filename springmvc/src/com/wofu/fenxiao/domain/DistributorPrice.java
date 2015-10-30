package com.wofu.fenxiao.domain;

public class DistributorPrice {
	private Integer ID;
	private Integer CustomerID;
	private Integer ParentID;
	private Integer GoodsLevel;
	private Integer SetType;
	private String GoodsKey;
	private double Value;
	
	
	
	public Integer getID() {
		return ID;
	}
	public void setID(Integer id) {
		ID = id;
	}
	public Integer getCustomerID() {
		return CustomerID;
	}
	public void setCustomerID(Integer customerID) {
		CustomerID = customerID;
	}
	public Integer getParentID() {
		return ParentID;
	}
	public void setParentID(Integer parentID) {
		ParentID = parentID;
	}
	public int getGoodsLevel() {
		return GoodsLevel;
	}
	public void setGoodsLevel(Integer goodsLevel) {
		GoodsLevel = goodsLevel;
	}
	public Integer getSetType() {
		return SetType;
	}
	public void setSetType(Integer setType) {
		SetType = setType;
	}
	public String getGoodsKey() {
		return GoodsKey;
	}
	public void setGoodsKey(String goodsKey) {
		GoodsKey = goodsKey;
	}
	public double getValue() {
		return Value;
	}
	public void setValue(double value) {
		Value = value;
	}


	
}
