package com.wofu.ecommerce.taobao;
import java.util.Date;

import com.wofu.base.util.BusinessObject;


public class Refund extends BusinessObject {
	private int serialid;
	private long refund_id;
	private long tid;
	private long oid;
	private String alipay_no;
	private String buyer_nick;
	private Date created;
	private Date modified;
	private String order_status;
	private String status;
	private String goods_status;
	private boolean has_goods_return;
	private double refund_fee;
	private double payment;
	private String reason;
	private String desc;
	private String title;
	private double price;
	private int num;
	private Date goods_return_time;
	private String sid;  //ÕÀªı‘Àµ•∫≈
	private double total_fee;
	private String company_name;
	private String address;
	private String attribute="";
	
	
	
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public long getRefund_id() {
		return refund_id;
	}
	public void setRefund_id(long refund_id) {
		this.refund_id = refund_id;
	}
	public long getTid() {
		return tid;
	}
	public void setTid(long tid) {
		this.tid = tid;
	}
	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAlipay_no() {
		return alipay_no;
	}
	public void setAlipay_no(String alipay_no) {
		this.alipay_no = alipay_no;
	}
	public String getBuyer_nick() {
		return buyer_nick;
	}
	public void setBuyer_nick(String buyer_nick) {
		this.buyer_nick = buyer_nick;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getGoods_return_time() {
		return goods_return_time;
	}
	public void setGoods_return_time(Date goods_return_time) {
		this.goods_return_time = goods_return_time;
	}
	public String getGoods_status() {
		return goods_status;
	}
	public void setGoods_status(String goods_status) {
		this.goods_status = goods_status;
	}
	public boolean getHas_goods_return() {
		return has_goods_return;
	}
	public void setHas_goods_return(boolean has_goods_return) {
		this.has_goods_return = has_goods_return;
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


	public String getOrder_status() {
		return order_status;
	}
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}
	public double getPayment() {
		return payment;
	}
	public void setPayment(double payment) {
		this.payment = payment;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public double getRefund_fee() {
		return refund_fee;
	}
	public void setRefund_fee(double refund_fee) {
		this.refund_fee = refund_fee;
	}

	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(double total_fee) {
		this.total_fee = total_fee;
	}
	public int getSerialid() {
		return serialid;
	}
	public void setSerialid(int serialid) {
		this.serialid = serialid;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	
}
