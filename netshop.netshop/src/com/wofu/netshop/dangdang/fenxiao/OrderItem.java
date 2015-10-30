package com.wofu.netshop.dangdang.fenxiao;

/**
 * 
 * 订单中的商品
 *
 */
public class OrderItem {
	private String itemID="";//商品标识
	private String outerItemID="";//企业商品标识符（sku）
	private String itemName="";//商品名称
	private String itemType="";//0商品，1赠品
	private String specialAttribute="";//分色分码
	private float marketPrice=0.0f;//市场价 
	private float unitPrice=0.0f;//成交价
	private int orderCount=0;//网购订货数量
	private int sendGoodsCount=0;//发货数量，当订单已经发货时，才会返回  
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
