package com.wofu.ecommerce.lenovo;

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
	private String order_status = "";//订单状态
	private String shipping_status = "";//物流状态
	private String pay_status = "";//支付状态
	private String pay_name = "";//支付方式
	private String user_name = "";//收货人姓名
	private String province_name = "";//省
	private String city_name = "";//市
	private String district_name = "";//区
	private String address = "";//地址
	private String shipping_time = "";//送货时间
	private String inv_payee = "";//发票抬头  个人/单位名称   如果空则表示不开发票
	private String total_fee = "";//应付金额  如果在线支付 已支付完成   total_fee为0
	private String money_paid = "";//已付款金额
	private String shipping_fee = "";//运费
	private String discount = "";//优惠金额
	private String bonus = "";//优惠券/红包  金额
	private long add_time; //订单创建时间
	private String pay_id; //支付方式   1：在线  2：货到付款
	private String  tel; //支付方式   1：在线  2：货到付款
	
	private DataRelation goods_list =new DataRelation("goods_list","com.wofu.ecommerce.lenovo.OrderItem");

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
	
	

}
