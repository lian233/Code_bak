package com.wofu.netshop.mogujie.fenxiao;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Order extends BusinessObject{

	private String tid;//交易编号(交易ID)
	private String title;
	private Date created;
	private String tradetype;
	private String status;
	private String pay_status;
	private String ship_status;
	private float total_trade_fee;
	private float payed_fee;
	private String shipping_type;
	private String payment_tid;
	private String payment_type;
	private Date pay_time;
	private Date lastmodify;
	private Date end_time;
	private Date confirm_time;
	private String goods_discount_fee;
	private String orders_discount_fee;
	private String receiver_name;
	private String receiver_mobile;
	private String receiver_email;
	private String receiver_state;
	private String receiver_city;
	private String receiver_district;
	private String receiver_address;
	private String receiver_zip;
	private String receiver_phone;
	private String buyer_id;
	private String buyer_uname;
	private String buyer_memo;
	private String trade_memo;
	private String logistics_name;
	private boolean is_errortrade;//是否是异常订单
	private DataRelation orders = new DataRelation("orders","com.wofu.netshop.mogujie.fenxiao.OrderItem");
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getTradetype() {
		return tradetype;
	}
	public void setTradetype(String tradetype) {
		this.tradetype = tradetype;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPay_status() {
		return pay_status;
	}
	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}
	public String getShip_status() {
		return ship_status;
	}
	public void setShip_status(String ship_status) {
		this.ship_status = ship_status;
	}
	public float getTotal_trade_fee() {
		return total_trade_fee;
	}
	public void setTotal_trade_fee(float total_trade_fee) {
		this.total_trade_fee = total_trade_fee;
	}
	public float getPayed_fee() {
		return payed_fee;
	}
	public void setPayed_fee(float payed_fee) {
		this.payed_fee = payed_fee;
	}
	public String getShipping_type() {
		return shipping_type;
	}
	public void setShipping_type(String shipping_type) {
		this.shipping_type = shipping_type;
	}
	public String getPayment_tid() {
		return payment_tid;
	}
	public void setPayment_tid(String payment_tid) {
		this.payment_tid = payment_tid;
	}
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	public Date getPay_time() {
		return pay_time;
	}
	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
	}
	public Date getLastmodify() {
		return lastmodify;
	}
	public void setLastmodify(Date lastmodify) {
		this.lastmodify = lastmodify;
	}
	public Date getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}
	public Date getConfirm_time() {
		return confirm_time;
	}
	public void setConfirm_time(Date confirm_time) {
		this.confirm_time = confirm_time;
	}
	public String getGoods_discount_fee() {
		return goods_discount_fee;
	}
	public void setGoods_discount_fee(String goods_discount_fee) {
		this.goods_discount_fee = goods_discount_fee;
	}
	public String getOrders_discount_fee() {
		return orders_discount_fee;
	}
	public void setOrders_discount_fee(String orders_discount_fee) {
		this.orders_discount_fee = orders_discount_fee;
	}
	public String getReceiver_name() {
		return receiver_name;
	}
	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}
	public String getReceiver_mobile() {
		return receiver_mobile;
	}
	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}
	public String getReceiver_email() {
		return receiver_email;
	}
	public void setReceiver_email(String receiver_email) {
		this.receiver_email = receiver_email;
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
	public String getReceiver_phone() {
		return receiver_phone;
	}
	public void setReceiver_phone(String receiver_phone) {
		this.receiver_phone = receiver_phone;
	}
	public String getBuyer_id() {
		return buyer_id;
	}
	public void setBuyer_id(String buyer_id) {
		this.buyer_id = buyer_id;
	}
	public String getBuyer_memo() {
		return buyer_memo;
	}
	public void setBuyer_memo(String buyer_memo) {
		this.buyer_memo = buyer_memo;
	}
	public String getTrade_memo() {
		return trade_memo;
	}
	public void setTrade_memo(String trade_memo) {
		this.trade_memo = trade_memo;
	}
	public String getLogistics_name() {
		return logistics_name;
	}
	public void setLogistics_name(String logistics_name) {
		this.logistics_name = logistics_name;
	}
	public boolean isIs_errortrade() {
		return is_errortrade;
	}
	public void setIs_errortrade(boolean is_errortrade) {
		this.is_errortrade = is_errortrade;
	}
	public DataRelation getOrders() {
		return orders;
	}
	public void setOrders(DataRelation orders) {
		this.orders = orders;
	}
	public String getBuyer_uname() {
		return buyer_uname;
	}
	public void setBuyer_uname(String buyer_uname) {
		this.buyer_uname = buyer_uname;
	}
	
	
	

	
	
	
	
	
	
}
