package com.wofu.intf.jw;

import com.wofu.base.util.BusinessObject;

public class detail extends BusinessObject{
	private String uuid="";
	private String orderCode="";//��������
	private String orderDetailCode="";//�Ӷ�������
	private String skuId="";//ƽ̨SKU����
	private String outerSkuId="";//�ⲿSku���
	private String num="";       //����
	private String title="";     //��Ʒ����
	private String price="";     //��Ʒ�۸�
	private String payment="0";   //��ʵ�ʽ��
	private String discountPrice="0";   //�Żݽ��
	private String totalPrice="0";   //Ӧ�����
	private String adjustPrice="0";   //�ֹ��������
	private String divideOrderPrice="";   //��̯֮���ʵ�����
	private String billPrice="0";   //��Ʊ���
	private String partMjzDiscoun="";   //�Żݷ�̯
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
