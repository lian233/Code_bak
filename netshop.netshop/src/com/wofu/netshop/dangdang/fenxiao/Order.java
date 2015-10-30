package com.wofu.netshop.dangdang.fenxiao;

import java.util.ArrayList;
import java.util.Date;

import com.wofu.netshop.dangdang.fenxiao.OperateInfo;
/**
 * 
 * 订单类
 *
 */
public class Order {
	private String orderID = "";//订单编号
	private String orderState = "";//订单状态
	private String message = ""; //买家留言
	private String remark =""; //备注
	private String label = "" ; //5类固定标志 1红色2黄色3绿色4蓝色5紫色
	private Date lastModifyTime; //最后修改时间
	
	private String buyerPayMode="";//买家支付方式
	private float totalFee = 0f ;//订单商品总金额
	private float goodsMoney =0.0f;//本订单商家应收金额
	private float deductAmount=0.0f;//网银支付满额减优惠金额
	private float totalBarginPrice=0.0f;//顾客需为订单支付现金 
	private float postage=0.0f;//买家支付邮费 
	private float giftCertMoney=0.0f;//买家支付礼卷金额 
	private float giftCardMoney=0.0f; //买家支付礼品卡金额
	private float accountBalance=0.0f;//买家支付帐户余额 
	//收货人信息
	private String consigneeName="";//收货人姓名
	private String consigneeAddr="";//收货地址
	private String consigneeAddrStte="";//收货国家
	private String consigneeAddrProvince="";//收货省份
	private String consigneeAddrCity="";//收货市
	private String consigneeAddrArea="";//收货区
	private String consigneePostcode="";//邮编
	private String consigneeTel="";//固定电话
	private String consigneeMobileTel="";//移动电话
	private String sendGoodsMode="";//配送方式
	private String sendCompany="";//物流公司
	private String sendOrderID="";//送货单号
	private String dangdangWarehouseAddr="";//商家需要把包裹发到当当仓库地址

	//发票
	private String receiptName="";//发票台头
	private String receiptDetails="";//发票内容
	private float receiptMoney=0.0f;//发票金额
	private String isDangdangReceipt="";//1:由当当代开发票 2：不由当当代开发票
	//处理结果
	private String operCode="";//操作码
	private String operation="";//操作结果信息

	private ArrayList<OrderItem> orderItemList=new ArrayList<OrderItem>();
	private ArrayList<OperateInfo> operateInfoList = new ArrayList<OperateInfo>() ;//订单操作列表
	//发票
	private ArrayList<String> invoiceItem = new ArrayList<String>() ;
	
