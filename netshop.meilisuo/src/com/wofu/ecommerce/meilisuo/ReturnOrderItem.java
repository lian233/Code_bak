package com.wofu.ecommerce.meilisuo;

public class ReturnOrderItem {
	private String itemID = "" ;//��Ʒ��ʶ
	private String itemName = "" ;//��Ʒ����
	private String itemSubhead = "" ;//��Ʒ����(�̱���)
	private float unitPrice = 0.0f ; //�ɽ���
	private int orderCount = 0 ; //��������
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
