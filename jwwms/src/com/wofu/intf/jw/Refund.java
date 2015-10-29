package com.wofu.intf.jw;

import com.wofu.base.util.BusinessObject;

public class Refund extends BusinessObject{
	private String refundId ;//退货订单号
	private String orderCode ;//销售订单号
	private String oid ;//子订单号
	private String refundBarCode ;//退货海关条码
	private String goodsId ;//货号
	private String num ;//退货数量
	private String companyName ;//物流公司
	private String expressNo ;//快递单号
	private String hasGoodReturn ;//是否退货(true/false)
	private String refundFee ;//退款金额
	private String created ;//申请退款时间
	private String reason ;//退款原因
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
