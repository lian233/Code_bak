package com.wofu.ecommerce.oauthpaipai;

public class OrderItem {
	
	private String dealSubCode;			//子订单id
	private String itemCode;			//商品编码
	private String itemCodeHistory;		//订单的商品快照编码
	private String itemLocalCode;		//商家编码
	private String stockLocalCode;		//商品库存编码
	private String stockAttr;			//买家下单时选择的库存属性
	private String itemDetailLink;		//商品详情的url
	private String itemName;			//商品名称
	private String itemPic80;			//商品图片URL
	private Double itemRetailPrice;		//商品原价(可能取消)
	private double itemDealPrice;		//买家下单时的商品价格
	private double itemAdjustPrice;		//订单的调整价格:正数为订单加价,负数为订单减价
	private double itemDiscountFee;		//购买商品的红包值、折扣优惠价
	private int itemDealCount;			//购买数量
	private String account;				//充值帐号（点卡类商品订单中才有意义）
	private String refundState;			//退款状态，有退款时才有值
	private String refundStateDesc;		//款状态描述，有退款时才有值
	
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getDealSubCode() {
		return dealSubCode;
	}
	public void setDealSubCode(String dealSubCode) {
		this.dealSubCode = dealSubCode;
	}
	public double getItemAdjustPrice() {
		return itemAdjustPrice;
	}
	public void setItemAdjustPrice(double itemAdjustPrice) {
		this.itemAdjustPrice = itemAdjustPrice;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemCodeHistory() {
		return itemCodeHistory;
	}
	public void setItemCodeHistory(String itemCodeHistory) {
		this.itemCodeHistory = itemCodeHistory;
	}
	public int getItemDealCount() {
		return itemDealCount;
	}
	public void setItemDealCount(int itemDealCount) {
		this.itemDealCount = itemDealCount;
	}
	public double getItemDealPrice() {
		return itemDealPrice;
	}
	public void setItemDealPrice(double itemDealPrice) {
		this.itemDealPrice = itemDealPrice;
	}
	public String getItemDetailLink() {
		return itemDetailLink;
	}
	public void setItemDetailLink(String itemDetailLink) {
		this.itemDetailLink = itemDetailLink;
	}
	public double getItemDiscountFee() {
		return itemDiscountFee;
	}
	public void setItemDiscountFee(double itemDiscountFee) {
		this.itemDiscountFee = itemDiscountFee;
	}
	public String getItemLocalCode() {
		return itemLocalCode;
	}
	public void setItemLocalCode(String itemLocalCode) {
		this.itemLocalCode = itemLocalCode;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemPic80() {
		return itemPic80;
	}
	public void setItemPic80(String itemPic80) {
		this.itemPic80 = itemPic80;
	}
	public Double getItemRetailPrice() {
		return itemRetailPrice;
	}
	public void setItemRetailPrice(Double itemRetailPrice) {
		this.itemRetailPrice = itemRetailPrice;
	}
	public String getRefundState() {
		return refundState;
	}
	public void setRefundState(String refundState) {
		this.refundState = refundState;
	}
	public String getRefundStateDesc() {
		return refundStateDesc;
	}
	public void setRefundStateDesc(String refundStateDesc) {
		this.refundStateDesc = refundStateDesc;
	}
	public String getStockAttr() {
		return stockAttr;
	}
	public void setStockAttr(String stockAttr) {
		this.stockAttr = stockAttr;
	}
	public String getStockLocalCode() {
		return stockLocalCode;
	}
	public void setStockLocalCode(String stockLocalCode) {
		this.stockLocalCode = stockLocalCode;
	}
	

}
