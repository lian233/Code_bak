package com.wofu.ecommerce.yz;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Order extends BusinessObject{
	private String status;//����״̬
	private String shipping_type;//������ʽ 
	private float post_fee;//�ʷ�
	private float payment;//ʵ�����
	private String trade_memo;//���ұ�ע
	private String type;//�������� 
	private String receiver_mobile;//�ֻ�
	private float total_fee=0.0f;//��Ʒ�ܽ��
	private float discount_fee=0.0f;//�Żݽ��   ����Żݽ�������ϸ���Żݽ��
	private String receiver_phone;//�绰
	private String receiver_city;//�绰
	private String tid;//������
	private Date update_time;//�޸�ʱ��
	private Date pay_time;//֧��ʱ��
	private String receiver_name;//�ռ���
	private String buyer_nick;//����ǳ�
	private String receiver_district;//�ջ���
	private String receiver_address;//�ջ���ϸ��ַ
	private String receiver_zip;//�ջ����ʱ�
	private Date created;//��������ʱ��
	private String receiver_state;//�ջ���ʡ��
	private String pay_type;//֧������
	private String buyer_message;//�������
	private String weixin_user_id;//΢��ID
	private String outer_tid;//�ⲿ���ױ��
	private DataRelation orders = new DataRelation("orders","com.wofu.ecommerce.yz.OrderItem");
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getShipping_type() {
		return shipping_type;
	}
	public void setShipping_type(String shipping_type) {
		this.shipping_type = shipping_type;
	}
	public float getPost_fee() {
		return post_fee;
	}
	public void setPost_fee(float post_fee) {
		this.post_fee = post_fee;
	}
	public float getPayment() {
		return payment;
	}
	public void setPayment(float payment) {
		this.payment = payment;
	}
	
	public String getTrade_memo() {
		return trade_memo;
	}
	public void setTrade_memo(String trade_memo) {
		this.trade_memo = trade_memo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getReceiver_mobile() {
		return receiver_mobile;
	}
	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}
	public float getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(float total_fee) {
		this.total_fee = total_fee;
	}
	public float getDiscount_fee() {
		return discount_fee;
	}
	public void setDiscount_fee(float discount_fee) {
		this.discount_fee = discount_fee;
	}
	public String getReceiver_phone() {
		return receiver_phone;
	}
	public void setReceiver_phone(String receiver_phone) {
		this.receiver_phone = receiver_phone;
	}
	public String getReceiver_city() {
		return receiver_city;
	}
	public void setReceiver_city(String receiver_city) {
		this.receiver_city = receiver_city;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public Date getPay_time() {
		return pay_time;
	}
	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
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
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public String getBuyer_message() {
		return buyer_message;
	}
	public void setBuyer_message(String buyer_message) {
		this.buyer_message = buyer_message;
	}
	public String getWeixin_user_id() {
		return weixin_user_id;
	}
	public void setWeixin_user_id(String weixin_user_id) {
		this.weixin_user_id = weixin_user_id;
	}
	public String getOuter_tid() {
		return outer_tid;
	}
	public void setOuter_tid(String outer_tid) {
		this.outer_tid = outer_tid;
	}
	public DataRelation getOrders() {
		return orders;
	}
	public void setOrders(DataRelation orders) {
		this.orders = orders;
	}
	
	
	
	
	
	
	
}
