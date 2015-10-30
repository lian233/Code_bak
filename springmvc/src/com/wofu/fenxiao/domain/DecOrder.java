package com.wofu.fenxiao.domain;

import java.util.Date;
import java.util.List;

import com.wofu.fenxiao.mapping.DecOrderItemMapper;

public class DecOrder {
	private int id;
	private String sheetID;
	private String refSheetID;
	private int customerID;
	private int shopID;
	private int deliveryID;
	private int outFlag;
	private int flag;
	private String editor;
	private Date editTime;
	private String checker;
	private Date checkTime;
	private String buyerMemo;
	private String sellerMemo;
	private String buyerMessage;
	private String tradeMemo;
	private String buyerNick;
	private String sellerNick;
	private String state;
	private String city;
	private String district;
	private String address;
	private String phone;
	private String mobile;
	private String linkMan;
	private String zipCode;
	private String buyerEmail;
	private double postFee;
	private String customState;
	private int sheetFlag;
	private String promotionDetails;
	private String tradeFrom;
	private double payFee;
	private int payMode;
	private int invoiceFlag;
	private String invoiceTitle;
	private String distributorID;
	private String distributeTid;
	private String distributorShopName;
	private int refundFlag;
	private int overSaleFlag;
	private String canceler;
	private String deliverySheetID;
	private int weigh;
	private int mark;
	private Date createTime;
	private Date payTime;
	private Date printTime;
	private int printTimes;
	private Date modified;
	private Date endTime;
	private Date sendTime;
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	private String buyerAlipayNo;
	private String alipayNo;
	private String invoiceID;
	private String addressID;
	private String zoneCode;
	private int sellerFlag;
	private String keyNote;
	private String picNote;
	private String itemContent;
	private int itemCount;
	private int totalQty;
	private double totalAmount;
	private double totalDistributePrice;
	
	private String note;
	private String front ;
	private int merFlag ;
		
