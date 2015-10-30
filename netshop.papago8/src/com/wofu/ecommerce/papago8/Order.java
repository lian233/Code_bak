package com.wofu.ecommerce.papago8;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * 订单类
 *
 */
public class Order extends BusinessObject{
	
	private String tid = "";//订单编号
	private String status = "";//订单状态
	private String type = "";//"fix"线上付款   ”cod“货到付款
	private Date created; //订单创建时间
	private float total_money = 0f ;//订单商品总金额
	private float discount_fee = 0f ;//优惠金额
	private String  buyer_message ="";//买家留言
	private String  seller_message ="";//买家留言
	private float post_fee=0.0f;//运费
	private Date pay_time=new Date();//支付时间 
	private Date modified=new Date();//修改时间
	private String receiver_name="";//买家昵称
	private String receiver_state="";//收货省份
	private String receiver_city="";//收货市
	private String receiver_district="";//收货区
	private String receiver_address="";//收货街
	private String postcode="";//邮编
	private String receiver_phone="";//移动电话
	private String receiver_mobile="";//移动电话
	private String receiver_zip="";//移动电话
	private String nickname="";//买家帐号-写buyernick
	private DataRelation orderItemList =new DataRelation("order","com.wofu.ecommerce.papago8.OrderItem");
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public float getTotal_money() {
		return total_money;
	}
	public void setTotal_money(float total_price) {
		this.total_money = total_price;
	}
	public float getDiscount_fee() {
		return discount_fee;
	}
	public void setDiscount_fee(float discount_fee) {
		this.discount_fee = discount_fee;
	}
	public String getBuyer_message() {
		return buyer_message;
	}
	public void setBuyer_message(String buyer_message) {
		this.buyer_message = buyer_message;
	}
	public String getSeller_message() {
		return seller_message;
	}
	public void setSeller_message(String seller_message) {
		this.seller_message = seller_message;
	}
	public float getPost_fee() {
		return post_fee;
	}
	public void setPost_fee(float post_fee) {
		this.post_fee = post_fee;
	}
	public Date getPay_time() {
		return pay_time;
	}
	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
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
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
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
	public String getReceiver_zip() {
		return receiver_zip;
	}
	public void setReceiver_zip(String receiver_zip) {
		this.receiver_zip = receiver_zip;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public DataRelation getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(DataRelation orderItemList) {
		this.orderItemList = orderItemList;
	}

	
}
