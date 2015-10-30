package com.wofu.netshop.dangdang.fenxiao;

import java.util.ArrayList;
import java.util.Date;

import com.wofu.netshop.dangdang.fenxiao.OperateInfo;
/**
 * 
 * ������
 *
 */
public class Order {
	private String orderID = "";//�������
	private String orderState = "";//����״̬
	private String message = ""; //�������
	private String remark =""; //��ע
	private String label = "" ; //5��̶���־ 1��ɫ2��ɫ3��ɫ4��ɫ5��ɫ
	private Date lastModifyTime; //����޸�ʱ��
	
	private String buyerPayMode="";//���֧����ʽ
	private float totalFee = 0f ;//������Ʒ�ܽ��
	private float goodsMoney =0.0f;//�������̼�Ӧ�ս��
	private float deductAmount=0.0f;//����֧��������Żݽ��
	private float totalBarginPrice=0.0f;//�˿���Ϊ����֧���ֽ� 
	private float postage=0.0f;//���֧���ʷ� 
	private float giftCertMoney=0.0f;//���֧������� 
	private float giftCardMoney=0.0f; //���֧����Ʒ�����
	private float accountBalance=0.0f;//���֧���ʻ���� 
	//�ջ�����Ϣ
	private String consigneeName="";//�ջ�������
	private String consigneeAddr="";//�ջ���ַ
	private String consigneeAddrStte="";//�ջ�����
	private String consigneeAddrProvince="";//�ջ�ʡ��
	private String consigneeAddrCity="";//�ջ���
	private String consigneeAddrArea="";//�ջ���
	private String consigneePostcode="";//�ʱ�
	private String consigneeTel="";//�̶��绰
	private String consigneeMobileTel="";//�ƶ��绰
	private String sendGoodsMode="";//���ͷ�ʽ
	private String sendCompany="";//������˾
	private String sendOrderID="";//�ͻ�����
	private String dangdangWarehouseAddr="";//�̼���Ҫ�Ѱ������������ֿ��ַ

	//��Ʊ
	private String receiptName="";//��Ʊ̨ͷ
	private String receiptDetails="";//��Ʊ����
	private float receiptMoney=0.0f;//��Ʊ���
	private String isDangdangReceipt="";//1:�ɵ���������Ʊ 2�����ɵ���������Ʊ
	//������
	private String operCode="";//������
	private String operation="";//���������Ϣ

	private ArrayList<OrderItem> orderItemList=new ArrayList<OrderItem>();
	private ArrayList<OperateInfo> operateInfoList = new ArrayList<OperateInfo>() ;//���������б�
	//��Ʊ
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
