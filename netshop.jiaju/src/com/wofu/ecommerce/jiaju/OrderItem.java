package com.wofu.ecommerce.jiaju;

import com.wofu.base.util.BusinessObject;

/*
 * 
 * �����е���Ʒ
 *
 */
public class OrderItem extends BusinessObject{
	private long amount = 0;			//��Ʒ����
	private float price = 0.0f;			//��Ʒ����
	private String outer_id = "";		//SKU
	private float price_fare = 0.0f;	//��Ʒ�˷�
	private String goods_id = "";		//��ƷID
	private String goods_name = "";		//��Ʒ����
	private float price_total = 0.0f;	//��Ʒ�ܷ���
	private String goods_logo = "";		//��ƷͼƬ
	
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public long getAmount() {
		return amount;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getPrice() {
		return price;
	}
	public void setOuter_id(String outer_id) {
		this.outer_id = outer_id;
	}
	public String getOuter_id() {
		return outer_id;
	}
	public void setPrice_fare(float price_fare) {
		this.price_fare = price_fare;
	}
	public float getPrice_fare() {
		return price_fare;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}
	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setPrice_total(float price_total) {
		this.price_total = price_total;
	}
	public float getPrice_total() {
		return price_total;
	}
	public void setGoods_logo(String goods_logo) {
		this.goods_logo = goods_logo;
	}
	public String getGoods_logo() {
		return goods_logo;
	}
}
