package com.wofu.ecommerce.taobao;


import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class Order extends BusinessObject {
	private long oid;
	private String sku_id;
	private String iid;
	private int item_meal_id;
	private String order_from;
	private long refund_id;
	private String item_meal_name;  //套餐名称
	private String title;			//宝贝标题
	private String seller_nick;			//卖家昵称
	private String buyer_nick;			//买家昵称
	private String outer_iid;			//商家外部编码
	private String outer_sku_id;		//商家SKU编码
	private double total_fee;			//应付金额
	private double payment;				//子订单实付金额
	private double discount_fee;		//子订单折扣金额
	private double adjust_fee;
	private String status;
	private String sku_properties_name;
	private int num;	
	private double price;
	private String pic_path;
	private boolean buyer_rate;
	private boolean seller_rate;
	private Date modified;			//订单修改时间
	private String num_iid;			//商品ID
	private int cid;				//类目ID
	private boolean is_oversold;	//是否超卖
	private boolean is_service_order;
	private Date end_time;
	private Date consign_time;
	private String shipping_type;
	private String bind_oid;
	private String logistics_company;
	private String invoice_no;
	private boolean is_daixiao;
	private double divide_order_fee;
	private double part_mjz_discoun;
	private String refund_status;
	private String snapshot_url;
	private String snapshot;
	private Date timeout_action_time;
	private String seller_type;

	
	public Order()
	{
		outer_iid="";
		outer_sku_id="";
	}
	
	public String getIid() {
		return iid;
	}
	public void setIid(String iid) {
		this.iid = iid;
	}
	public int getItem_meal_id() {
		return item_meal_id;
	}
	public void setItem_meal_id(int item_meal_id) {
		this.item_meal_id = item_meal_id;
	}
	public String getOrder_from() {
		return order_from;
	}
	public void setOrder_from(String order_from) {
		this.order_from = order_from;
	}

	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}
	public long getRefund_id() {
		return refund_id;
	}
	public void setRefund_id(long refund_id) {
		this.refund_id = refund_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean getIs_service_order() {
		return is_service_order;
	}
	public void setIs_service_order(boolean is_service_order) {
		this.is_service_order = is_service_order;
	}
	public Date getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}
	public Date getConsign_time() {
		return consign_time;
	}
	public void setConsign_time(Date consign_time) {
		this.consign_time = consign_time;
	}
	public String getShipping_type() {
		return shipping_type;
	}
	public void setShipping_type(String shipping_type) {
		this.shipping_type = shipping_type;
	}
	public String getBind_oid() {
		return bind_oid;
	}
	public void setBind_oid(String bind_oid) {
		this.bind_oid = bind_oid;
	}
	public String getLogistics_company() {
		return logistics_company;
	}
	public void setLogistics_company(String logistics_company) {
		this.logistics_company = logistics_company;
	}
	public String getInvoice_no() {
		return invoice_no;
	}
	public void setInvoice_no(String invoice_no) {
		this.invoice_no = invoice_no;
	}
	public boolean getIs_daixiao() {
		return is_daixiao;
	}
	public void setIs_daixiao(boolean is_daixiao) {
		this.is_daixiao = is_daixiao;
	}
	public double getDivide_order_fee() {
		return divide_order_fee;
	}
	public void setDivide_order_fee(double divide_order_fee) {
		this.divide_order_fee = divide_order_fee;
	}
	public double getPart_mjz_discoun() {
		return part_mjz_discoun;
	}
	public void setPart_mjz_discoun(double part_mjz_discoun) {
		this.part_mjz_discoun = part_mjz_discoun;
	}
	public String getRefund_status() {
		return refund_status;
	}
	public void setRefund_status(String refund_status) {
		this.refund_status = refund_status;
	}
	public String getSnapshot_url() {
		return snapshot_url;
	}
	public void setSnapshot_url(String snapshot_url) {
		this.snapshot_url = snapshot_url;
	}
	public String getSnapshot() {
		return snapshot;
	}
	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}
	public Date getTimeout_action_time() {
		return timeout_action_time;
	}
	public void setTimeout_action_time(Date timeout_action_time) {
		this.timeout_action_time = timeout_action_time;
	}
	public String getSeller_type() {
		return seller_type;
	}
	public void setSeller_type(String seller_type) {
		this.seller_type = seller_type;
	}
	public boolean getIs_oversold() {
		return is_oversold;
	}
	public void setIs_oversold(boolean is_oversold) {
		this.is_oversold = is_oversold;
	}
	public double getAdjust_fee() {
		return adjust_fee;
	}
	public void setAdjust_fee(double adjust_fee) {
		this.adjust_fee = adjust_fee;
	}
	public String getBuyer_nick() {
		return buyer_nick;
	}
	public void setBuyer_nick(String buyer_nick) {
		this.buyer_nick = buyer_nick;
	}
	public boolean isBuyer_rate() {
		return buyer_rate;
	}
	public void setBuyer_rate(boolean buyer_rate) {
		this.buyer_rate = buyer_rate;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public double getDiscount_fee() {
		return discount_fee;
	}
	public void setDiscount_fee(double discount_fee) {
		this.discount_fee = discount_fee;
	}
	public String getItem_meal_name() {
		return item_meal_name;
	}
	public void setItem_meal_name(String item_meal_name) {
		this.item_meal_name = item_meal_name;
	}

	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getNum_iid() {
		return num_iid;
	}
	public void setNum_iid(String num_iid) {
		this.num_iid = num_iid;
	}


	public String getOuter_iid() {
		return outer_iid;
	}
	public void setOuter_iid(String outer_iid) {
		this.outer_iid = outer_iid;
	}
	public String getOuter_sku_id() {
		return outer_sku_id;
	}
	public void setOuter_sku_id(String outer_sku_id) {
		this.outer_sku_id = outer_sku_id;
	}
	public double getPayment() {
		return payment;
	}
	public void setPayment(double payment) {
		this.payment = payment;
	}
	public String getPic_path() {
		return pic_path;
	}
	public void setPic_path(String pic_path) {
		this.pic_path = pic_path;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
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
	public String getSku_id() {
		return sku_id;
	}
	public void setSku_id(String sku_id) {
		this.sku_id = sku_id;
	}
	public String getSku_properties_name() {
		return sku_properties_name;
	}
	public void setSku_properties_name(String sku_properties_name) {
		this.sku_properties_name = sku_properties_name;
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


}
