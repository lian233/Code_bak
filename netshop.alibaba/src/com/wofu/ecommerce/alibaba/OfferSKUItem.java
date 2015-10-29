package com.wofu.ecommerce.alibaba;

import java.util.Map;

import com.wofu.base.util.BusinessObject;

public class OfferSKUItem extends BusinessObject {
	private String cargoNumber;		// ��	ָ�����Ļ���	
	private int amountOnSale;		// ��	ָ�����Ĺ�������	
	private Double retailPrice;		// ��	�������ۼ�	
	private Double price;		 	//��	�񱨼�ʱ�ù��ĵ���	
	private Map specAttributes;		// ��	������	{"3216":"A 1cm��","450":"����"}
	
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
