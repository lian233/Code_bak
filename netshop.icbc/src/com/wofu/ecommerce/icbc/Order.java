package com.wofu.ecommerce.icbc;
import java.util.ArrayList;
import com.wofu.base.util.BusinessObject;

/**
 * 
 * 订单类
 *
 */
public class Order extends BusinessObject{
	private String order_id = "";//订单编号
	private String order_status = "";//订单状态
	private String order_modify_time = "";//最后更新时间
	private String  order_buyer_remark; //买家备注
	private String  order_seller_remark; //买家备注
	private String  order_buyer_id; //买家ID
	private String order_pay_sys; //支付系统号
	private String  order_buyer_username; //买家登录名
	private String  order_buyer_name; //买方名称
	private String  order_create_time; //下单时间
	private float  order_amount; //订单总金额
	private float  order_credit_amount; //积分抵扣金额
	private float  credit_liquid_amount; //积分清算金额
	private float  order_other_discount; //其他优惠金额
	private String  order_channel; //下单渠道
	private float  order_coupon_amount; //电子券抵扣金额
	private ArrayList<Discount>  discounts; //优惠信息列表
	private ArrayList<Product>  products; //商品列表
	private ArrayList<Activity>  activities; //活动列表
	private ArrayList<Tringproduct>  tringproducts; //搭售商品列表
	private ArrayList<Giftproduct>  giftproducts; //赠品列表
	private int  invoice_type; //发票类型
	private String  invoice_title; //发票抬头
	private String  invoice_content; //发票内容
	private String  order_pay_time; //付款时间
	private float  order_pay_amount; //实际支付的总金额
	private float  order_discount_amount; //优惠金额
	private float  order_freight; //运费
	private String  consignee_name; //收货人姓名
	private String  consignee_province; //收货地址省份
	private String  consignee_city; //收货地址城市名
	private String  consignee_district; //收货地址区县名
	private String  consignee_address; //详细地址
	private String  consignee_zipcode; //收货地址邮编
	private String  consignee_mobile; //收货人手机
	private String  consignee_phone; //收货人固定电话
	
