package com.wofu.ecommerce.taobao;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class SubPurchaseOrder extends BusinessObject {

	private long id;
	private String status;
	private double refund_fee;
	private long fenxiao_id;
	private long sku_id;
	private long tc_order_id;
	private long item_id;
	private double auction_price;
	private long num;
	private String title;
	private double price;
	private double total_fee;
	private double distributor_payment;
	private double buyer_payment;
	private double bill_fee;
	private long sc_item_id;
	private String tc_preferential_type;
	private double tc_discount_fee;
	private double tc_adjust_fee;
	private String old_sku_properties;
	private String item_outer_id;
	private String sku_outer_id;
	private String sku_properties;
	private String snapshot_url;
	private Date created;
	private String order_200_status;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getOrder_200_status() {
		return order_200_status;
	}
	public void setOrder_200_status(String order_200_status) {
		this.order_200_status = order_200_status;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getRefund_fee() {
		return refund_fee;
	}
	public void setRefund_fee(double refund_fee) {
		this.refund_fee = refund_fee;
	}
	public long getFenxiao_id() {
		return fenxiao_id;
	}
	public void setFenxiao_id(long fenxiao_id) {
		this.fenxiao_id = fenxiao_id;
	}
	public long getSku_id() {
		return sku_id;
	}
	public void setSku_id(long sku_id) {
		this.sku_id = sku_id;
	}
	public long getTc_order_id() {
		return tc_order_id;
	}
	public void setTc_order_id(long tc_order_id) {
		this.tc_order_id = tc_order_id;
	}
	public long getItem_id() {
		return item_id;
	}
	public void setItem_id(long item_id) {
		this.item_id = item_id;
	}
	public double getAuction_price() {
		return auction_price;
	}
	public void setAuction_price(double auction_price) {
		this.auction_price = auction_price;
	}
	public long getNum() {
		return num;
	}
	public void setNum(long num) {
		this.num = num;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(double total_fee) {
		this.total_fee = total_fee;
	}
	public double getDistributor_payment() {
		return distributor_payment;
	}
	public void setDistributor_payment(double distributor_payment) {
		this.distributor_payment = distributor_payment;
	}
	public double getBuyer_payment() {
		return buyer_payment;
	}
	public void setBuyer_payment(double buyer_payment) {
		this.buyer_payment = buyer_payment;
	}
	public double getBill_fee() {
		return bill_fee;
	}
	public void setBill_fee(double bill_fee) {
		this.bill_fee = bill_fee;
	}
	public long getSc_item_id() {
		return sc_item_id;
	}
	public void setSc_item_id(long sc_item_id) {
		this.sc_item_id = sc_item_id;
	}
	public String getTc_preferential_type() {
		return tc_preferential_type;
	}
	public void setTc_preferential_type(String tc_preferential_type) {
		this.tc_preferential_type = tc_preferential_type;
	}
	public double getTc_discount_fee() {
		return tc_discount_fee;
	}
	public void setTc_discount_fee(double tc_discount_fee) {
		this.tc_discount_fee = tc_discount_fee;
	}
	public double getTc_adjust_fee() {
		return tc_adjust_fee;
	}
	public void setTc_adjust_fee(double tc_adjust_fee) {
		this.tc_adjust_fee = tc_adjust_fee;
	}
	public String getOld_sku_properties() {
		return old_sku_properties;
	}
	public void setOld_sku_properties(String old_sku_properties) {
		this.old_sku_properties = old_sku_properties;
	}
	public String getItem_outer_id() {
		return item_outer_id;
	}
	public void setItem_outer_id(String item_outer_id) {
		this.item_outer_id = item_outer_id;
	}
	public String getSku_outer_id() {
		return sku_outer_id;
	}
	public void setSku_outer_id(String sku_outer_id) {
		this.sku_outer_id = sku_outer_id;
	}
	public String getSku_properties() {
		return sku_properties;
	}
	public void setSku_properties(String sku_properties) {
		this.sku_properties = sku_properties;
	}
	public String getSnapshot_url() {
		return snapshot_url;
	}
	public void setSnapshot_url(String snapshot_url) {
		this.snapshot_url = snapshot_url;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	
	
	
}
