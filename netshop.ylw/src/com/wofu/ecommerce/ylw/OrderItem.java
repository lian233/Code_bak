package com.wofu.ecommerce.ylw;

import com.wofu.base.util.BusinessObject;

/**
 * 
 * 订单中的商品
 *
 */
public class OrderItem extends BusinessObject{
	private String productCode="";//商品标识
	private String itemCode="";//企业商品标识符（sku）
	private String productName="";//商品名称
	private String itemType="";//0商品，1赠品
	//private String specialAttribute="";//分色分码
	private float marketPrice=0.0f;//市场价 
	private float unitPrice=0.0f;//成交价 订单返回的价格
	private float saleNum=0;//网购订货数量
	private int sendGoodsCount=0;//发货数量，当订单已经发货时，才会返回  
	private float coupontotalMoney=0.00f;   //优惠劵总金额
	private float vouchertotalMoney=0.00f;  //优惠单总金额
	private float payAmount=0.00f;          //实际收款
	private float transportFee=0.0f;        //邮费
	private String orderLineStatus = "";//订单状态
	private String returnOrderFlag="";//退货标志
	public String getReturnOrderFlag() {
		return returnOrderFlag;
	}
	public void setReturnOrderFlag(String returnOrderFlag) {
		this.returnOrderFlag = returnOrderFlag;
	}
	private String picPath="";
	public String getOrderLineStatus() {
		return orderLineStatus;
	}
	public void setOrderLineStatus(String orderLineStatus) {
		this.orderLineStatus = orderLineStatus;
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

	public int getSendGoodsCount() {
		return sendGoodsCount;
	}
	public void setSendGoodsCount(int sendGoodsCount) {
		this.sendGoodsCount = sendGoodsCount;
	}

	public float getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public float getSaleNum() {
		return saleNum;
	}
	public void setSaleNum(float saleNum) {
		this.saleNum = saleNum;
	}
	public float getCoupontotalMoney() {
		return coupontotalMoney;
	}
	public void setCoupontotalMoney(float coupontotalMoney) {
		this.coupontotalMoney = coupontotalMoney;
	}
	public float getVouchertotalMoney() {
		return vouchertotalMoney;
	}
	public void setVouchertotalMoney(float vouchertotalMoney) {
		this.vouchertotalMoney = vouchertotalMoney;
	}
	public float getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(float payAmount) {
		this.payAmount = payAmount;
	}
	public float getTransportFee() {
		return transportFee;
	}
	public void setTransportFee(float transportFee) {
		this.transportFee = transportFee;
	}
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	
	
	
	
}
