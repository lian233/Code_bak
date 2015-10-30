package com.wofu.ecommerce.yhd;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class OrderItem extends BusinessObject {

	private long id;
	private long orderId;
	private String productCName;
	private double orderItemAmount;
	private int orderItemNum;
	private double orderItemPrice;
	private double originalPrice;
	private long productId;
	private int groupFlag;
	private long merchantId;
	private Date processFinishDate;
	private Date updateTime;
	private String outerId;
	private double deliveryFeeAmount;
	private double promotionAmount;
	private double couponAmountMerchant;
	private double couponPlatformDiscount;
	private double subsidyAmount;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public String getProductCName() {
		return productCName;
	}
	public void setProductCName(String productCName) {
		this.productCName = productCName;
	}
	public double getOrderItemAmount() {
		return orderItemAmount;
	}
	public void setOrderItemAmount(double orderItemAmount) {
		this.orderItemAmount = orderItemAmount;
	}
	public int getOrderItemNum() {
		return orderItemNum;
	}
	public void setOrderItemNum(int orderItemNum) {
		this.orderItemNum = orderItemNum;
	}
	public double getOrderItemPrice() {
		return orderItemPrice;
	}
	public void setOrderItemPrice(double orderItemPrice) {
		this.orderItemPrice = orderItemPrice;
	}
	public double getOriginalPrice() {
		return originalPrice;
	}
	public void setOriginalPrice(double originalPrice) {
		this.originalPrice = originalPrice;
	}
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public int getGroupFlag() {
		return groupFlag;
	}
	public void setGroupFlag(int groupFlag) {
		this.groupFlag = groupFlag;
	}
	public long getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}
	public Date getProcessFinishDate() {
		return processFinishDate;
	}
	public void setProcessFinishDate(Date processFinishDate) {
		this.processFinishDate = processFinishDate;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getOuterId() {
		return outerId;
	}
	public void setOuterId(String outerId) {
		this.outerId = outerId;
	}
	public double getDeliveryFeeAmount() {
		return deliveryFeeAmount;
	}
	public void setDeliveryFeeAmount(double deliveryFeeAmount) {
		this.deliveryFeeAmount = deliveryFeeAmount;
	}
	public double getPromotionAmount() {
		return promotionAmount;
	}
	public void setPromotionAmount(double promotionAmount) {
		this.promotionAmount = promotionAmount;
	}
	public double getCouponAmountMerchant() {
		return couponAmountMerchant;
	}
	public void setCouponAmountMerchant(double couponAmountMerchant) {
		this.couponAmountMerchant = couponAmountMerchant;
	}
	public double getCouponPlatformDiscount() {
		return couponPlatformDiscount;
	}
	public void setCouponPlatformDiscount(double couponPlatformDiscount) {
		this.couponPlatformDiscount = couponPlatformDiscount;
	}
	public double getSubsidyAmount() {
		return subsidyAmount;
	}
	public void setSubsidyAmount(double subsidyAmount) {
		this.subsidyAmount = subsidyAmount;
	}
	
	
	
}
