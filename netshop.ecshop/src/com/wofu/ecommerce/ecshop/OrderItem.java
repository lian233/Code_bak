package com.wofu.ecommerce.ecshop;

import com.wofu.base.util.BusinessObject;

/**
 * 
 * �����е���Ʒ
 *
 */
public class OrderItem extends BusinessObject{
	private String goods_id="";//��Ʒ��ʶ
	private String goods_name="";// ��Ʒ����
	private String goods_sn="";//����
	private String goods_attr="";//��Ʒ����
	private float goods_price=0.0f;//��Ʒ�ı����ۼ�
	private float goods_number=0.0f;//����
	private float market_price=0;//�г���
	private String goods_attr_id="";//����id 
	private String product_id="";   //��Ʒid
	private String product_sn="";  //sku
	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public String getGoods_sn() {
		return goods_sn;
	}
	public void setGoods_sn(String goods_sn) {
		this.goods_sn = goods_sn;
	}
	public String getGoods_attr() {
		return goods_attr;
	}
	public void setGoods_attr(String goods_attr) {
		this.goods_attr = goods_attr;
	}
	public float getGoods_price() {
		return goods_price;
	}
	public void setGoods_price(float goods_price) {
		this.goods_price = goods_price;
	}
	public float getGoods_number() {
		return goods_number;
	}
	public void setGoods_number(float goods_number) {
		this.goods_number = goods_number;
	}
	public float getMarket_price() {
		return market_price;
	}
	public void setMarket_price(float market_price) {
		this.market_price = market_price;
	}
	public String getGoods_attr_id() {
		return goods_attr_id;
	}
	public void setGoods_attr_id(String goods_attr_id) {
		this.goods_attr_id = goods_attr_id;
	}
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public String getProduct_sn() {
		return product_sn;
	}
	public void setProduct_sn(String product_sn) {
		this.product_sn = product_sn;
	}

}
