package com.wofu.ecommerce.taobao.fenxiao;


import java.util.Date;


import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;

import com.wofu.common.tools.util.Formatter;

import com.wofu.common.tools.util.Types;
import com.wofu.common.tools.util.log.Log;

public class Trade extends BusinessObject {

	private String seller_nick;
	private int num;
	private long tid;
	private String type;
	private Date created;
	private String buyer_memo;
	private String buyer_message;
	private String shipping_type;
	private double payment;
	private double discount_fee;
	private double adjust_fee;
	private String status;
	private String seller_memo;
	private String trade_memo;
	private Date pay_time;
	private Date end_time;
	private Date modified;
	private int buyer_obtain_point_fee;  //买家获的积分
	private int point_fee;				//买家使用的积分
	private int real_point_fee;			//买家实际使用积分
	private double total_fee;			//商品总金额
	private double post_fee;			//邮费
	private String buyer_alipay_no;		//买家支付宝帐户
	private String buyer_nick;			//买家昵称
	private String receiver_name;
	private String receiver_state;
	private String receiver_city;
	private String receiver_district;	
	private String receiver_address;
	private String receiver_zip;
	private String receiver_mobile;
	private String receiver_phone;
	private Date consign_time;	//卖家发货时间
	private Date jdp_modified;	//订单最新修改时间
	private String buyer_email;
	private double received_payment; //卖家实际收到的支付宝打款金额
	private String alipay_no;	//支付宝交易号
	private int seller_flag;	//卖家旗帜
	private int buyer_flag;
	private boolean is_brand_sale;  //是否为品牌特买
	private boolean seller_rate;	//卖家是否已评价
	private boolean buyer_rate;		//买家是否已评价
	private String trade_from;		//交易来源
	private boolean has_post_fee;
	private String promotion_detail;
	private String promotion;
	private String alipay_url;
	private String seller_name;
	
	
	private DataRelation orders =new DataRelation("order","com.wofu.ecommerce.taobao.fenxiao.Order");
	
