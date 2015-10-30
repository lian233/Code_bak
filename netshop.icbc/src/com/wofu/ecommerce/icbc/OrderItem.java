package com.wofu.ecommerce.icbc;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * 订单中的商品
 *
 */
public class OrderItem extends BusinessObject{
	private String goods_id="";//商品id  --自增
	private String goods_name="";//商品名称
	private String goods_sn="";//货号
	private float goods_price=0.0f;//市场价 
	private String is_gift="";//网购订货数量
	private String parent_id="";//网购订货数量
	private String goods_number="";//网购订货数量
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
	public float getGoods_price() {
		return goods_price;
	}
	public void setGoods_price(float goods_price) {
		this.goods_price = goods_price;
	}
	public String getIs_gift() {
		return is_gift;
	}
	public void setIs_gift(String is_gift) {
		this.is_gift = is_gift;
	}
	public String getParent_id() {
		return parent_id;
	}
	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}
	public String getGoods_number() {
		return goods_number;
	}
	public void setGoods_number(String goods_number) {
		this.goods_number = goods_number;
	}
	
	
	

	
	
	
	
	
}
