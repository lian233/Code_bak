package com.wofu.ecommerce.ylw;

import com.wofu.base.util.BusinessObject;

/**
 * 
 * �����е���Ʒ
 *
 */
public class OrderItem extends BusinessObject{
	private String productCode="";//��Ʒ��ʶ
	private String itemCode="";//��ҵ��Ʒ��ʶ����sku��
	private String productName="";//��Ʒ����
	private String itemType="";//0��Ʒ��1��Ʒ
	//private String specialAttribute="";//��ɫ����
	private float marketPrice=0.0f;//�г��� 
	private float unitPrice=0.0f;//�ɽ��� �������صļ۸�
	private float saleNum=0;//������������
	private int sendGoodsCount=0;//�����������������Ѿ�����ʱ���Ż᷵��  
	private float coupontotalMoney=0.00f;   //�Ż݄��ܽ��
	private float vouchertotalMoney=0.00f;  //�Żݵ��ܽ��
	private float payAmount=0.00f;          //ʵ���տ�
	private float transportFee=0.0f;        //�ʷ�
	private String orderLineStatus = "";//����״̬
	private String returnOrderFlag="";//�˻���־
	public String getReturnOrderFlag() {
		return returnOrderFlag;
	}
	public void setReturnOrderFlag(String returnOrderFlag) {
		this.returnOrderFlag = returnOrderFlag;
	}
	private String picPath="";
	public String getOrderLineStatus() {
		return orderLineStatus;
	}
	public void setOrderLineStatus(String orderLineStatus) {
		this.orderLineStatus = orderLineStatus;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public float getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(float marketPrice) {
		this.marketPrice = marketPrice;
	}

	public int getSendGoodsCount() {
		return sendGoodsCount;
	}
	public void setSendGoodsCount(int sendGoodsCount) {
		this.sendGoodsCount = sendGoodsCount;
	}

	public float getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public float getSaleNum() {
		return saleNum;
	}
	public void setSaleNum(float saleNum) {
		this.saleNum = saleNum;
	}
	public float getCoupontotalMoney() {
		return coupontotalMoney;
	}
	public void setCoupontotalMoney(float coupontotalMoney) {
		this.coupontotalMoney = coupontotalMoney;
	}
	public float getVouchertotalMoney() {
		return vouchertotalMoney;
	}
	public void setVouchertotalMoney(float vouchertotalMoney) {
		this.vouchertotalMoney = vouchertotalMoney;
	}
	public float getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(float payAmount) {
		this.payAmount = payAmount;
	}
	public float getTransportFee() {
		return transportFee;
	}
	public void setTransportFee(float transportFee) {
		this.transportFee = transportFee;
	}
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	
	
	
	
}
