package com.wofu.ecommerce.miya;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class OrderItem extends BusinessObject {

	private String item_id;//������Ʒ��ˮ��
	private String sku_id;//��ƷSKU
	private String item_name;//��Ʒ����
	private String item_code;//��Ʒ����
	private String barcode;//��Ʒ����
	private String sku_item_size;//��Ʒ����, û�г���Ĭ��"SINGLE"
	private int item_total;//��������
	private String sale_price;//��Ʒ�۸񣨲������˷ѣ�
	private String ship_price;//��Ʒ�ʷ�(��̯��ÿ����Ʒ)
	private String pay_price;//��ƷӦ���������˷ѣ�
	private String coupon_price;//��Ʒ�Ż�ȯ���
	private String seller_discount;//��Ʒ�Żݽ��������˷ѣ�
	
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
