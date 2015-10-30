package com.wofu.ecommerce.ecshop;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * 订单类
 *
 */
public class Order extends BusinessObject{
	
	private String order_sn = "";//订单编号
	private String user_name = "";//买家名称
	private String email = ""; //买家邮箱
	private String zipcode =""; //买家邮编
	private String postscript = "" ; //买家留言
	
	private float order_amount = 0f ;//应付款
	private float preferential =0.0f;//优惠
	private String province="";//省份
	private String city="";//市 
	private String district="";//区
	//收货人信息
	private float goods_amount=0f;//商品总金额
	private String to_buyer="";//商家留言
	private String pay_note="";//卖家留言
	private String shipping_id="";//收货省份
	private int add_time;//订单生成时间
	private String address="";//收货区
	private String mobile="";//邮编
	private String consignee="";//移动电话
	private String tel="";   //评价标识 ，1已评价 0未评价
	private String order_status;//订单状态 order_status  0 未确认 1 确认 2 已取消 3 无效 4 退货 
	private String shipping_status;// 0 未发货 1 已发货 2 已收货 4 退货
	private String pay_status;//是否支付  pay_status 0 未付款 1 付款中 2 已付款
	private String shipping_print="";//买家备注
	private String pay_time="";//订单支付时间
	private float fee=0f;//运费
	private float tax_fee=0f;//运费
	private String inv_payee="";//发票抬头
	private String inv_content="";//发票内容
	private String realename="";//海外购用户的真实名字
	private String subs_identi_no="";//海外购用户的身份证
	private String hwgs_fee="0.0";//海外购用户的关税金额
	
	private DataRelation shop_info =new DataRelation("shop_info","com.wofu.ecommerce.ecshop.OrderItem");

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getPostscript() {
		return postscript;
	}

	public void setPostscript(String postscript) {
		this.postscript = postscript;
	}

	public float getOrder_amount() {
		return order_amount;
	}

	public void setOrder_amount(float order_amount) {
		this.order_amount = order_amount;
	}

	public float getPreferential() {
		return preferential;
	}

	public void setPreferential(float preferential) {
		this.preferential = preferential;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public float getGoods_amount() {
		return goods_amount;
	}

	public void setGoods_amount(float goods_amount) {
		this.goods_amount = goods_amount;
	}

	public String getTo_buyer() {
		return to_buyer;
	}

	public void setTo_buyer(String to_buyer) {
		this.to_buyer = to_buyer;
	}

	public String getShipping_id() {
		return shipping_id;
	}

	public void setShipping_id(String shipping_id) {
		this.shipping_id = shipping_id;
	}

	public int getAdd_time() {
		return add_time;
	}

	public void setAdd_time(int add_time) {
		this.add_time = add_time;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
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

	public String getShipping_print() {
		return shipping_print;
	}

	public void setShipping_print(String shipping_print) {
		this.shipping_print = shipping_print;
	}

	public String getPay_time() {
		return pay_time;
	}

	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}

	public float getFee() {
		return fee;
	}

	public void setFee(float fee) {
		this.fee = fee;
	}

	public String getInv_payee() {
		return inv_payee;
	}

	public void setInv_payee(String inv_payee) {
		this.inv_payee = inv_payee;
	}

	public String getInv_content() {
		return inv_content;
	}

	public void setInv_content(String inv_content) {
		this.inv_content = inv_content;
	}

	public DataRelation getShop_info() {
		return shop_info;
	}

	public void setShop_info(DataRelation shop_info) {
		this.shop_info = shop_info;
	}

	public String getRealename() {
		return realename;
	}

	public void setRealename(String realename) {
		this.realename = realename;
	}

	public String getSubs_identi_no() {
		return subs_identi_no;
	}

	public void setSubs_identi_no(String subs_identi_no) {
		this.subs_identi_no = subs_identi_no;
	}

	public String getHwgs_fee() {
		return hwgs_fee;
	}

	public void setHwgs_fee(String hwgs_fee) {
		this.hwgs_fee = hwgs_fee;
	}

	public String getPay_note() {
		return pay_note;
	}

	public void setPay_note(String pay_note) {
		this.pay_note = pay_note;
	}

	public float getTax_fee() {
		return tax_fee;
	}

	public void setTax_fee(float tax_fee) {
		this.tax_fee = tax_fee;
	}
	
	
	
}
