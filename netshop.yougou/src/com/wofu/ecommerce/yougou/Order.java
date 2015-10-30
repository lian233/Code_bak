package com.wofu.ecommerce.yougou;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Order extends BusinessObject{

	private Date online_pay_time;   //订单在线支付时间
	private float coupon_pref_amount5;  //礼品卡优惠金额
	private float prod_total_amt;  //货品结算总金额
	private float actual_postage;  //实际运费
	private String order_status_name;   //订单状态名称
	private String mobile_phone;        //收货人手机号码
	private String consignee_name;      //收货人姓名
	private String area_name;          //区（中文名称）
	private String order_sub_no;  //--原始订单号
	private Date modify_time;          //修改日期
	private String logistics_name;  //--快递公司名称
	private String payment;  //--支付方式
	private String zipcode;  //--收货人邮编
	private String constact_phone;  //--收货人电话
	private float discount_amount;  //--总优惠金额
	private float order_pay_total_amont;  //--订单支付金额
	private String province_name;  //--省（中文名称）
	private String city_name;  //--市（中文名称）
	private float coupon_pref_amount;  //优惠券优惠金额
	private Date create_time;  //--创建日期
	private String consignee_address;  //--收货人详细地址
	private String member_name;  //--会员号
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
