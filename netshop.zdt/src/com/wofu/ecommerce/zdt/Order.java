package com.wofu.ecommerce.zdt;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Order extends BusinessObject{
	private float payment;//订单支付金额
	private String buyer_memo;//买家备注
	private String status;//订单状态
	private String receiver_zip;//买家邮编
	private String receiver_city;//市
	private String receiver_address;//地址
	private String seller_memo;//卖家备注
	private String adjust_fee;//优惠金额
	private String type;//支付方式 
	private String receiver_mobile;//手机
	private float post_fee;//邮费
	private float total_fee=0.0f;//商品总金额
	private String receiver_phone;//电话
	private String order_no;//订单号
	private Date modified;//修改时间
	private String receiver_name;//收件人
	private String buyer_nick;//买家昵称
	private String receiver_district;//收货区
	private String buyer_email;//电子邮件
	private Date created;//订单创建时间
	private String receiver_state;//收货人省份
	private DataRelation orderItems = new DataRelation("orderItems","com.wofu.ecommerce.zdt.OrderItem");
	public float getPayment() {
		return payment;
	}
	public void setPayment(float payment) {
		this.payment = payment;
	}
	public String getBuyer_memo() {
		return buyer_memo;
	}
	public void setBuyer_memo(String buyer_memo) {
		this.buyer_memo = buyer_memo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReceiver_zip() {
		return receiver_zip;
	}
	public void setReceiver_zip(String receiver_zip) {
		this.receiver_zip = receiver_zip;
	}
	public String getReceiver_city() {
		return receiver_city;
	}
	public void setReceiver_city(String receiver_city) {
		this.receiver_city = receiver_city;
	}
	public String getReceiver_address() {
		return receiver_address;
	}
	public void setReceiver_address(String receiver_address) {
		this.receiver_address = receiver_address;
	}
	public String getSeller_memo() {
		return seller_memo;
	}
	public void setSeller_memo(String seller_memo) {
		this.seller_memo = seller_memo;
	}
	public String getAdjust_fee() {
		return adjust_fee;
	}
	public void setAdjust_fee(String adjust_fee) {
		this.adjust_fee = adjust_fee;
	}
	public DataRelation getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(DataRelation orderItems) {
		this.orderItems = orderItems;
	}
	public String getReceiver_mobile() {
		return receiver_mobile;
	}
	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}
	public float getPost_fee() {
		return post_fee;
	}
	public void setPost_fee(float post_fee) {
		this.post_fee = post_fee;
	}
	public String getReceiver_phone() {
		return receiver_phone;
	}
	public void setReceiver_phone(String receiver_phone) {
		this.receiver_phone = receiver_phone;
	}
	public String getOrder_no() {
		return order_no;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public String getReceiver_name() {
		return receiver_name;
	}
	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}
	public String getBuyer_nick() {
		return buyer_nick;
	}
	public void setBuyer_nick(String buyer_nick) {
		this.buyer_nick = buyer_nick;
	}
	public String getReceiver_district() {
		return receiver_district;
	}
	public void setReceiver_district(String receiver_district) {
		this.receiver_district = receiver_district;
	}
	public String getBuyer_email() {
		return buyer_email;
	}
	public void setBuyer_email(String buyer_email) {
		this.buyer_email = buyer_email;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getReceiver_state() {
		return receiver_state;
	}
	public void setReceiver_state(String receiver_state) {
		this.receiver_state = receiver_state;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public float getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(float total_fee) {
		this.total_fee = total_fee;
	}
	
	
	
	
	
	
}