	private DataRelation promotion_details =new DataRelation("promotion_detail","com.wofu.ecommerce.taobao.fenxiao.PromotionDetail");
	private DataRelation service_orders = new DataRelation("service_order","com.wofu.ecommerce.taobao.fenxiao.ServiceOrder");
	

		
	public String getSeller_name() {
		return seller_name;
	}
	public void setSeller_name(String seller_name) {
		this.seller_name = seller_name;
	}
	public String getAlipay_url() {
		return alipay_url;
	}
	public void setAlipay_url(String alipay_url) {
		this.alipay_url = alipay_url;
	}
	public String getPromotion() {
		return promotion;
	}
	public void setPromotion(String promotion) {
		this.promotion = promotion;
	}
	public int getBuyer_flag() {
		return buyer_flag;
	}
	public void setBuyer_flag(int buyer_flag) {
		this.buyer_flag = buyer_flag;
	}
	public String getBuyer_memo() {
		return buyer_memo;
	}
	public void setBuyer_memo(String buyer_memo) {
		this.buyer_memo = buyer_memo;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public DataRelation getOrders() {
		return orders;
	}
	public void setOrders(DataRelation orders) {
		this.orders = orders;
	}
	public DataRelation getPromotion_details() {
		return promotion_details;
	}
	public void setPromotion_details(DataRelation promotion_details) {
		this.promotion_details = promotion_details;
	}
	public double getAdjust_fee() {
		return adjust_fee;
	}
	public void setAdjust_fee(double adjust_fee) {
		this.adjust_fee = adjust_fee;
	}
	public String getAlipay_no() {
		return alipay_no;
	}
	public void setAlipay_no(String alipay_no) {
		this.alipay_no = alipay_no;
	}

	public String getBuyer_alipay_no() {
		return buyer_alipay_no;
	}
	public void setBuyer_alipay_no(String buyer_alipay_no) {
		this.buyer_alipay_no = buyer_alipay_no;
	}
	public String getBuyer_email() {
		return buyer_email;
	}
	public void setBuyer_email(String buyer_email) {
		this.buyer_email = buyer_email;
	}

	public String getBuyer_message() {
		return buyer_message;
	}
	public void setBuyer_message(String buyer_message) {
		this.buyer_message = buyer_message;
	}
	public String getBuyer_nick() {
		return buyer_nick;
	}
	public void setBuyer_nick(String buyer_nick) {
		this.buyer_nick = buyer_nick;
	}
	public int getBuyer_obtain_point_fee() {
		return buyer_obtain_point_fee;
	}
	public void setBuyer_obtain_point_fee(int buyer_obtain_point_fee) {
		this.buyer_obtain_point_fee = buyer_obtain_point_fee;
	}
	public boolean getBuyer_rate() {
		return buyer_rate;
	}
	public void setBuyer_rate(boolean buyer_rate) {
		this.buyer_rate = buyer_rate;
	}
	public Date getConsign_time() {
		return consign_time;
	}
	public void setConsign_time(Date consign_time) {
		this.consign_time = consign_time;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public double getDiscount_fee() {
		return discount_fee;
	}
	public void setDiscount_fee(double discount_fee) {
		this.discount_fee = discount_fee;
	}
	public Date getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}
	public boolean getHas_post_fee() {
		return has_post_fee;
	}
	public void setHas_post_fee(boolean has_post_fee) {
		this.has_post_fee = has_post_fee;
	}
	public boolean getIs_brand_sale() {
		return is_brand_sale;
	}
	public void setIs_brand_sale(boolean is_brand_sale) {
		this.is_brand_sale = is_brand_sale;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public Date getPay_time() {
		return pay_time;
	}
	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
	}
	public double getPayment() {
		return payment;
	}
	public void setPayment(double payment) {
		this.payment = payment;
	}
	public int getPoint_fee() {
		return point_fee;
	}
	public void setPoint_fee(int point_fee) {
		this.point_fee = point_fee;
	}
	public double getPost_fee() {
		return post_fee;
	}
	public void setPost_fee(double post_fee) {
		this.post_fee = post_fee;
	}

	public String getPromotion_detail() {
		return promotion_detail;
	}
	public void setPromotion_detail(String promotion_detail) {
		this.promotion_detail = promotion_detail;
	}
	public int getReal_point_fee() {
		return real_point_fee;
	}
	public void setReal_point_fee(int real_point_fee) {
		this.real_point_fee = real_point_fee;
	}
	public double getReceived_payment() {
		return received_payment;
	}
	public void setReceived_payment(double received_payment) {
		this.received_payment = received_payment;
	}
	public String getReceiver_address() {
		return receiver_address;
	}
	public void setReceiver_address(String receiver_address) {
		this.receiver_address = receiver_address;
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
	public String getReceiver_mobile() {
		return receiver_mobile;
	}
	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}
	public String getReceiver_name() {
		return receiver_name;
	}
	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}
	public String getReceiver_phone() {
		return receiver_phone;
	}
	public void setReceiver_phone(String receiver_phone) {
		this.receiver_phone = receiver_phone;
	}
	public String getReceiver_state() {
		return receiver_state;
	}
	public void setReceiver_state(String receiver_state) {
		this.receiver_state = receiver_state;
	}
	public String getReceiver_zip() {
		return receiver_zip;
	}
	public void setReceiver_zip(String receiver_zip) {
		this.receiver_zip = receiver_zip;
	}
	public int getSeller_flag() {
		return seller_flag;
	}
	public void setSeller_flag(int seller_flag) {
		this.seller_flag = seller_flag;
	}
	public String getSeller_memo() {
		return seller_memo;
	}
	public void setSeller_memo(String seller_memo) {
		this.seller_memo = seller_memo;
	}
	public String getSeller_nick() {
		return seller_nick;
	}
	public void setSeller_nick(String seller_nick) {
		this.seller_nick = seller_nick;
	}
	public boolean getSeller_rate() {
		return seller_rate;
	}
	public void setSeller_rate(boolean seller_rate) {
		this.seller_rate = seller_rate;
	}
	public String getShipping_type() {
		return shipping_type;
	}
	public void setShipping_type(String shipping_type) {
		this.shipping_type = shipping_type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(double total_fee) {
		this.total_fee = total_fee;
	}
	public String getTrade_from() {
		return trade_from;
	}
	public void setTrade_from(String trade_from) {
		this.trade_from = trade_from;
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
	public long getTid() {
		return tid;
	}
	public void setTid(long tid) {
		this.tid = tid;
	}
	public DataRelation getService_orders() {
		return service_orders;
	}
	public void setService_orders(DataRelation service_orders) {
		this.service_orders = service_orders;
	}
	public Date getJdp_modified() {
		return jdp_modified;
	}
	public void setJdp_modified(Date jdp_modified) {
		this.jdp_modified = jdp_modified;
	}
	
	


}
