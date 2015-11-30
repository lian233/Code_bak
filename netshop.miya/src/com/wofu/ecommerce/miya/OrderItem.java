package com.wofu.ecommerce.miya;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class OrderItem extends BusinessObject {

	private String item_id;//订单商品流水号
	private String sku_id;//商品SKU
	private String item_name;//商品名称
	private String item_code;//商品货号
	private String barcode;//商品条码
	private String sku_item_size;//商品尺码, 没有尺码默认"SINGLE"
	private int item_total;//购买数量
	private String sale_price;//商品价格（不包含运费）
	private String ship_price;//商品邮费(均摊到每个商品)
	private String pay_price;//商品应付金额（包含运费）
	private String coupon_price;//商品优惠券金额
	private String seller_discount;//商品优惠金额（不包含运费）
	
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getSku_id() {
		return sku_id;
	}
	public void setSku_id(String sku_id) {
		this.sku_id = sku_id;
	}
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public String getItem_code() {
		return item_code;
	}
	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getSku_item_size() {
		return sku_item_size;
	}
	public void setSku_item_size(String sku_item_size) {
		this.sku_item_size = sku_item_size;
	}

	public String getSale_price() {
		return sale_price;
	}
	public void setSale_price(String sale_price) {
		this.sale_price = sale_price;
	}
	public String getShip_price() {
		return ship_price;
	}
	public void setShip_price(String ship_price) {
		this.ship_price = ship_price;
	}
	public String getPay_price() {
		return pay_price;
	}
	public void setPay_price(String pay_price) {
		this.pay_price = pay_price;
	}
	public String getCoupon_price() {
		return coupon_price;
	}
	public void setCoupon_price(String coupon_price) {
		this.coupon_price = coupon_price;
	}
	public String getSeller_discount() {
		return seller_discount;
	}
	public void setSeller_discount(String seller_discount) {
		this.seller_discount = seller_discount;
	}
	public int getItem_total() {
		return item_total;
	}
	public void setItem_total(int item_total) {
		this.item_total = item_total;
	}
	
}
