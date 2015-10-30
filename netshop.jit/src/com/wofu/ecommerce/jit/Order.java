package com.wofu.ecommerce.jit;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Order extends BusinessObject{

	private long orderId;
	private String orderCode;
	private String orderStatus;
	private double orderAmount;
	private double productAmount;
	private Date orderCreateTime;
	private double orderDeliveryFee;
	private int orderNeedInvoice;
	private String goodReceiverName;
	private String goodReceiverAddress;
	private String goodReceiverProvince;
	private String goodReceiverCity;
	private String goodReceiverCounty;
	private String goodReceiverPostCode;
	private String goodReceiverPhone;
	private String goodReceiverMoblie;
	private Date deliveryDate;
	private Date receiveDate;
	private String deliveryRemark;
	private int deliverySupplierId;
	private String merchantRemark;
	private Date orderPaymentConfirmDate;
	private int payServiceType;
	private double orderPromotionDiscount;
	private String merchantExpressNbr;
	private Date updateTime;
	private int siteType;
	private double orderCouponDiscount;
	private double orderPlatformDiscount;
	private String invoiceTitle;
	private String invoiceContent;
	private long endUserId;
	
	private DataRelation orderItemList =new DataRelation("orderItemList","com.wofu.ecommerce.yhd.OrderItem");
	
	public DataRelation getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(DataRelation orderItemList) {
		this.orderItemList = orderItemList;
	}
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public double getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}
	public double getProductAmount() {
		return productAmount;
	}
	public void setProductAmount(double productAmount) {
		this.productAmount = productAmount;
	}
	public Date getOrderCreateTime() {
		return orderCreateTime;
	}
	public void setOrderCreateTime(Date orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}
	public double getOrderDeliveryFee() {
		return orderDeliveryFee;
	}
	public void setOrderDeliveryFee(double orderDeliveryFee) {
		this.orderDeliveryFee = orderDeliveryFee;
	}
	public int getOrderNeedInvoice() {
		return orderNeedInvoice;
	}
	public void setOrderNeedInvoice(int orderNeedInvoice) {
		this.orderNeedInvoice = orderNeedInvoice;
	}
	public String getGoodReceiverName() {
		return goodReceiverName;
	}
	public void setGoodReceiverName(String goodReceiverName) {
		this.goodReceiverName = goodReceiverName;
	}
	public String getGoodReceiverAddress() {
		return goodReceiverAddress;
	}
	public void setGoodReceiverAddress(String goodReceiverAddress) {
		this.goodReceiverAddress = goodReceiverAddress;
	}
	public String getGoodReceiverProvince() {
		return goodReceiverProvince;
	}
	public void setGoodReceiverProvince(String goodReceiverProvince) {
		this.goodReceiverProvince = goodReceiverProvince;
	}
	public String getGoodReceiverCity() {
		return goodReceiverCity;
	}
	public void setGoodReceiverCity(String goodReceiverCity) {
		this.goodReceiverCity = goodReceiverCity;
	}
	public String getGoodReceiverCounty() {
		return goodReceiverCounty;
	}
	public void setGoodReceiverCounty(String goodReceiverCounty) {
		this.goodReceiverCounty = goodReceiverCounty;
	}
	public String getGoodReceiverPostCode() {
		return goodReceiverPostCode;
	}
	public void setGoodReceiverPostCode(String goodReceiverPostCode) {
		this.goodReceiverPostCode = goodReceiverPostCode;
	}
	public String getGoodReceiverPhone() {
		return goodReceiverPhone;
	}
	public void setGoodReceiverPhone(String goodReceiverPhone) {
		this.goodReceiverPhone = goodReceiverPhone;
	}
	public String getGoodReceiverMoblie() {
		return goodReceiverMoblie;
	}
	public void setGoodReceiverMoblie(String goodReceiverMoblie) {
		this.goodReceiverMoblie = goodReceiverMoblie;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public Date getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	public String getDeliveryRemark() {
		return deliveryRemark;
	}
	public void setDeliveryRemark(String deliveryRemark) {
		this.deliveryRemark = deliveryRemark;
	}
	public int getDeliverySupplierId() {
		return deliverySupplierId;
	}
	public void setDeliverySupplierId(int deliverySupplierId) {
		this.deliverySupplierId = deliverySupplierId;
	}
	public String getMerchantRemark() {
		return merchantRemark;
	}
	public void setMerchantRemark(String merchantRemark) {
		this.merchantRemark = merchantRemark;
	}
	public Date getOrderPaymentConfirmDate() {
		return orderPaymentConfirmDate;
	}
	public void setOrderPaymentConfirmDate(Date orderPaymentConfirmDate) {
		this.orderPaymentConfirmDate = orderPaymentConfirmDate;
	}
	public int getPayServiceType() {
		return payServiceType;
	}
	public void setPayServiceType(int payServiceType) {
		this.payServiceType = payServiceType;
	}
	public double getOrderPromotionDiscount() {
		return orderPromotionDiscount;
	}
	public void setOrderPromotionDiscount(double orderPromotionDiscount) {
		this.orderPromotionDiscount = orderPromotionDiscount;
	}
	public String getMerchantExpressNbr() {
		return merchantExpressNbr;
	}
	public void setMerchantExpressNbr(String merchantExpressNbr) {
		this.merchantExpressNbr = merchantExpressNbr;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public int getSiteType() {
		return siteType;
	}
	public void setSiteType(int siteType) {
		this.siteType = siteType;
	}
	public double getOrderCouponDiscount() {
		return orderCouponDiscount;
	}
	public void setOrderCouponDiscount(double orderCouponDiscount) {
		this.orderCouponDiscount = orderCouponDiscount;
	}
	public double getOrderPlatformDiscount() {
		return orderPlatformDiscount;
	}
	public void setOrderPlatformDiscount(double orderPlatformDiscount) {
		this.orderPlatformDiscount = orderPlatformDiscount;
	}
	public String getInvoiceTitle() {
		return invoiceTitle;
	}
	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}
	public String getInvoiceContent() {
		return invoiceContent;
	}
	public void setInvoiceContent(String invoiceContent) {
		this.invoiceContent = invoiceContent;
	}
	public long getEndUserId() {
		return endUserId;
	}
	public void setEndUserId(long endUserId) {
		this.endUserId = endUserId;
	}
	
	
	
	
}