	public int getMerFlag() {
		return merFlag;
	}
	public void setMerFlag(int merFlag) {
		this.merFlag = merFlag;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSheetID() {
		return sheetID;
	}
	public void setSheetID(String sheetID) {
		this.sheetID = sheetID;
	}
	public String getRefSheetID() {
		return refSheetID;
	}
	public void setRefSheetID(String refSheetID) {
		this.refSheetID = refSheetID;
	}
	public int getCustomerID() {
		return customerID;
	}
	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}
	public int getShopID() {
		return shopID;
	}
	public void setShopID(int shopID) {
		this.shopID = shopID;
	}
	public int getDeliveryID() {
		return deliveryID;
	}
	public void setDeliveryID(int deliveryID) {
		this.deliveryID = deliveryID;
	}
	public int getOutFlag() {
		return outFlag;
	}
	public void setOutFlag(int outFlag) {
		this.outFlag = outFlag;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	public Date getEditTime() {
		return editTime;
	}
	public void setEditTime(Date editTime) {
		this.editTime = editTime;
	}
	public String getChecker() {
		return checker;
	}
	public void setChecker(String checker) {
		this.checker = checker;
	}
	public Date getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}
	public String getBuyerMemo() {
		return buyerMemo;
	}
	public void setBuyerMemo(String buyerMemo) {
		this.buyerMemo = buyerMemo;
	}
	public String getSellerMemo() {
		return sellerMemo;
	}
	public void setSellerMemo(String sellerMemo) {
		this.sellerMemo = sellerMemo;
	}
	public String getBuyerMessage() {
		return buyerMessage;
	}
	public void setBuyerMessage(String buyerMessage) {
		this.buyerMessage = buyerMessage;
	}
	public String getTradeMemo() {
		return tradeMemo;
	}
	public void setTradeMemo(String tradeMemo) {
		this.tradeMemo = tradeMemo;
	}
	public String getBuyerNick() {
		return buyerNick;
	}
	public void setBuyerNick(String buyerNick) {
		this.buyerNick = buyerNick;
	}
	public String getSellerNick() {
		return sellerNick;
	}
	public void setSellerNick(String sellerNick) {
		this.sellerNick = sellerNick;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getLinkMan() {
		return linkMan;
	}
	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getBuyerEmail() {
		return buyerEmail;
	}
	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}
	public double getPostFee() {
		return postFee;
	}
	public void setPostFee(double postFee) {
		this.postFee = postFee;
	}
	public String getCustomState() {
		return customState;
	}
	public void setCustomState(String customState) {
		this.customState = customState;
	}
	public int getSheetFlag() {
		return sheetFlag;
	}
	public void setSheetFlag(int sheetFlag) {
		this.sheetFlag = sheetFlag;
	}
	public String getPromotionDetails() {
		return promotionDetails;
	}
	public void setPromotionDetails(String promotionDetails) {
		this.promotionDetails = promotionDetails;
	}
	public String getTradeFrom() {
		return tradeFrom;
	}
	public void setTradeFrom(String tradeFrom) {
		this.tradeFrom = tradeFrom;
	}
	public double getPayFee() {
		return payFee;
	}
	public void setPayFee(double payFee) {
		this.payFee = payFee;
	}
	public int getPayMode() {
		return payMode;
	}
	public void setPayMode(int payMode) {
		this.payMode = payMode;
	}
	public int getInvoiceFlag() {
		return invoiceFlag;
	}
	public void setInvoiceFlag(int invoiceFlag) {
		this.invoiceFlag = invoiceFlag;
	}
	public String getInvoiceTitle() {
		return invoiceTitle;
	}
	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}
	public String getDistributorID() {
		return distributorID;
	}
	public void setDistributorID(String distributorID) {
		this.distributorID = distributorID;
	}
	public String getDistributeTid() {
		return distributeTid;
	}
	public void setDistributeTid(String distributeTid) {
		this.distributeTid = distributeTid;
	}
	public String getDistributorShopName() {
		return distributorShopName;
	}
	public void setDistributorShopName(String distributorShopName) {
		this.distributorShopName = distributorShopName;
	}
	public int getRefundFlag() {
		return refundFlag;
	}
	public void setRefundFlag(int refundFlag) {
		this.refundFlag = refundFlag;
	}
	public int getOverSaleFlag() {
		return overSaleFlag;
	}
	public void setOverSaleFlag(int overSaleFlag) {
		this.overSaleFlag = overSaleFlag;
	}
	public String getCanceler() {
		return canceler;
	}
	public void setCanceler(String canceler) {
		this.canceler = canceler;
	}
	public String getDeliverySheetID() {
		return deliverySheetID;
	}
	public void setDeliverySheetID(String deliverySheetID) {
		this.deliverySheetID = deliverySheetID;
	}
	public int getWeigh() {
		return weigh;
	}
	public void setWeigh(int weigh) {
		this.weigh = weigh;
	}
	public int getMark() {
		return mark;
	}
	public void setMark(int mark) {
		this.mark = mark;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createtime) {
		this.createTime = createtime;
	}
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	public Date getPrintTime() {
		return printTime;
	}
	public void setPrintTime(Date printTime) {
		this.printTime = printTime;
	}
	public int getPrintTimes() {
		return printTimes;
	}
	public void setPrintTimes(int printTimes) {
		this.printTimes = printTimes;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getBuyerAlipayNo() {
		return buyerAlipayNo;
	}
	public void setBuyerAlipayNo(String buyerAlipayNo) {
		this.buyerAlipayNo = buyerAlipayNo;
	}
	public String getAlipayNo() {
		return alipayNo;
	}
	public void setAlipayNo(String alipayNo) {
		this.alipayNo = alipayNo;
	}
	public String getInvoiceID() {
		return invoiceID;
	}
	public void setInvoiceID(String invoiceID) {
		this.invoiceID = invoiceID;
	}
	public String getAddressID() {
		return addressID;
	}
	public void setAddressID(String addressID) {
		this.addressID = addressID;
	}
	public int getSellerFlag() {
		return sellerFlag;
	}
	public void setSellerFlag(int sellerFlag) {
		this.sellerFlag = sellerFlag;
	}
	public String getKeyNote() {
		return keyNote;
	}
	public void setKeyNote(String keyNote) {
		this.keyNote = keyNote;
	}
	public String getPicNote() {
		return picNote;
	}
	public void setPicNote(String picNote) {
		this.picNote = picNote;
	}
	public String getItemContent() {
		return itemContent;
	}
	public void setItemContent(String itemContent) {
		this.itemContent = itemContent;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	public int getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(int totalQty) {
		this.totalQty = totalQty;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public void setFront(String front) {
		this.front = front;
	}
	public String getFront() {
		return front;
	}

	public String getZoneCode() {
		return zoneCode;
	}
	public void setZoneCode(String zoneCode) {
		this.zoneCode = zoneCode;
	}
	public double getTotalDistributePrice() {
		return totalDistributePrice;
	}
	public void setTotalDistributePrice(double totalDistributePrice) {
		this.totalDistributePrice = totalDistributePrice;
	}
	
	
	
	
}
