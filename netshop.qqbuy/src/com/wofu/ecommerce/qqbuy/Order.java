package com.wofu.ecommerce.qqbuy;

import java.util.ArrayList;
import java.util.Hashtable;

public class Order {
	private int errorCode = 0 ;
	private String errorMessage = "" ;
	private String bdealId = "" ;
	private String dealId = "" ;
	private String buyerId = "" ;
	private String buyerNick = "" ;
	private String cooperatorId = "" ;
	private String cooperatorName  = "" ;
	private String dealPayType = "" ;
	private String dealPayTypeDesc = "" ;
	private String cftDealId = "" ;
	private String dealGenTime  ;
	private String dealPayTime ;
	private String dealCheckTime ;
	private String dealConsignTime ;
	private String dealEndTime ;
	private String lastUpdateTime ;
	private String dealType = "" ;
	private String dealTypeDesc = "" ;
	private String dealState = "" ;
	private String dealStateDesc = "" ;
	private ArrayList<String> dealProperty = new ArrayList<String>() ;
	private String dealCheckVersion = "" ;
	private String dealCheckDesc  = "" ;
	private float dealTotalFee = 0.0f ;
	private float itemTotalFee = 0.0f ;
	private float insuranceFee = 0.0f ;
	private float dealShippingFee = 0.0f ;
	private float mailPromotionFee = 0.0f ;
	private float dealAdjustFee = 0.0f ;
	private float codFee = 0.0f ;
	private String codFeePayType = "" ;
	private String recvBuyerDemand  = "" ;
	private String recvRegionId = "" ;
	private String recvName = "" ;
	private String recvMobile = "" ;
	private String recvPhone  = "" ;
	private String recvPostcode = "" ;
	private String recvAddress = "" ;
	private String wdealBuyerRemark = "" ;
	private String sellerMark = "" ;
	private String expectRecvDate = "" ;
	private String expectRecvTime = "" ;
	private String haveInvoice = "" ;
	private String invoiceTitle  = "" ;
	private String invoiceContent = "" ;
	private String buyerNote  = "" ;
	private String expressCompanyId = "" ;
	private String expressName  = "" ;
	private String expressDealId  = "" ;
	private String expArriveDays = "" ;
	private String sendDesc = "" ;
	private String transportType = "" ;
	private String transportTypeDesc = "" ;
	private String coopConfirmRecvTime  ;
	private String coopConsignTime ;
	private String dealPlatMark  = "" ;
	private float refundShippingFee = 0.0f ;
	private String refundBeginTime ;
	private String refundEndTime ;
	private String refundReason = "" ;
	private String realStorehouseId = "" ;
	private String fouthPartyLogisticsType = "" ;
	private ArrayList<String> refundList = new ArrayList<String>() ; 
	private ArrayList<OrderItem> itemList = new ArrayList<OrderItem>() ;
	private ArrayList<Hashtable<String, String>> abnormalGoodsList = new ArrayList<Hashtable<String,String>>() ;
	private String subPackageVersion = "" ;
	private String subPackFlag = "" ;
	public String getSubPackageVersion() {
		return subPackageVersion;
	}
	public void setSubPackageVersion(String subPackageVersion) {
		this.subPackageVersion = subPackageVersion;
	}
	public String getSubPackFlag() {
		return subPackFlag;
	}
	public void setSubPackFlag(String subPackFlag) {
		this.subPackFlag = subPackFlag;
	}
	public ArrayList<Hashtable<String, String>> getAbnormalGoodsList() {
		return abnormalGoodsList;
	}
	public void setAbnormalGoodsList(
			ArrayList<Hashtable<String, String>> abnormalGoodsList) {
		this.abnormalGoodsList = abnormalGoodsList;
	}
	public String getBdealId() {
		return bdealId;
	}
	public void setBdealId(String bdealId) {
		this.bdealId = bdealId;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}
	public String getBuyerNick() {
		return buyerNick;
	}
	public void setBuyerNick(String buyerNick) {
		this.buyerNick = buyerNick;
	}
	public String getBuyerNote() {
		return buyerNote;
	}
	public void setBuyerNote(String buyerNote) {
		this.buyerNote = buyerNote;
	}
	public String getCftDealId() {
		return cftDealId;
	}
	public void setCftDealId(String cftDealId) {
		this.cftDealId = cftDealId;
	}
	public float getCodFee() {
		return codFee;
	}
	public void setCodFee(float codFee) {
		this.codFee = codFee;
	}
	public String getCodFeePayType() {
		return codFeePayType;
	}
	public void setCodFeePayType(String codFeePayType) {
		this.codFeePayType = codFeePayType;
	}
	public String getCoopConfirmRecvTime() {
		return coopConfirmRecvTime;
	}
	public void setCoopConfirmRecvTime(String coopConfirmRecvTime) {
		this.coopConfirmRecvTime = coopConfirmRecvTime;
	}
	public String getCoopConsignTime() {
		return coopConsignTime;
	}
	public void setCoopConsignTime(String coopConsignTime) {
		this.coopConsignTime = coopConsignTime;
	}
	public String getCooperatorId() {
		return cooperatorId;
	}
	public void setCooperatorId(String cooperatorId) {
		this.cooperatorId = cooperatorId;
	}
	public String getCooperatorName() {
		return cooperatorName;
	}
	public void setCooperatorName(String cooperatorName) {
		this.cooperatorName = cooperatorName;
	}
	public float getDealAdjustFee() {
		return dealAdjustFee;
	}
	public void setDealAdjustFee(float dealAdjustFee) {
		this.dealAdjustFee = dealAdjustFee;
	}
	public String getDealCheckDesc() {
		return dealCheckDesc;
	}
	public void setDealCheckDesc(String dealCheckDesc) {
		this.dealCheckDesc = dealCheckDesc;
	}
	public String getDealCheckTime() {
		return dealCheckTime;
	}
	public void setDealCheckTime(String dealCheckTime) {
		this.dealCheckTime = dealCheckTime;
	}
	public String getDealCheckVersion() {
		return dealCheckVersion;
	}
	public void setDealCheckVersion(String dealCheckVersion) {
		this.dealCheckVersion = dealCheckVersion;
	}
	public String getDealConsignTime() {
		return dealConsignTime;
	}
	public void setDealConsignTime(String dealConsignTime) {
		this.dealConsignTime = dealConsignTime;
	}
	public String getDealEndTime() {
		return dealEndTime;
	}
	public void setDealEndTime(String dealEndTime) {
		this.dealEndTime = dealEndTime;
	}
	public String getDealGenTime() {
		return dealGenTime;
	}
	public void setDealGenTime(String dealGenTime) {
		this.dealGenTime = dealGenTime;
	}
	public String getDealId() {
		return dealId;
	}
	public void setDealId(String dealId) {
		this.dealId = dealId;
	}
	public String getDealPayTime() {
		return dealPayTime;
	}
	public void setDealPayTime(String dealPayTime) {
		this.dealPayTime = dealPayTime;
	}
	public String getDealPayType() {
		return dealPayType;
	}
	public void setDealPayType(String dealPayType) {
		this.dealPayType = dealPayType;
	}
	public String getDealPlatMark() {
		return dealPlatMark;
	}
	public void setDealPlatMark(String dealPlatMark) {
		this.dealPlatMark = dealPlatMark;
	}
	public ArrayList<String> getDealProperty() {
		return dealProperty;
	}
	public void setDealProperty(ArrayList<String> dealProperty) {
		this.dealProperty = dealProperty;
	}
	public float getDealShippingFee() {
		return dealShippingFee;
	}
	public void setDealShippingFee(float dealShippingFee) {
		this.dealShippingFee = dealShippingFee;
	}
	public String getDealState() {
		return dealState;
	}
	public void setDealState(String dealState) {
		this.dealState = dealState;
	}
	public String getDealStateDesc() {
		return dealStateDesc;
	}
	public void setDealStateDesc(String dealStateDesc) {
		this.dealStateDesc = dealStateDesc;
	}
	public float getDealTotalFee() {
		return dealTotalFee;
	}
	public void setDealTotalFee(float dealTotalFee) {
		this.dealTotalFee = dealTotalFee;
	}
	public String getDealType() {
		return dealType;
	}
	public void setDealType(String dealType) {
		this.dealType = dealType;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getExpArriveDays() {
		return expArriveDays;
	}
	public void setExpArriveDays(String expArriveDays) {
		this.expArriveDays = expArriveDays;
	}
	public String getExpressCompanyId() {
		return expressCompanyId;
	}
	public void setExpressCompanyId(String expressCompanyId) {
		this.expressCompanyId = expressCompanyId;
	}
	public String getExpressDealId() {
		return expressDealId;
	}
	public void setExpressDealId(String expressDealId) {
		this.expressDealId = expressDealId;
	}
	public String getExpressName() {
		return expressName;
	}
	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}
	public String getFouthPartyLogisticsType() {
		return fouthPartyLogisticsType;
	}
	public void setFouthPartyLogisticsType(String fouthPartyLogisticsType) {
		this.fouthPartyLogisticsType = fouthPartyLogisticsType;
	}
	public String getHaveInvoice() {
		return haveInvoice;
	}
	public void setHaveInvoice(String haveInvoice) {
		this.haveInvoice = haveInvoice;
	}
	public float getInsuranceFee() {
		return insuranceFee;
	}
	public void setInsuranceFee(float insuranceFee) {
		this.insuranceFee = insuranceFee;
	}
	public String getInvoiceContent() {
		return invoiceContent;
	}
	public void setInvoiceContent(String invoiceContent) {
		this.invoiceContent = invoiceContent;
	}
	public String getInvoiceTitle() {
		return invoiceTitle;
	}
	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}
	public ArrayList<OrderItem> getItemList() {
		return itemList;
	}
	public void setItemList(ArrayList<OrderItem> itemList) {
		this.itemList = itemList;
	}
	public float getItemTotalFee() {
		return itemTotalFee;
	}
	public void setItemTotalFee(float itemTotalFee) {
		this.itemTotalFee = itemTotalFee;
	}
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public float getMailPromotionFee() {
		return mailPromotionFee;
	}
	public void setMailPromotionFee(float mailPromotionFee) {
		this.mailPromotionFee = mailPromotionFee;
	}
	public String getRealStorehouseId() {
		return realStorehouseId;
	}
	public void setRealStorehouseId(String realStorehouseId) {
		this.realStorehouseId = realStorehouseId;
	}
	public String getRecvAddress() {
		return recvAddress;
	}
	public void setRecvAddress(String recvAddress) {
		this.recvAddress = recvAddress;
	}
	public String getRecvBuyerDemand() {
		return recvBuyerDemand;
	}
	public void setRecvBuyerDemand(String recvBuyerDemand) {
		this.recvBuyerDemand = recvBuyerDemand;
	}
	public String getRecvMobile() {
		return recvMobile;
	}
	public void setRecvMobile(String recvMobile) {
		this.recvMobile = recvMobile;
	}
	public String getRecvName() {
		return recvName;
	}
	public void setRecvName(String recvName) {
		this.recvName = recvName;
	}
	public String getRecvPhone() {
		return recvPhone;
	}
	public void setRecvPhone(String recvPhone) {
		this.recvPhone = recvPhone;
	}
	public String getRecvPostcode() {
		return recvPostcode;
	}
	public void setRecvPostcode(String recvPostcode) {
		this.recvPostcode = recvPostcode;
	}
	public String getRecvRegionId() {
		return recvRegionId;
	}
	public void setRecvRegionId(String recvRegionId) {
		this.recvRegionId = recvRegionId;
	}
	public String getRefundBeginTime() {
		return refundBeginTime;
	}
	public void setRefundBeginTime(String refundBeginTime) {
		this.refundBeginTime = refundBeginTime;
	}
	public String getRefundEndTime() {
		return refundEndTime;
	}
	public void setRefundEndTime(String refundEndTime) {
		this.refundEndTime = refundEndTime;
	}
	public String getRefundReason() {
		return refundReason;
	}
	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}
	public float getRefundShippingFee() {
		return refundShippingFee;
	}
	public void setRefundShippingFee(float refundShippingFee) {
		this.refundShippingFee = refundShippingFee;
	}
	public String getSendDesc() {
		return sendDesc;
	}
	public void setSendDesc(String sendDesc) {
		this.sendDesc = sendDesc;
	}
	public String getTransportType() {
		return transportType;
	}
	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}
	public String getDealPayTypeDesc() {
		return dealPayTypeDesc;
	}
	public void setDealPayTypeDesc(String dealPayTypeDesc) {
		this.dealPayTypeDesc = dealPayTypeDesc;
	}
	public String getDealTypeDesc() {
		return dealTypeDesc;
	}
	public void setDealTypeDesc(String dealTypeDesc) {
		this.dealTypeDesc = dealTypeDesc;
	}
	public String getWdealBuyerRemark() {
		return wdealBuyerRemark;
	}
	public void setWdealBuyerRemark(String wdealBuyerRemark) {
		this.wdealBuyerRemark = wdealBuyerRemark;
	}
	public String getExpectRecvDate() {
		return expectRecvDate;
	}
	public void setExpectRecvDate(String expectRecvDate) {
		this.expectRecvDate = expectRecvDate;
	}
	public String getExpectRecvTime() {
		return expectRecvTime;
	}
	public void setExpectRecvTime(String expectRecvTime) {
		this.expectRecvTime = expectRecvTime;
	}
	public String getTransportTypeDesc() {
		return transportTypeDesc;
	}
	public void setTransportTypeDesc(String transportTypeDesc) {
		this.transportTypeDesc = transportTypeDesc;
	}
	public ArrayList<String> getRefundList() {
		return refundList;
	}
	public void setRefundList(ArrayList<String> refundList) {
		this.refundList = refundList;
	}
	public String getSellerMark() {
		return sellerMark;
	}
	public void setSellerMark(String sellerMark) {
		this.sellerMark = sellerMark;
	}
	
}