	public String getOrder_pay_sys() {
		return order_pay_sys;
	}
	public void setOrder_pay_sys(String order_pay_sys) {
		this.order_pay_sys = order_pay_sys;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getOrder_status() {
		return order_status;
	}
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public String getOrder_modify_time() {
		return order_modify_time;
	}
	public void setOrder_modify_time(String order_modify_time) {
		this.order_modify_time = order_modify_time;
	}
	
	public String getOrder_buyer_remark() {
		return order_buyer_remark;
	}
	public void setOrder_buyer_remark(String order_buyer_remark) {
		this.order_buyer_remark = order_buyer_remark;
	}
	public String getOrder_seller_remark() {
		return order_seller_remark;
	}
	public void setOrder_seller_remark(String order_seller_remark) {
		this.order_seller_remark = order_seller_remark;
	}
	public String getOrder_buyer_id() {
		return order_buyer_id;
	}
	public void setOrder_buyer_id(String order_buyer_id) {
		this.order_buyer_id = order_buyer_id;
	}
	public String getOrder_buyer_username() {
		return order_buyer_username;
	}
	public void setOrder_buyer_username(String order_buyer_username) {
		this.order_buyer_username = order_buyer_username;
	}
	public String getOrder_buyer_name() {
		return order_buyer_name;
	}
	public void setOrder_buyer_name(String order_buyer_name) {
		this.order_buyer_name = order_buyer_name;
	}
	public String getOrder_create_time() {
		return order_create_time;
	}
	public void setOrder_create_time(String order_create_time) {
		this.order_create_time = order_create_time;
	}
	public float getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(float order_amount) {
		this.order_amount = order_amount;
	}
	public float getOrder_credit_amount() {
		return order_credit_amount;
	}
	public void setOrder_credit_amount(float order_credit_amount) {
		this.order_credit_amount = order_credit_amount;
	}
	public float getCredit_liquid_amount() {
		return credit_liquid_amount;
	}
	public void setCredit_liquid_amount(float credit_liquid_amount) {
		this.credit_liquid_amount = credit_liquid_amount;
	}
	public float getOrder_other_discount() {
		return order_other_discount;
	}
	public void setOrder_other_discount(float order_other_discount) {
		this.order_other_discount = order_other_discount;
	}
	public String getOrder_channel() {
		return order_channel;
	}
	public void setOrder_channel(String order_channel) {
		this.order_channel = order_channel;
	}
	public float getOrder_coupon_amount() {
		return order_coupon_amount;
	}
	public void setOrder_coupon_amount(float order_coupon_amount) {
		this.order_coupon_amount = order_coupon_amount;
	}
	public ArrayList<Discount> getDiscounts() {
		return discounts;
	}
	public void setDiscounts(ArrayList<Discount> discounts) {
		this.discounts = discounts;
	}
	public ArrayList<Product> getProducts() {
		return products;
	}
	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}
	public ArrayList<Activity> getActivities() {
		return activities;
	}
	public void setActivities(ArrayList<Activity> activities) {
		this.activities = activities;
	}
	public ArrayList<Tringproduct> getTringproducts() {
		return tringproducts;
	}
	public void setTringproducts(ArrayList<Tringproduct> tringproducts) {
		this.tringproducts = tringproducts;
	}
	public ArrayList<Giftproduct> getGiftproducts() {
		return giftproducts;
	}
	public void setGiftproducts(ArrayList<Giftproduct> giftproducts) {
		this.giftproducts = giftproducts;
	}
	public int getInvoice_type() {
		return invoice_type;
	}
	public void setInvoice_type(int invoice_type) {
		this.invoice_type = invoice_type;
	}
	public String getInvoice_title() {
		return invoice_title;
	}
	public void setInvoice_title(String invoice_title) {
		this.invoice_title = invoice_title;
	}
	public String getInvoice_content() {
		return invoice_content;
	}
	public void setInvoice_content(String invoice_content) {
		this.invoice_content = invoice_content;
	}
	public String getOrder_pay_time() {
		return order_pay_time;
	}
	public void setOrder_pay_time(String order_pay_time) {
		this.order_pay_time = order_pay_time;
	}
	public float getOrder_pay_amount() {
		return order_pay_amount;
	}
	public void setOrder_pay_amount(float order_pay_amount) {
		this.order_pay_amount = order_pay_amount;
	}
	public float getOrder_discount_amount() {
		return order_discount_amount;
	}
	public void setOrder_discount_amount(float order_discount_amount) {
		this.order_discount_amount = order_discount_amount;
	}
	public float getOrder_freight() {
		return order_freight;
	}
	public void setOrder_freight(float order_freight) {
		this.order_freight = order_freight;
	}
	public String getConsignee_name() {
		return consignee_name;
	}
	public void setConsignee_name(String consignee_name) {
		this.consignee_name = consignee_name;
	}
	public String getConsignee_province() {
		return consignee_province;
	}
	public void setConsignee_province(String consignee_province) {
		this.consignee_province = consignee_province;
	}
	public String getConsignee_city() {
		return consignee_city;
	}
	public void setConsignee_city(String consignee_city) {
		this.consignee_city = consignee_city;
	}
	public String getConsignee_district() {
		return consignee_district;
	}
	public void setConsignee_district(String consignee_district) {
		this.consignee_district = consignee_district;
	}
	public String getConsignee_address() {
		return consignee_address;
	}
	public void setConsignee_address(String consignee_address) {
		this.consignee_address = consignee_address;
	}
	public String getConsignee_zipcode() {
		return consignee_zipcode;
	}
	public void setConsignee_zipcode(String consignee_zipcode) {
		this.consignee_zipcode = consignee_zipcode;
	}
	public String getConsignee_mobile() {
		return consignee_mobile;
	}
	public void setConsignee_mobile(String consignee_mobile) {
		this.consignee_mobile = consignee_mobile;
	}
	public String getConsignee_phone() {
		return consignee_phone;
	}
	public void setConsignee_phone(String consignee_phone) {
		this.consignee_phone = consignee_phone;
	}
	
	
	
	
	

}
