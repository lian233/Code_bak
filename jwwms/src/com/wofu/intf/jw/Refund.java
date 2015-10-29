package com.wofu.intf.jw;

import com.wofu.base.util.BusinessObject;

public class Refund extends BusinessObject{
	private String refundId ;//�˻�������
	private String orderCode ;//���۶�����
	private String oid ;//�Ӷ�����
	private String refundBarCode ;//�˻���������
	private String goodsId ;//����
	private String num ;//�˻�����
	private String companyName ;//������˾
	private String expressNo ;//��ݵ���
	private String hasGoodReturn ;//�Ƿ��˻�(true/false)
	private String refundFee ;//�˿���
	private String created ;//�����˿�ʱ��
	private String reason ;//�˿�ԭ��
	public String getRefundId() {
		return refundId;
	}
	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getRefundBarCode() {
		return refundBarCode;
	}
	public void setRefundBarCode(String refundBarCode) {
		this.refundBarCode = refundBarCode;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getExpressNo() {
		return expressNo;
	}
	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}
	public String getHasGoodReturn() {
		return hasGoodReturn;
	}
	public void setHasGoodReturn(String hasGoodReturn) {
		this.hasGoodReturn = hasGoodReturn;
	}
	public String getRefundFee() {
		return refundFee;
	}
	public void setRefundFee(String refundFee) {
		this.refundFee = refundFee;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	

}
