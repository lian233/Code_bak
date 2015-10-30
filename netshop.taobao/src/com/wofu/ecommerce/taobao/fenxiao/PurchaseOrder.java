package com.wofu.ecommerce.taobao.fenxiao;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class PurchaseOrder extends BusinessObject {
	
	private long fenxiao_id;
	private String supplier_memo;
	private String pay_type;
	private String trade_type;
	private String distributor_from;
	private long id;
	private String status;
	private String memo;
	private long tc_order_id;
	private Receiver receiverinfo;
	private String shipping;
	private String logistics_company_name;
	private String logistics_id;
	private Date end_time;
	private long supplier_flag;
	private double buyer_payment;
	private String supplier_from;
	private String supplier_username;
	private String distributor_username;
	private Date created;
	private String alipay_no;
	private double total_fee;
	private double post_fee;
	private double distributor_payment;
	private String snapshot_url;
	private Date pay_time;
	private Date consign_time;
	private Date modified;
	
	private DataRelation sub_purchase_orders =new DataRelation("order","com.wofu.ecommerce.taobao.SubPurchaseOrder");
	
	
	public PurchaseOrder()
	{
		this.receiverinfo=new Receiver();
	}

	public long getFenxiao_id() {
		return fenxiao_id;
	}

	public void setFenxiao_id(long fenxiao_id) {
		this.fenxiao_id = fenxiao_id;
	}

	public String getSupplier_memo() {
		return supplier_memo;
	}

	public void setSupplier_memo(String supplier_memo) {
		this.supplier_memo = supplier_memo;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getDistributor_from() {
		return distributor_from;
	}

	public void setDistributor_from(String distributor_from) {
		this.distributor_from = distributor_from;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public long getTc_order_id() {
		return tc_order_id;
	}

	public void setTc_order_id(long tc_order_id) {
		this.tc_order_id = tc_order_id;
	}



	public Receiver getReceiverinfo() {
		return receiverinfo;
	}

	public void setReceiverinfo(Receiver receiverinfo) {
		this.receiverinfo = receiverinfo;
	}

	public String getShipping() {
		return shipping;
	}

	public void setShipping(String shipping) {
		this.shipping = shipping;
	}

	public String getLogistics_company_name() {
		return logistics_company_name;
	}

	public void setLogistics_company_name(String logistics_company_name) {
		this.logistics_company_name = logistics_company_name;
	}

	public String getLogistics_id() {
		return logistics_id;
	}

	public void setLogistics_id(String logistics_id) {
		this.logistics_id = logistics_id;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public long getSupplier_flag() {
		return supplier_flag;
	}

	public void setSupplier_flag(long supplier_flag) {
		this.supplier_flag = supplier_flag;
	}

	public double getBuyer_payment() {
		return buyer_payment;
	}

	public void setBuyer_payment(double buyer_payment) {
		this.buyer_payment = buyer_payment;
	}

	public String getSupplier_from() {
		return supplier_from;
	}

	public void setSupplier_from(String supplier_from) {
		this.supplier_from = supplier_from;
	}

	public String getSupplier_username() {
		return supplier_username;
	}

	public void setSupplier_username(String supplier_username) {
		this.supplier_username = supplier_username;
	}

	public String getDistributor_username() {
		return distributor_username;
	}

	public void setDistributor_username(String distributor_username) {
		this.distributor_username = distributor_username;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getAlipay_no() {
		return alipay_no;
	}

	public void setAlipay_no(String alipay_no) {
		this.alipay_no = alipay_no;
	}

	public double getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(double total_fee) {
		this.total_fee = total_fee;
	}

	public double getPost_fee() {
		return post_fee;
	}

	public void setPost_fee(double post_fee) {
		this.post_fee = post_fee;
	}

	public double getDistributor_payment() {
		return distributor_payment;
	}

	public void setDistributor_payment(double distributor_payment) {
		this.distributor_payment = distributor_payment;
	}

	public String getSnapshot_url() {
		return snapshot_url;
	}

	public void setSnapshot_url(String snapshot_url) {
		this.snapshot_url = snapshot_url;
	}

	public Date getPay_time() {
		return pay_time;
	}

	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
	}

	public Date getConsign_time() {
		return consign_time;
	}

	public void setConsign_time(Date consign_time) {
		this.consign_time = consign_time;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public DataRelation getSub_purchase_orders() {
		return sub_purchase_orders;
	}

	public void setSub_purchase_orders(DataRelation sub_purchase_orders) {
		this.sub_purchase_orders = sub_purchase_orders;
	}
	
	

}
