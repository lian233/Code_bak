package com.wofu.ecommerce.taobao;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class NS_ReturnBill extends BusinessObject {
	private String sheetid;
	private String refund_id;
	private String refund_version;
	private String status;
	private String refund_phase;
	private String reason;
	private String company_name;
	private String sid;
	private Date created;
	private String tid;
	private String oid;
	private String buyer_nick;
	private String seller_nick;
	private String shopid;
	private String linkman;
	private String linktele;
	private String mobile;
	private String address;
	private String buyeralipayno;
	private String operation_log;
	private String tags;
	private String description;
	
	private DataRelation item_list=new DataRelation("item_list","com.wofu.ecommerce.taobao.NS_ReturnBillItem");
	
	private Date modified;

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
	public String getStatus() {
		return status;	
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRefund_phase() {
		return refund_phase;	
	}
	public void setRefund_phase(String refund_phase) {
		this.refund_phase = refund_phase;
	}
	public String getReason() {
		return reason;	
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getCompany_name() {
		return company_name;	
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getSid() {
		return sid;	
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public Date getCreated() {
		return created;	
	}
	public void setCreated(Date created) {
		this.created = created;
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
	public String getOperation_log() {
		return operation_log;	
	}
	public void setOperation_log(String operation_log) {
		this.operation_log = operation_log;
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
	
}