	public ArrayList<String> getInvoiceItem() {
		return invoiceItem;
	}
	public void setInvoiceItem(ArrayList<String> invoiceItem) {
		this.invoiceItem = invoiceItem;
	}
	public float getAccountBalance() {
		return accountBalance;
	}
	public void setAccountBalance(float accountBalance) {
		this.accountBalance = accountBalance;
	}
	public String getBuyerPayMode() {
		return buyerPayMode;
	}
	public void setBuyerPayMode(String buyerPayMode) {
		this.buyerPayMode = buyerPayMode;
	}
	public String getConsigneeAddr() {
		return consigneeAddr;
	}
	public void setConsigneeAddr(String consigneeAddr) {
		this.consigneeAddr = consigneeAddr;
	}
	public String getConsigneeAddrArea() {
		return consigneeAddrArea;
	}
	public void setConsigneeAddrArea(String consigneeAddrArea) {
		this.consigneeAddrArea = consigneeAddrArea;
	}
	public String getConsigneeAddrCity() {
		return consigneeAddrCity;
	}
	public void setConsigneeAddrCity(String consigneeAddrCity) {
		this.consigneeAddrCity = consigneeAddrCity;
	}
	public String getConsigneeAddrProvince() {
		return consigneeAddrProvince;
	}
	public void setConsigneeAddrProvince(String consigneeAddrProvince) {
		this.consigneeAddrProvince = consigneeAddrProvince;
	}
	public String getConsigneeAddrStte() {
		return consigneeAddrStte;
	}
	public void setConsigneeAddrStte(String consigneeAddrStte) {
		this.consigneeAddrStte = consigneeAddrStte;
	}
	public String getConsigneeMobileTel() {
		return consigneeMobileTel;
	}
	public void setConsigneeMobileTel(String consigneeMobileTel) {
		this.consigneeMobileTel = consigneeMobileTel;
	}
	public String getConsigneeName() {
		return consigneeName;
	}
	public void setConsigneeName(String consigneeName) {
		this.consigneeName = consigneeName;
	}
	public String getConsigneePostcode() {
		return consigneePostcode;
	}
	public void setConsigneePostcode(String consigneePostcode) {
		this.consigneePostcode = consigneePostcode;
	}
	public String getConsigneeTel() {
		return consigneeTel;
	}
	public void setConsigneeTel(String consigneeTel) {
		this.consigneeTel = consigneeTel;
	}
	public String getDangdangWarehouseAddr() {
		return dangdangWarehouseAddr;
	}
	public void setDangdangWarehouseAddr(String dangdangWarehouseAddr) {
		this.dangdangWarehouseAddr = dangdangWarehouseAddr;
	}
	public float getDeductAmount() {
		return deductAmount;
	}
	public void setDeductAmount(float deductAmount) {
		this.deductAmount = deductAmount;
	}
	public float getGiftCardMoney() {
		return giftCardMoney;
	}
	public void setGiftCardMoney(float giftCardMoney) {
		this.giftCardMoney = giftCardMoney;
	}
	public float getGiftCertMoney() {
		return giftCertMoney;
	}
	public void setGiftCertMoney(float giftCertMoney) {
		this.giftCertMoney = giftCertMoney;
	}
	public float getGoodsMoney() {
		return goodsMoney;
	}
	public void setGoodsMoney(float goodsMoney) {
		this.goodsMoney = goodsMoney;
	}
	public String getIsDangdangReceipt() {
		return isDangdangReceipt;
	}
	public void setIsDangdangReceipt(String isDangdangReceipt) {
		this.isDangdangReceipt = isDangdangReceipt;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Date getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ArrayList<OperateInfo> getOperateInfoList() {
		return operateInfoList;
	}
	public void setOperateInfoList(ArrayList<OperateInfo> operateInfoList) {
		this.operateInfoList = operateInfoList;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getOperCode() {
		return operCode;
	}
	public void setOperCode(String operCode) {
		this.operCode = operCode;
	}
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public ArrayList<OrderItem> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(ArrayList<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	public float getPostage() {
		return postage;
	}
	public void setPostage(float postage) {
		this.postage = postage;
	}
	public String getReceiptDetails() {
		return receiptDetails;
	}
	public void setReceiptDetails(String receiptDetails) {
		this.receiptDetails = receiptDetails;
	}
	public float getReceiptMoney() {
		return receiptMoney;
	}
	public void setReceiptMoney(float receiptMoney) {
		this.receiptMoney = receiptMoney;
	}
	public String getReceiptName() {
		return receiptName;
	}
	public void setReceiptName(String receiptName) {
		this.receiptName = receiptName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSendCompany() {
		return sendCompany;
	}
	public void setSendCompany(String sendCompany) {
		this.sendCompany = sendCompany;
	}
	public String getSendGoodsMode() {
		return sendGoodsMode;
	}
	public void setSendGoodsMode(String sendGoodsMode) {
		this.sendGoodsMode = sendGoodsMode;
	}
	public String getSendOrderID() {
		return sendOrderID;
	}
	public void setSendOrderID(String sendOrderID) {
		this.sendOrderID = sendOrderID;
	}
	public float getTotalBarginPrice() {
		return totalBarginPrice;
	}
	public void setTotalBarginPrice(float totalBarginPrice) {
		this.totalBarginPrice = totalBarginPrice;
	}
	public float getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(float totalFee) {
		this.totalFee = totalFee;
	}
}
