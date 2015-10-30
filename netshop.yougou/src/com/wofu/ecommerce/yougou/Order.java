package com.wofu.ecommerce.yougou;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Order extends BusinessObject{

	private Date online_pay_time;   //��������֧��ʱ��
	private float coupon_pref_amount5;  //��Ʒ���Żݽ��
	private float prod_total_amt;  //��Ʒ�����ܽ��
	private float actual_postage;  //ʵ���˷�
	private String order_status_name;   //����״̬����
	private String mobile_phone;        //�ջ����ֻ�����
	private String consignee_name;      //�ջ�������
	private String area_name;          //�����������ƣ�
	private String order_sub_no;  //--ԭʼ������
	private Date modify_time;          //�޸�����
	private String logistics_name;  //--��ݹ�˾����
	private String payment;  //--֧����ʽ
	private String zipcode;  //--�ջ����ʱ�
	private String constact_phone;  //--�ջ��˵绰
	private float discount_amount;  //--���Żݽ��
	private float order_pay_total_amont;  //--����֧�����
	private String province_name;  //--ʡ���������ƣ�
	private String city_name;  //--�У��������ƣ�
	private float coupon_pref_amount;  //�Ż�ȯ�Żݽ��
	private Date create_time;  //--��������
	private String consignee_address;  //--�ջ�����ϸ��ַ
	private String member_name;  //--��Ա��
	DataRelation orderItem = new DataRelation("item_details","com.wofu.ecommerce.yougou.OrderItem");
	public Date getOnline_pay_time() {
		return online_pay_time;
	}
	public void setOnline_pay_time(Date online_pay_time) {
		this.online_pay_time = online_pay_time;
	}
	public float getCoupon_pref_amount5() {
		return coupon_pref_amount5;
	}
	public void setCoupon_pref_amount5(float coupon_pref_amount5) {
		this.coupon_pref_amount5 = coupon_pref_amount5;
	}
	public String getOrder_status_name() {
		return order_status_name;
	}
	public void setOrder_status_name(String order_status_name) {
		this.order_status_name = order_status_name;
	}
	public String getMobile_phone() {
		return mobile_phone;
	}
	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}
	public String getConsignee_name() {
		return consignee_name;
	}
	public void setConsignee_name(String consignee_name) {
		this.consignee_name = consignee_name;
	}
	public String getArea_name() {
		return area_name;
	}
	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

	public Date getModify_time() {
		return modify_time;
	}
	public void setModify_time(Date modify_time) {
		this.modify_time = modify_time;
	}
	public String getLogistics_name() {
		return logistics_name;
	}
	public void setLogistics_name(String logistics_name) {
		this.logistics_name = logistics_name;
	}
	public String getPayment() {
		return payment;
	}
	public void setPayment(String payment) {
		this.payment = payment;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getConstact_phone() {
		return constact_phone;
	}
	public void setConstact_phone(String constact_phone) {
		this.constact_phone = constact_phone;
	}
	public float getDiscount_amount() {
		return discount_amount;
	}
	public void setDiscount_amount(float discount_amount) {
		this.discount_amount = discount_amount;
	}
	public float getOrder_pay_total_amont() {
		return order_pay_total_amont;
	}
	public void setOrder_pay_total_amont(float order_pay_total_amont) {
		this.order_pay_total_amont = order_pay_total_amont;
	}
	public String getProvince_name() {
		return province_name;
	}
	public void setProvince_name(String province_name) {
		this.province_name = province_name;
	}
	public float getCoupon_pref_amount() {
		return coupon_pref_amount;
	}
	public void setCoupon_pref_amount(float coupon_pref_amount) {
		this.coupon_pref_amount = coupon_pref_amount;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getConsignee_address() {
		return consignee_address;
	}
	public void setConsignee_address(String consignee_address) {
		this.consignee_address = consignee_address;
	}
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
	public DataRelation getOrderItem() {
		return orderItem;
	}
	public void setOrderItem(DataRelation orderItem) {
		this.orderItem = orderItem;
	}
	public float getProd_total_amt() {
		return prod_total_amt;
	}
	public void setProd_total_amt(float prod_total_amt) {
		this.prod_total_amt = prod_total_amt;
	}
	public float getActual_postage() {
		return actual_postage;
	}
	public void setActual_postage(float actual_postage) {
		this.actual_postage = actual_postage;
	}
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	public String getOrder_sub_no() {
		return order_sub_no;
	}
	public void setOrder_sub_no(String order_sub_no) {
		this.order_sub_no = order_sub_no;
	}
	
	
	
	
	
	
	
	
}
