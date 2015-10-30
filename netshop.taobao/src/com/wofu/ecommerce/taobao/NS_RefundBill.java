package com.wofu.ecommerce.taobao;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class NS_RefundBill extends BusinessObject {
	private String sheetid;
	private String refund_id;
	private String refund_version;
	private String refund_phase;
	private String refund_type;
	private String operation_constraint;
	private String trade_status;
	private double refund_fee;
	private String reason;
	private double actual_refund_fee;
	private Date created;
	private Date current_phase_timeout;
	private String alipay_no;
	private String buyer_nick;
	private String seller_nick;
	private String shopid;
	private String linkman;
	private String linktele;
	private String mobile;
	private String address;
	private String buyeralipayno;
	private String tid;
	private String oid;
	private String cs_status;
	private String attribute;
	private String status;
	private String tags;
	private Date modified;
	private String description;
	
	private DataRelation item_list=new DataRelation("item_list","com.wofu.ecommerce.taobao.NS_RefundBillItem");
	
	public String getSheetid() {
		return sheetid;	
	}
	public void setSheetid(String sheetid) {
		this.sheetid = sheetid;
	}
	public String getRefund_id() {
		return refund_id;	
	}
	public void setRefund_id(String refund_id) {
		this.refund_id = refund_id;
	}
	public String getRefund_version() {
		return refund_version;	
	}
	public void setRefund_version(String refund_version) {
		this.refund_version = refund_version;
	}
	public String getRefund_phase() {
		return refund_phase;	
	}
	public void setRefund_phase(String refund_phase) {
		this.refund_phase = refund_phase;
	}
	public String getRefund_type() {
		return refund_type;	
	}
	public void setRefund_type(String refund_type) {
		this.refund_type = refund_type;
	}
	public String getOperation_constraint() {
		return operation_constraint;	
	}
	public void setOperation_constraint(String operation_constraint) {
		this.operation_constraint = operation_constraint;
	}
	public String getTrade_status() {
		return trade_status;	
	}
	public void setTrade_status(String trade_status) {
		this.trade_status = trade_status;
	}
	public double getRefund_fee() {
		return refund_fee;	
	}
	public void setRefund_fee(double refund_fee) {
		this.refund_fee = refund_fee;
	}
	public String getReason() {
		return reason;	
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public double getActual_refund_fee() {
		return actual_refund_fee;	
	}
	public void setActual_refund_fee(double actual_refund_fee) {
		this.actual_refund_fee = actual_refund_fee;
	}
	public Date getCreated() {
		return created;	
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getCurrent_phase_timeout() {
		return current_phase_timeout;	
	}
	public void setCurrent_phase_timeout(Date current_phase_timeout) {
		this.current_phase_timeout = current_phase_timeout;
	}
	public String getAlipay_no() {
		return alipay_no;	
	}
	public void setAlipay_no(String alipay_no) {
		this.alipay_no = alipay_no;
	}
	public String getBuyer_nick() {
		return buyer_nick;	
	}
	public void setBuyer_nick(String buyer_nick) {
		this.buyer_nick = buyer_nick;
	}
	public String getSeller_nick() {
		return seller_nick;	
	}
	public void setSeller_nick(String seller_nick) {
		this.seller_nick = seller_nick;
	}
	public String getShopid() {
		return shopid;	
	}
	public void setShopid(String shopid) {
		this.shopid = shopid;
	}
	public String getLinkman() {
		return linkman;	
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	public String getLinktele() {
		return linktele;	
	}
	public void setLinktele(String linktele) {
		this.linktele = linktele;
	}
	public String getMobile() {
		return mobile;	
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAddress() {
		return address;	
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBuyeralipayno() {
		return buyeralipayno;	
	}
	public void setBuyeralipayno(String buyeralipayno) {
		this.buyeralipayno = buyeralipayno;
	}
	public String getTid() {
		return tid;	
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getOid() {
		return oid;	
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getCs_status() {
		return cs_status;	
	}
	public void setCs_status(String cs_status) {
		this.cs_status = cs_status;
	}
	public String getAttribute() {
		return attribute;	
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getStatus() {
		return status;	
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public Date getModified() {
		return modified;	
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public DataRelation getItem_list() {
		return item_list;
	}
	public void setItem_list(DataRelation item_list) {
		this.item_list = item_list;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
