package com.wofu.ecommerce.icbc;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * �˻�������
 *
 */
public class RefundOrder extends BusinessObject{
	
	private String order_sn = "";//�������
	private String order_status = "";//����״̬
	private String shipping_status = "";//����״̬
	private String pay_status = "";//֧��״̬
	private String pay_name = "";//֧����ʽ
	private String user_name = "";//�ջ�������
	private String province_name = "";//ʡ
	private String city_name = "";//��
	private String district_name = "";//��
	private String address = "";//��ַ
	private String shipping_time = "";//�ͻ�ʱ��
	private String inv_payee = "";//��Ʊ̧ͷ  ����/��λ����   ��������ʾ������Ʊ
	private String total_fee = "";//Ӧ�����  �������֧�� ��֧�����   total_feeΪ0
	private String money_paid = "";//�Ѹ�����
	private String shipping_fee = "";//�˷�
	private String discount = "";//�Żݽ��
	private String bonus = "";//�Ż�ȯ/���  ���
	private long add_time; //��������ʱ��
	private long refund_time; //�˻�ʱ��
	private String pay_id; //֧����ʽ   1������  2����������
	private String  tel; //֧����ʽ   1������  2����������
	private float  refund_paid; //�˻����
	
	private DataRelation goods_list =new DataRelation("goods_list","com.wofu.ecommerce.lenovo.RefundOrderItem");

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getOrder_status() {
		return order_status;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public String getShipping_status() {
		return shipping_status;
	}

	public void setShipping_status(String shipping_status) {
		this.shipping_status = shipping_status;
	}

	public String getPay_status() {
		return pay_status;
	}

	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}

	public String getPay_name() {
		return pay_name;
	}

	public void setPay_name(String pay_name) {
		this.pay_name = pay_name;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getProvince_name() {
		return province_name;
	}

	public void setProvince_name(String province_name) {
		this.province_name = province_name;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getDistrict_name() {
		return district_name;
	}

	public void setDistrict_name(String district_name) {
		this.district_name = district_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getShipping_time() {
		return shipping_time;
	}

	public void setShipping_time(String shipping_time) {
		this.shipping_time = shipping_time;
	}

	public String getInv_payee() {
		return inv_payee;
	}

	public void setInv_payee(String inv_payee) {
		this.inv_payee = inv_payee;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getMoney_paid() {
		return money_paid;
	}

	public void setMoney_paid(String money_paid) {
		this.money_paid = money_paid;
	}

	public String getShipping_fee() {
		return shipping_fee;
	}

	public void setShipping_fee(String shipping_fee) {
		this.shipping_fee = shipping_fee;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getBonus() {
		return bonus;
	}

	public void setBonus(String bonus) {
		this.bonus = bonus;
	}

	public long getAdd_time() {
		return add_time;
	}

	public void setAdd_time(long add_time) {
		this.add_time = add_time;
	}

	public DataRelation getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(DataRelation goods_list) {
		this.goods_list = goods_list;
	}

	public String getPay_id() {
		return pay_id;
	}

	public void setPay_id(String pay_id) {
		this.pay_id = pay_id;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public float getRefund_paid() {
		return refund_paid;
	}

	public void setRefund_paid(float refund_paid) {
		this.refund_paid = refund_paid;
	}

	public long getRefund_time() {
		return refund_time;
	}

	public void setRefund_time(long refund_time) {
		this.refund_time = refund_time;
	}
	
	
	
	

}
