package com.wofu.ecommerce.taobao;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
/**
 * 
 * 经销订单封装类
 *
 */
public class Dealer_order extends BusinessObject{
	private String alipay_no;
	private Date applied_time;
	private String applier_nick;
	private Date audit_time_supplier;
	private long dealer_order_id;
	private int delivered_quantity_count;
	private String logistics_fee;
	private String logistics_type;
	private Date modified_time;
	private String order_status;
	private Date pay_time;
	private String pay_type;
	private int quantity_count;
	private Receiver Receiverinfo=new Receiver();
	private String close_reason;
	public Receiver getReceiverinfo() {
		return Receiverinfo;
	}
	public void setReceiverinfo(Receiver receiverinfo) {
		Receiverinfo = receiverinfo;
	}
	private String supplier_nick;
	private String total_price;
	private DataRelation dealer_order_details = new DataRelation("dealer_order_detail","com.wofu.ecommerce.taobao.Dealer_order_detail");
	public String getAlipay_no() {
		return alipay_no;
	}
	public void setAlipay_no(String alipay_no) {
		this.alipay_no = alipay_no;
	}
	public Date getApplied_time() {
		return applied_time;
	}
	public void setApplied_time(Date applied_time) {
		this.applied_time = applied_time;
	}
	public String getApplier_nick() {
		return applier_nick;
	}
	public void setApplier_nick(String applier_nick) {
		this.applier_nick = applier_nick;
	}
	public Date getAudit_time_supplier() {
		return audit_time_supplier;
	}
	public void setAudit_time_supplier(Date audit_time_supplier) {
		this.audit_time_supplier = audit_time_supplier;
	}
	public long getDealer_order_id() {
		return dealer_order_id;
	}
	public void setDealer_order_id(long dealer_order_id) {
		this.dealer_order_id = dealer_order_id;
	}
	public int getDelivered_quantity_count() {
		return delivered_quantity_count;
	}
	public void setDelivered_quantity_count(int delivered_quantity_count) {
		this.delivered_quantity_count = delivered_quantity_count;
	}
	public String getLogistics_fee() {
		return logistics_fee;
	}
	public void setLogistics_fee(String logistics_fee) {
		this.logistics_fee = logistics_fee;
	}
	public String getLogistics_type() {
		return logistics_type;
	}
	public void setLogistics_type(String logistics_type) {
		this.logistics_type = logistics_type;
	}
	public Date getModified_time() {
		return modified_time;
	}
	public void setModified_time(Date modified_time) {
		this.modified_time = modified_time;
	}
	public String getOrder_status() {
		return order_status;
	}
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}
	public Date getPay_time() {
		return pay_time;
	}
	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public int getQuantity_count() {
		return quantity_count;
	}
	public void setQuantity_count(int quantity_count) {
		this.quantity_count = quantity_count;
	}

	public String getSupplier_nick() {
		return supplier_nick;
	}
	public void setSupplier_nick(String supplier_nick) {
		this.supplier_nick = supplier_nick;
	}
	public String getTotal_price() {
		return total_price;
	}
	public void setTotal_price(String total_price) {
		this.total_price = total_price;
	}
	public DataRelation getDealer_order_details() {
		return dealer_order_details;
	}
	public void setDealer_order_details(DataRelation dealer_order_details) {
		this.dealer_order_details = dealer_order_details;
	}
	public String getClose_reason() {
		return close_reason;
	}
	public void setClose_reason(String close_reason) {
		this.close_reason = close_reason;
	}
	
	
}
