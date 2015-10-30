package com.wofu.ecommerce.lefeng;

import com.wofu.base.util.BusinessObject;

public class OrderItem extends BusinessObject {

	private String itemCode;
	private String itemName;
	private int itemPirce;
	private int itemQuantity;
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public int getItemPirce() {
		return itemPirce;
	}
	public void setItemPirce(int itemPirce) {
		this.itemPirce = itemPirce;
	}
	public int getItemQuantity() {
		return itemQuantity;
	}
	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	
	
	
}
