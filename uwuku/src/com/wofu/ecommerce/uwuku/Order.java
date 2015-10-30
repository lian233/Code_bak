package com.wofu.ecommerce.uwuku;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class Order extends BusinessObject {

	private String tid;
	private String status;
	private String num_iid;
	private String title;
	private int num;
	private double price;
	private double discount_fee;
	
	private String discount_type;
	private double adjust_fee;
	private double post_fee;
	private double payment;
	private double total_fee;
	private String sku_id;
	private String properties_name;
	
	private Date modified;
	private String pic_path;
	private String buyer_id;
	private String seller_nick;
	private String buyer_nick;
	private int buyer_rate;
	private int seller_rate;
	private Date creat_time;
	
	private Date pay_time;
	private String seller_id;
	private String ali_trade_no;
	private String outer_id;
	private String sku_outer_id;
	private String receiver_name;
	private String receiver_phone;
	private String receiver_mobile;
	private String receiver_state;
	private String receiver_city;
	private String receiver_district;
	private String receiver_address;
	private String receiver_zip;
	
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNum_iid() {
		return num_iid;
	}
	public void setNum_iid(String num_iid) {
		this.num_iid = num_iid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getDiscount_fee() {
		return discount_fee;
	}
	public void setDiscount_fee(double discount_fee) {
		this.discount_fee = discount_fee;
	}
	public String getDiscount_type() {
		return discount_type;
	}
	public void setDiscount_type(String discount_type) {
		this.discount_type = discount_type;
	}
	public double getAdjust_fee() {
		return adjust_fee;
	}
	public void setAdjust_fee(double adjust_fee) {
		this.adjust_fee = adjust_fee;
	}
	public double getPost_fee() {
		return post_fee;
	}
	public void setPost_fee(double post_fee) {
		this.post_fee = post_fee;
	}
	public double getPayment() {
		return payment;
	}
	public void setPayment(double payment) {
		this.payment = payment;
	}
	public double getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(double total_fee) {
		this.total_fee = total_fee;
	}
	public String getSku_id() {
		return sku_id;
	}
	public void setSku_id(String sku_id) {
		this.sku_id = sku_id;
	}
	public String getProperties_name() {
		return properties_name;
	}
	public void setProperties_name(String properties_name) {
		this.properties_name = properties_name;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public String getPic_path() {
		return pic_path;
	}
	public void setPic_path(String pic_path) {
		this.pic_path = pic_path;
	}
	public String getBuyer_id() {
		return buyer_id;
	}
	public void setBuyer_id(String buyer_id) {
		this.buyer_id = buyer_id;
	}
	public String getSeller_nick() {
		return seller_nick;
	}
	public void setSeller_nick(String seller_nick) {
		this.seller_nick = seller_nick;
	}
	public String getBuyer_nick() {
		return buyer_nick;
	}
	public void setBuyer_nick(String buyer_nick) {
		this.buyer_nick = buyer_nick;
	}
	public int getBuyer_rate() {
		return buyer_rate;
	}
	public void setBuyer_rate(int buyer_rate) {
		this.buyer_rate = buyer_rate;
	}
	public int getSeller_rate() {
		return seller_rate;
	}
	public void setSeller_rate(int seller_rate) {
		this.seller_rate = seller_rate;
	}
	public Date getCreat_time() {
		return creat_time;
	}
	public void setCreat_time(Date creat_time) {
		this.creat_time = creat_time;
	}
	public Date getPay_time() {
		return pay_time;
	}
	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
	}
	public String getSeller_id() {
		return seller_id;
	}
	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}
	public String getAli_trade_no() {
		return ali_trade_no;
	}
	public void setAli_trade_no(String ali_trade_no) {
		this.ali_trade_no = ali_trade_no;
	}
	public String getOuter_id() {
		return outer_id;
	}
	public void setOuter_id(String outer_id) {
		this.outer_id = outer_id;
	}
	public String getSku_outer_id() {
		return sku_outer_id;
	}
	public void setSku_outer_id(String sku_outer_id) {
		this.sku_outer_id = sku_outer_id;
	}
	public String getReceiver_name() {
		return receiver_name;
	}
	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}
	public String getReceiver_phone() {
		return receiver_phone;
	}
	public void setReceiver_phone(String receiver_phone) {
		this.receiver_phone = receiver_phone;
	}
	public String getReceiver_mobile() {
		return receiver_mobile;
	}
	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}
	public String getReceiver_state() {
		return receiver_state;
	}
	public void setReceiver_state(String receiver_state) {
		this.receiver_state = receiver_state;
	}
	public String getReceiver_city() {
		return receiver_city;
	}
	public void setReceiver_city(String receiver_city) {
		this.receiver_city = receiver_city;
	}
	public String getReceiver_district() {
		return receiver_district;
	}
	public void setReceiver_district(String receiver_district) {
		this.receiver_district = receiver_district;
	}
	public String getReceiver_address() {
		return receiver_address;
	}
	public void setReceiver_address(String receiver_address) {
		this.receiver_address = receiver_address;
	}
	public String getReceiver_zip() {
		return receiver_zip;
	}
	public void setReceiver_zip(String receiver_zip) {
		this.receiver_zip = receiver_zip;
	}
	
	
}
