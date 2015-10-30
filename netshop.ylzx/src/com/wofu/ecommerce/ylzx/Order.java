package com.wofu.ecommerce.ylzx;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.wofu.base.util.BusinessObject;
public class Order extends BusinessObject{

	private String order_id;
	private String order_sn;
	private String type;
	private String seller_name;
	private String buyer_id;
	private String buyer_name;
	private String status;
	private Date add_time;//下单时间
	private String payment_name;
	private String payment_code;
	private Date pay_time;
	private String pay_message;//线下付款告知店主的信息
	private Date finished_time;//交易完成时间
	private float goods_amount;//订单商品总额
	private float discount;//折扣金额
	private float order_amount;//折后订单总额
	private int evaluation_status;//买家是否评价
	private Date evaluation_time;//买家评价时间
	private String postscript;//买家给卖家的附言
	private String invoice_kind;//发票类型
	private String invoice_content;//发票内容
	private String invoice_title;//发票抬头
	private String dispose_remark;//处理备注
	private float seller_income;//卖家实得金额
	private String consignee;//收货人
	private String region_name;//地区名称
	private String address;//详细地址
	private String zipcode;//邮政编码
	private String phone_tel;//电话
	private String phone_mob;//手机号码
	private String shipping_name;//配送方式名称
	private float shipping_fee;//配送费用
	private float mall_rate;//商城扣点(%)
	private float mall_income;//配送费用
	private float recommend_rate;//中介扣点(%)
	private float recommend_income;//中介实得总额
	List<OrderItem> orderItems = new ArrayList<OrderItem>();
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSeller_name() {
		return seller_name;
	}
	public void setSeller_name(String seller_name) {
		this.seller_name = seller_name;
	}
	public String getBuyer_id() {
		return buyer_id;
	}
	public void setBuyer_id(String buyer_id) {
		this.buyer_id = buyer_id;
	}
	public String getBuyer_name() {
		return buyer_name;
	}
	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getAdd_time() {
		return add_time;
	}
	public void setAdd_time(Date add_time) {
		this.add_time = add_time;
	}
	public String getPayment_name() {
		return payment_name;
	}
	public void setPayment_name(String payment_name) {
		this.payment_name = payment_name;
	}
	public String getPayment_code() {
		return payment_code;
	}
	public void setPayment_code(String payment_code) {
		this.payment_code = payment_code;
	}
	public Date getPay_time() {
		return pay_time;
	}
	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
	}
	public String getPay_message() {
		return pay_message;
	}
	public void setPay_message(String pay_message) {
		this.pay_message = pay_message;
	}
	public Date getFinished_time() {
		return finished_time;
	}
	public void setFinished_time(Date finished_time) {
		this.finished_time = finished_time;
	}
	public float getGoods_amount() {
		return goods_amount;
	}
	public void setGoods_amount(float goods_amount) {
		this.goods_amount = goods_amount;
	}
	public float getDiscount() {
		return discount;
	}
	public void setDiscount(float discount) {
		this.discount = discount;
	}
	public float getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(float order_amount) {
		this.order_amount = order_amount;
	}
	public int getEvaluation_status() {
		return evaluation_status;
	}
	public void setEvaluation_status(int evaluation_status) {
		this.evaluation_status = evaluation_status;
	}
	public Date getEvaluation_time() {
		return evaluation_time;
	}
	public void setEvaluation_time(Date evaluation_time) {
		this.evaluation_time = evaluation_time;
	}
	public String getPostscript() {
		return postscript;
	}
	public void setPostscript(String postscript) {
		this.postscript = postscript;
	}
	public String getInvoice_kind() {
		return invoice_kind;
	}
	public void setInvoice_kind(String invoice_kind) {
		this.invoice_kind = invoice_kind;
	}
	public String getInvoice_content() {
		return invoice_content;
	}
	public void setInvoice_content(String invoice_content) {
		this.invoice_content = invoice_content;
	}
	public String getInvoice_title() {
		return invoice_title;
	}
	public void setInvoice_title(String invoice_title) {
		this.invoice_title = invoice_title;
	}
	public String getDispose_remark() {
		return dispose_remark;
	}
	public void setDispose_remark(String dispose_remark) {
		this.dispose_remark = dispose_remark;
	}
	public float getSeller_income() {
		return seller_income;
	}
	public void setSeller_income(float seller_income) {
		this.seller_income = seller_income;
	}
	public String getConsignee() {
		return consignee;
	}
	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}
	public String getRegion_name() {
		return region_name;
	}
	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getPhone_tel() {
		return phone_tel;
	}
	public void setPhone_tel(String phone_tel) {
		this.phone_tel = phone_tel;
	}
	public String getPhone_mob() {
		return phone_mob;
	}
	public void setPhone_mob(String phone_mob) {
		this.phone_mob = phone_mob;
	}
	public String getShipping_name() {
		return shipping_name;
	}
	public void setShipping_name(String shipping_name) {
		this.shipping_name = shipping_name;
	}
	public float getShipping_fee() {
		return shipping_fee;
	}
	public void setShipping_fee(float shipping_fee) {
		this.shipping_fee = shipping_fee;
	}
	public float getMall_rate() {
		return mall_rate;
	}
	public void setMall_rate(float mall_rate) {
		this.mall_rate = mall_rate;
	}
	public float getMall_income() {
		return mall_income;
	}
	public void setMall_income(float mall_income) {
		this.mall_income = mall_income;
	}
	public float getRecommend_rate() {
		return recommend_rate;
	}
	public void setRecommend_rate(float recommend_rate) {
		this.recommend_rate = recommend_rate;
	}
	public float getRecommend_income() {
		return recommend_income;
	}
	public void setRecommend_income(float recommend_income) {
		this.recommend_income = recommend_income;
	}
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	

	
	
	
	
	
}
