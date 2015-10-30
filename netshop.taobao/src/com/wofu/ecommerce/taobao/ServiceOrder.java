package com.wofu.ecommerce.taobao;

import com.wofu.base.util.BusinessObject;

public class ServiceOrder extends BusinessObject{
	private String buyer_nick;
	private long item_oid;
	private int num;
	private long oid;
	private String payment;
	private String pic_path;
	private float price;
	private String seller_nick;
	private String service_detail_url;
	private long service_id;
	private String title;
	private String tmser_spu_code;
	private String total_fee;
	public String getBuyer_nick() {
		return buyer_nick;
	}
	public void setBuyer_nick(String buyer_nick) {
		this.buyer_nick = buyer_nick;
	}
	public long getItem_oid() {
		return item_oid;
	}
	public void setItem_oid(long item_oid) {
		this.item_oid = item_oid;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}
	public String getPayment() {
		return payment;
	}
	public void setPayment(String payment) {
		this.payment = payment;
	}
	public String getPic_path() {
		return pic_path;
	}
	public void setPic_path(String pic_path) {
		this.pic_path = pic_path;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getSeller_nick() {
		return seller_nick;
	}
	public void setSeller_nick(String seller_nick) {
		this.seller_nick = seller_nick;
	}
	public String getService_detail_url() {
		return service_detail_url;
	}
	public void setService_detail_url(String service_detail_url) {
		this.service_detail_url = service_detail_url;
	}
	public long getService_id() {
		return service_id;
	}
	public void setService_id(long service_id) {
		this.service_id = service_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTmser_spu_code() {
		return tmser_spu_code;
	}
	public void setTmser_spu_code(String tmser_spu_code) {
		this.tmser_spu_code = tmser_spu_code;
	}
	public String getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
	
	
}
