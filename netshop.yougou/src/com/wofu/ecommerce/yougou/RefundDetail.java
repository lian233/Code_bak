package com.wofu.ecommerce.yougou;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class RefundDetail extends BusinessObject {

	private String refundCode;
	private long orderId;
	private String orderCode;
	private int refundStatus;
	private double deliveryFee;
	private double productAmount;
	private String contactName;
	private String contactPhone;
	private String sendBackAddress;
	private String reasonMsg;
	private String refundProblem;
	private String evidencePicUrls;
	private String receiverName;
	private String receiverPhone;
	private String receiverAddress;
	private Date applyDate;
	private String merchantMark;
	private String merchantRemark;
	private Date approveDate;
	private Date sendBackDate;
	private Date rejectDate;
	private Date cancelTime;
	private String expressName;
	private String expressNbr;
	
	private DataRelation refundItemList =new DataRelation("refundItemList","com.wofu.ecommerce.yhd.RefundItem");
	
	
	
	public DataRelation getRefundItemList() {
		return refundItemList;
	}
	public void setRefundItemList(DataRelation refundItemList) {
		this.refundItemList = refundItemList;
	}
	public String getRefundCode() {
		return refundCode;
	}
	public void setRefundCode(String refundCode) {
		this.refundCode = refundCode;
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
	public int getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(int refundStatus) {
		this.refundStatus = refundStatus;
	}
	public double getDeliveryFee() {
		return deliveryFee;
	}
	public void setDeliveryFee(double deliveryFee) {
		this.deliveryFee = deliveryFee;
	}
	public double getProductAmount() {
		return productAmount;
	}
	public void setProductAmount(double productAmount) {
		this.productAmount = productAmount;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getSendBackAddress() {
		return sendBackAddress;
	}
	public void setSendBackAddress(String sendBackAddress) {
		this.sendBackAddress = sendBackAddress;
	}
	public String getReasonMsg() {
		return reasonMsg;
	}
	public void setReasonMsg(String reasonMsg) {
		this.reasonMsg = reasonMsg;
	}
	public String getRefundProblem() {
		return refundProblem;
	}
	public void setRefundProblem(String refundProblem) {
		this.refundProblem = refundProblem;
	}
	public String getEvidencePicUrls() {
		return evidencePicUrls;
	}
	public void setEvidencePicUrls(String evidencePicUrls) {
		this.evidencePicUrls = evidencePicUrls;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
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
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public String getMerchantMark() {
		return merchantMark;
	}
	public void setMerchantMark(String merchantMark) {
		this.merchantMark = merchantMark;
	}
	public String getMerchantRemark() {
		return merchantRemark;
	}
	public void setMerchantRemark(String merchantRemark) {
		this.merchantRemark = merchantRemark;
	}
	public Date getApproveDate() {
		return approveDate;
	}
	public void setApproveDate(Date approveDate) {
		this.approveDate = approveDate;
	}
	public Date getSendBackDate() {
		return sendBackDate;
	}
	public void setSendBackDate(Date sendBackDate) {
		this.sendBackDate = sendBackDate;
	}
	public Date getRejectDate() {
		return rejectDate;
	}
	public void setRejectDate(Date rejectDate) {
		this.rejectDate = rejectDate;
	}
	public Date getCancelTime() {
		return cancelTime;
	}
	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}
	public String getExpressName() {
		return expressName;
	}
	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}
	public String getExpressNbr() {
		return expressNbr;
	}
	public void setExpressNbr(String expressNbr) {
		this.expressNbr = expressNbr;
	}
	
	
	
}
