package com.wofu.ecommerce.taobao;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class RefundDetail extends BusinessObject {
	
	private long sub_order_id;
	private boolean is_return_goods;
	private Date refund_create_time;
	private int refund_status;
	private double refund_fee;
	private double pay_sup_fee;
	private String refund_reason;
	private String refund_desc;
	private String supplier_nick;
	private String distributor_nick;
	private Date modified;
	private String attribute="";
	
	public long getSub_order_id() {
		return sub_order_id;
	}
	public void setSub_order_id(long sub_order_id) {
		this.sub_order_id = sub_order_id;
	}
	public boolean isIs_return_goods() {
		return is_return_goods;
	}
	public void setIs_return_goods(boolean is_return_goods) {
		this.is_return_goods = is_return_goods;
	}
	public Date getRefund_create_time() {
		return refund_create_time;
	}
	public void setRefund_create_time(Date refund_create_time) {
		this.refund_create_time = refund_create_time;
	}
	public int getRefund_status() {
		return refund_status;
	}
	public void setRefund_status(int refund_status) {
		this.refund_status = refund_status;
	}
	public double getRefund_fee() {
		return refund_fee;
	}
	public void setRefund_fee(double refund_fee) {
		this.refund_fee = refund_fee;
	}
	public double getPay_sup_fee() {
		return pay_sup_fee;
	}
	public void setPay_sup_fee(double pay_sup_fee) {
		this.pay_sup_fee = pay_sup_fee;
	}
	public String getRefund_reason() {
		return refund_reason;
	}
	public void setRefund_reason(String refund_reason) {
		this.refund_reason = refund_reason;
	}
	public String getRefund_desc() {
		return refund_desc;
	}
	public void setRefund_desc(String refund_desc) {
		this.refund_desc = refund_desc;
	}
	public String getSupplier_nick() {
		return supplier_nick;
	}
	public void setSupplier_nick(String supplier_nick) {
		this.supplier_nick = supplier_nick;
	}
	public String getDistributor_nick() {
		return distributor_nick;
	}
	public void setDistributor_nick(String distributor_nick) {
		this.distributor_nick = distributor_nick;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	

}
