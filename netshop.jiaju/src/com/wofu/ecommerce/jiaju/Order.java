package com.wofu.ecommerce.jiaju;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * 订单类
 * 
 */
public class Order extends BusinessObject {

	private String order_id = ""; // 订单ID
	private String trade_id = ""; // 订单编号
	private String shop_id = ""; // 店铺ID
	private int status_ship = 0; // 配送状态
	private String status = ""; // 订单状态
	private float fee_trade = 0.0f; // 订单总额
	private float fee_refund = 0.0f; // 订单退款金额
	private float fee_fare = 0.0f; // 订单运费总额
	private float fee_goods = 0.0f; // 订单商品总额
	private Date add_time = new Date();// 下单时间
	private String ext_type = ""; // 外部订单类型
	private String ext_order_id = ""; // 外部订单ID
	private String invoice = ""; // 发票抬头
	private Date payment_time = new Date();// 付款时间
	private String nickname = ""; // 买家昵称
	private String remark = ""; // 买家留言
	private String tag_id = ""; // 关联分组(关联的订单ID)
	// ship_info
	private String consignee = ""; // 收货人
	private String prov = ""; // 收货地址省份
	private String city = ""; // 收货地址城市
	private String county = ""; // 收货地址地区
	private String detail_addr = ""; // 详细收货地址
	private String address = ""; // 完整收货信息
	private String zip = ""; // 邮政编码
	private String mobile = ""; // 手机号码
	private String tel = ""; // 固话
	private String company = ""; // 公司名称
	// goods
	private DataRelation goods = new DataRelation("goods","com.wofu.ecommerce.jiaju.OrderItem");

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

	public String getTrade_id() {
		return trade_id;
	}

	public void setShop_id(String shop_id) {
		this.shop_id = shop_id;
	}

	public String getShop_id() {
		return shop_id;
	}

	public void setStatus_ship(int status_ship) {
		this.status_ship = status_ship;
	}

	public int getStatus_ship() {
		return status_ship;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setFee_trade(float fee_trade) {
		this.fee_trade = fee_trade;
	}

	public float getFee_trade() {
		return fee_trade;
	}

	public void setFee_refund(float fee_refund) {
		this.fee_refund = fee_refund;
	}

	public float getFee_refund() {
		return fee_refund;
	}

	public void setFee_fare(float fee_fare) {
		this.fee_fare = fee_fare;
	}

	public float getFee_fare() {
		return fee_fare;
	}

	public void setFee_goods(float fee_goods) {
		this.fee_goods = fee_goods;
	}

	public float getFee_goods() {
		return fee_goods;
	}

	public void setAdd_time(Date add_time) {
		this.add_time = add_time;
	}

	public Date getAdd_time() {
		return add_time;
	}

	public void setExt_type(String ext_type) {
		this.ext_type = ext_type;
	}

	public String getExt_type() {
		return ext_type;
	}

	public void setExt_order_id(String ext_order_id) {
		this.ext_order_id = ext_order_id;
	}

	public String getExt_order_id() {
		return ext_order_id;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public String getInvoice() {
		return invoice;
	}

	public void setPayment_time(Date payment_time) {
		this.payment_time = payment_time;
	}

	public Date getPayment_time() {
		return payment_time;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemark() {
		return remark;
	}

	public void setTag_id(String tag_id) {
		this.tag_id = tag_id;
	}

	public String getTag_id() {
		return tag_id;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setProv(String prov) {
		this.prov = prov;
	}

	public String getProv() {
		return prov;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCounty() {
		return county;
	}

	public void setDetail_addr(String detail_addr) {
		this.detail_addr = detail_addr;
	}

	public String getDetail_addr() {
		return detail_addr;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getZip() {
		return zip;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile() {
		return mobile;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getTel() {
		return tel;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCompany() {
		return company;
	}

	public void setGoods(DataRelation goods) {
		this.goods = goods;
	}

	public DataRelation getGoods() {
		return goods;
	}
}
