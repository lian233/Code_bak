package com.wofu.ecommerce.yougou;

import com.wofu.base.util.BusinessObject;

public class RefundItem extends BusinessObject {
	
	private long productId;
	private String productCname;
	private long orderItemNum;
	private double orderItemPrice;
	private long productRefundNum;
	private long originalRefundNum;
	private long orderItemId;
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public String getProductCname() {
		return productCname;
	}
	public void setProductCname(String productCname) {
		this.productCname = productCname;
	}
	public long getOrderItemNum() {
		return orderItemNum;
	}
	public void setOrderItemNum(long orderItemNum) {
		this.orderItemNum = orderItemNum;
	}
	public double getOrderItemPrice() {
		return orderItemPrice;
	}
	public void setOrderItemPrice(double orderItemPrice) {
		this.orderItemPrice = orderItemPrice;
	}
	public long getProductRefundNum() {
		return productRefundNum;
	}
	public void setProductRefundNum(long productRefundNum) {
		this.productRefundNum = productRefundNum;
	}
	public long getOriginalRefundNum() {
		return originalRefundNum;
	}
	public void setOriginalRefundNum(long originalRefundNum) {
		this.originalRefundNum = originalRefundNum;
	}
	public long getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(long orderItemId) {
		this.orderItemId = orderItemId;
	}
	
	

}
