package com.wofu.ecommerce.ecshop;

import com.wofu.base.util.BusinessObject;

/**
 * 
 * 订单中的商品
 *
 */
public class OrderItem extends BusinessObject{
	private String goods_id="";//商品标识
	private String goods_name="";// 商品名称
	private String goods_sn="";//货号
	private String goods_attr="";//商品属性
	private float goods_price=0.0f;//商品的本店售价
	private float goods_number=0.0f;//数量
	private float market_price=0;//市场价
	private String goods_attr_id="";//属性id 
	private String product_id="";   //产品id
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
