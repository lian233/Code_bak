package com.wofu.ecommerce.jumei;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class OrderItem extends BusinessObject {


	private String deal_hash_id;
	private String sku_no;
	private String upc_code;
	private String deal_short_name;
	private String attribute;
	private double deal_price;
	private int quantity;
	private double settlement_price;
	public String getDeal_hash_id() {
		return deal_hash_id;
	}
	public void setDeal_hash_id(String deal_hash_id) {
		this.deal_hash_id = deal_hash_id;
	}
	public String getSku_no() {
		return sku_no;
	}
	public void setSku_no(String sku_no) {
		this.sku_no = sku_no;
	}
	public String getUpc_code() {
		return upc_code;
	}
	public void setUpc_code(String upc_code) {
		this.upc_code = upc_code;
	}
	public String getDeal_short_name() {
		return deal_short_name;
	}
	public void setDeal_short_name(String deal_short_name) {
		this.deal_short_name = deal_short_name;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public double getDeal_price() {
		return deal_price;
	}
	public void setDeal_price(double deal_price) {
		this.deal_price = deal_price;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getSettlement_price() {
		return settlement_price;
	}
	public void setSettlement_price(double settlement_price) {
		this.settlement_price = settlement_price;
	}
	
	
}
