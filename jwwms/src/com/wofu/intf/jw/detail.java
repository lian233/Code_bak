package com.wofu.intf.jw;

import com.wofu.base.util.BusinessObject;

public class detail extends BusinessObject{
	private String uuid="";
	private String orderCode="";//订单编码
	private String orderDetailCode="";//子订单编码
	private String skuId="";//平台SKU编码
	private String outerSkuId="";//外部Sku编号
	private String num="";       //数量
	private String title="";     //商品标题
	private String price="";     //商品价格
	private String payment="0";   //单实际金额
	private String discountPrice="0";   //优惠金额
	private String totalPrice="0";   //应付金额
	private String adjustPrice="0";   //手工调整金额
	private String divideOrderPrice="";   //分摊之后的实付金额
	private String billPrice="0";   //开票金额
	private String partMjzDiscoun="";   //优惠分摊
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrdercode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getOrderDetailCode() {
		return orderDetailCode;
	}
	public void setOrderdetailcode(String orderDetailCode) {
		this.orderDetailCode = orderDetailCode;
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuid(String skuId) {
		this.skuId = skuId;
	}
	public String getOuterSkuId() {
		return outerSkuId;
	}
	public void setOuterskuid(String outerSkuId) {
		this.outerSkuId = outerSkuId;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPayment() {
		return payment;
	}
	public void setPayment(String payment) {
		this.payment = payment;
	}
	public String getDiscountPrice() {
		return discountPrice;
	}
	public void setDiscountprice(String discountPrice) {
		this.discountPrice = discountPrice;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalprice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getAdjustPrice() {
		return adjustPrice;
	}
	public void setAdjustprice(String adjustPrice) {
		this.adjustPrice = adjustPrice;
	}
	public String getDivideOrderPrice() {
		return divideOrderPrice;
	}
	public void setDivideorderprice(String divideOrderPrice) {
		this.divideOrderPrice = divideOrderPrice;
	}
	public String getBillPrice() {
		return billPrice;
	}
	public void setBillprice(String billPrice) {
		this.billPrice = billPrice;
	}
	public String getPartMjzDiscoun() {
		return partMjzDiscoun;
	}
	public void setPartmjzdiscoun(String partMjzDiscoun) {
		this.partMjzDiscoun = partMjzDiscoun;
	}
	
}
