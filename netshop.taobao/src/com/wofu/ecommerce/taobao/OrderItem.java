package com.wofu.ecommerce.taobao;


import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class OrderItem extends BusinessObject {
	private int serialid;
	private String oid;
	private String sku_id;
	private String item_meal_name;  //套餐名称
	private String item_title;			//宝贝标题
	private String seller_nick;			//卖家昵称
	private String buyer_nick;			//买家昵称
	private String outer_iid;			//商家外部编码
	private String outer_sku_id;		//商家SKU编码
	private double total_fee;			//应付金额
	private double payment;				//子订单实付金额
	private double discount_fee;		//子订单折扣金额
	private double adjust_fee;
	private String status;
	private String sku_properties_name;
	private int num;	
	private double price;
	private String pic_path;
	private boolean buyer_rate;
	private boolean seller_rate;
	private Date modified;			//订单修改时间
	private String num_iid;			//商品ID
	private int cid;				//类目ID
	private boolean is_oversold;	//是否超卖
	
	
	public boolean isIs_oversold() {
		return is_oversold;
	}
	public void setIs_oversold(boolean is_oversold) {
		this.is_oversold = is_oversold;
	}
	public double getAdjust_fee() {
		return adjust_fee;
	}
	public void setAdjust_fee(double adjust_fee) {
		this.adjust_fee = adjust_fee;
	}
	public String getBuyer_nick() {
		return buyer_nick;
	}
	public void setBuyer_nick(String buyer_nick) {
		this.buyer_nick = buyer_nick;
	}
	public boolean isBuyer_rate() {
		return buyer_rate;
	}
	public void setBuyer_rate(boolean buyer_rate) {
		this.buyer_rate = buyer_rate;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public double getDiscount_fee() {
		return discount_fee;
	}
	public void setDiscount_fee(double discount_fee) {
		this.discount_fee = discount_fee;
	}
	public String getItem_meal_name() {
		return item_meal_name;
	}
	public void setItem_meal_name(String item_meal_name) {
		this.item_meal_name = item_meal_name;
	}
	public String getItem_title() {
		return item_title;
	}
	public void setItem_title(String item_title) {
		this.item_title = item_title;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getNum_iid() {
		return num_iid;
	}
	public void setNum_iid(String num_iid) {
		this.num_iid = num_iid;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getOuter_iid() {
		return outer_iid;
	}
	public void setOuter_iid(String outer_iid) {
		this.outer_iid = outer_iid;
	}
	public String getOuter_sku_id() {
		return outer_sku_id;
	}
	public void setOuter_sku_id(String outer_sku_id) {
		this.outer_sku_id = outer_sku_id;
	}
	public double getPayment() {
		return payment;
	}
	public void setPayment(double payment) {
		this.payment = payment;
	}
	public String getPic_path() {
		return pic_path;
	}
	public void setPic_path(String pic_path) {
		this.pic_path = pic_path;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getSeller_nick() {
		return seller_nick;
	}
	public void setSeller_nick(String seller_nick) {
		this.seller_nick = seller_nick;
	}
	public boolean isSeller_rate() {
		return seller_rate;
	}
	public void setSeller_rate(boolean seller_rate) {
		this.seller_rate = seller_rate;
	}
	public String getSku_id() {
		return sku_id;
	}
	public void setSku_id(String sku_id) {
		this.sku_id = sku_id;
	}
	public String getSku_properties_name() {
		return sku_properties_name;
	}
	public void setSku_properties_name(String sku_properties_name) {
		this.sku_properties_name = sku_properties_name;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(double total_fee) {
		this.total_fee = total_fee;
	}
	public int getSerialid() {
		return serialid;
	}
	public void setSerialid(int serialid) {
		this.serialid = serialid;
	}

}
