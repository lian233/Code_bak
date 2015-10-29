package com.wofu.ecommerce.alibaba;

import java.util.Map;

import com.wofu.base.util.BusinessObject;

public class OfferSKUItem extends BusinessObject {
	private String cargoNumber;		// 否	指定规格的货号	
	private int amountOnSale;		// 否	指定规格的供货总量	
	private Double retailPrice;		// 否	建议零售价	
	private Double price;		 	//否	格报价时该规格的单价	
	private Map specAttributes;		// 否	特殊规格	{"3216":"A 1cm宽","450":"均码"}
	
	public OfferSKUItem(){
		this.cargoNumber="";
	}
	
	public String getCargoNumber() {
		return cargoNumber;
	}
	public void setCargoNumber(String cargoNumber) {
		this.cargoNumber = cargoNumber;
	}
	public int getAmountOnSale() {
		return amountOnSale;
	}
	public void setAmountOnSale(int amountOnSale) {
		this.amountOnSale = amountOnSale;
	}
	public Double getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Map getSpecAttributes() {
		return specAttributes;
	}
	public void setSpecAttributes(Map specAttributes) {
		this.specAttributes = specAttributes;
	}
	
	
}
