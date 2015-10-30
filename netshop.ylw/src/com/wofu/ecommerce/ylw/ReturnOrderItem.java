package com.wofu.ecommerce.ylw;

public class ReturnOrderItem {
	private String itemID = "" ;//商品标识
	private String itemName = "" ;//商品名称
	private String itemSubhead = "" ;//商品名称(短标题)
	private float unitPrice = 0.0f ; //成交价
	private int orderCount = 0 ; //购买数量
	private String outerItemID = "" ;//sku
	public String getOuterItemID() {
		return outerItemID;
	}
	public void setOuterItemID(String outerItemID) {
		this.outerItemID = outerItemID;
	}
	public String getItemID() {
		return itemID;
	}
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
	public float getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getItemSubhead() {
		return itemSubhead;
	}
	public void setItemSubhead(String itemSubhead) {
		this.itemSubhead = itemSubhead;
	}
}
