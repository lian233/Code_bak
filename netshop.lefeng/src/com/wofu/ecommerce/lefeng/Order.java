package com.wofu.ecommerce.lefeng;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Order extends BusinessObject {
	private String orderCode;
	private String createTime;
	private int status;
	private int orderStatus;
	private int payType;
	private String payTime;
	private int payerUserId;
	private int totalPay;
	private int realPay;
	private int hadPay;
	private int pointPay;
	private String receiverName;
	private String receiverMobile;
	private String receiverPhone;
	private String receiverAddress;
	private String receiverProvince;
	private String receiverArea;
	private String receiverPostcode;
	private int deliverType;
	private int deliverId;
	private int deliverPay;  //运费
	private String deliverMemo;
	private String hasInvoice;
	private String invoiceTitle;
	private String invoiceContent;
	private String operatorMemo; //订单备注
	
	private DataRelation itemList =new DataRelation("itemList","com.wofu.ecommerce.lefeng.OrderItem");

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}

	public int getPayerUserId() {
		return payerUserId;
	}

	public void setPayerUserId(int payerUserId) {
		this.payerUserId = payerUserId;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public int getTotalPay() {
		return totalPay;
	}

	public void setTotalPay(int totalPay) {
		this.totalPay = totalPay;
	}

	public int getRealPay() {
		return realPay;
	}

	public void setRealPay(int realPay) {
		this.realPay = realPay;
	}

	public int getHadPay() {
		return hadPay;
	}

	public void setHadPay(int hadPay) {
		this.hadPay = hadPay;
	}

	public int getPointPay() {
		return pointPay;
	}

	public void setPointPay(int pointPay) {
		this.pointPay = pointPay;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverMobile() {
		return receiverMobile;
	}

	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}

	public String getReceiverPhone() {
		return receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	public String getReceiverProvince() {
		return receiverProvince;
	}

	public void setReceiverProvince(String receiverProvince) {
		this.receiverProvince = receiverProvince;
	}

	public String getReceiverArea() {
		return receiverArea;
	}

	public void setReceiverArea(String receiverArea) {
		this.receiverArea = receiverArea;
	}

	public String getReceiverPostcode() {
		return receiverPostcode;
	}

	public void setReceiverPostcode(String receiverPostcode) {
		this.receiverPostcode = receiverPostcode;
	}

	public int getDeliverType() {
		return deliverType;
	}

	public void setDeliverType(int deliverType) {
		this.deliverType = deliverType;
	}

	public int getDeliverId() {
		return deliverId;
	}

	public void setDeliverId(int deliverId) {
		this.deliverId = deliverId;
	}

	public int getDeliverPay() {
		return deliverPay;
	}

	public void setDeliverPay(int deliverPay) {
		this.deliverPay = deliverPay;
	}

	public String getDeliverMemo() {
		return deliverMemo;
	}

	public void setDeliverMemo(String deliverMemo) {
		this.deliverMemo = deliverMemo;
	}

	public String getHasInvoice() {
		return hasInvoice;
	}

	public void setHasInvoice(String hasInvoice) {
		this.hasInvoice = hasInvoice;
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

	public String getOperatorMemo() {
		return operatorMemo;
	}

	public void setOperatorMemo(String operatorMemo) {
		this.operatorMemo = operatorMemo;
	}

	public DataRelation getItemList() {
		return itemList;
	}

	public void setItemList(DataRelation itemList) {
		this.itemList = itemList;
	}
}
