package com.wofu.ecommerce.dangdang;

/**
 * 
 * �����е���Ʒ
 *
 */
public class OrderItem {
	private String itemID="";//��Ʒ��ʶ
	private String outerItemID="";//��ҵ��Ʒ��ʶ����sku��
	private String itemName="";//��Ʒ����
	private String itemType="";//0��Ʒ��1��Ʒ
	private String specialAttribute="";//��ɫ����
	private float marketPrice=0.0f;//�г��� 
	private float unitPrice=0.0f;//�ɽ���
	private int orderCount=0;//������������
	private int sendGoodsCount=0;//�����������������Ѿ�����ʱ���Ż᷵��  
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
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public float getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(float marketPrice) {
		this.marketPrice = marketPrice;
	}
	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
	public String getOuterItemID() {
		return outerItemID;
	}
	public void setOuterItemID(String outerItemID) {
		this.outerItemID = outerItemID;
	}
	public int getSendGoodsCount() {
		return sendGoodsCount;
	}
	public void setSendGoodsCount(int sendGoodsCount) {
		this.sendGoodsCount = sendGoodsCount;
	}
	public String getSpecialAttribute() {
		return specialAttribute;
	}
	public void setSpecialAttribute(String specialAttribute) {
		this.specialAttribute = specialAttribute;
	}
	public float getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice;
	}
}
