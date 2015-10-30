package com.wofu.ecommerce.taobao.fenxiao;

import com.wofu.base.util.BusinessObject;

public class PromotionDetail extends BusinessObject {
	private String id;
	private String promotion_name;
	private double discount_fee;
	private String gift_item_name;
	private String gift_item_id;
	private String gift_item_num;
	private String promotion_desc;
	private String promotion_id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPromotion_name() {
		return promotion_name;
	}
	public void setPromotion_name(String promotion_name) {
		this.promotion_name = promotion_name;
	}
	public double getDiscount_fee() {
		return discount_fee;
	}
	public void setDiscount_fee(double discount_fee) {
		this.discount_fee = discount_fee;
	}
	public String getGift_item_name() {
		return gift_item_name;
	}
	public void setGift_item_name(String gift_item_name) {
		this.gift_item_name = gift_item_name;
	}
	public String getGift_item_id() {
		return gift_item_id;
	}
	public void setGift_item_id(String gift_item_id) {
		this.gift_item_id = gift_item_id;
	}
	public String getGift_item_num() {
		return gift_item_num;
	}
	public void setGift_item_num(String gift_item_num) {
		this.gift_item_num = gift_item_num;
	}
	public String getPromotion_desc() {
		return promotion_desc;
	}
	public void setPromotion_desc(String promotion_desc) {
		this.promotion_desc = promotion_desc;
	}
	public String getPromotion_id() {
		return promotion_id;
	}
	public void setPromotion_id(String promotion_id) {
		this.promotion_id = promotion_id;
	}
	
	
	
}
