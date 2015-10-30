package com.wofu.ecommerce.vjia;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;


public class Order {
	private String orderid  ;//订单号 12位
	private Date orderdistributetime  ;//订单确认有效时间
	private String username  ;//用户姓名
	private String usertel  ;//用户的固定电话
	private String userphone  ;//用户的手机号userphone
	private String areaid  ;//订单所属的省、市、县（区）及区的代码
	private String postalcode  ;//邮编
	private String address ;//订单的完整地址
	private String needinvoice ;//是否需发票：1需要，0不需要
	private String invoiceTitle ;
	private String receivetime ;//送货时间
	private float totalprice ;//订单总金额
	private float transferprice ;//运费
	private float paidprice ;//已付款
	private float unpaidprice ;//应付款
	private String comment ;//用户留言
	private String payMode;  //1是在线支付	2是货到付款
	private String orderstatus;
	
	private Vector<OrderItem> orderitems=new Vector<OrderItem>();	//订单明细
	private Hashtable<String, OrderItem> orderItem = new Hashtable<String, OrderItem>() ;//订单明细，能防重复
	private ArrayList<String> invoiceItem = new ArrayList<String>() ;//发票信息

	public ArrayList<String> getInvoiceItem() {
		return invoiceItem;
	}

	public void setInvoiceItem(ArrayList<String> invoiceItem) {
		this.invoiceItem = invoiceItem;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAreaid() {
		return areaid;
	}

	public String getOrderstatus() {
		return orderstatus;
	}

	public void setOrderstatus(String orderstatus) {
		this.orderstatus = orderstatus;
	}

	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getNeedinvoice() {
		return needinvoice;
	}

	public void setNeedinvoice(String needinvoice) {
		this.needinvoice = needinvoice;
	}

	public Date getOrderdistributetime() {
		return orderdistributetime;
	}

	public void setOrderdistributetime(Date orderdistributetime) {
		this.orderdistributetime = orderdistributetime;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public Vector<OrderItem> getOrderitems() {
		return orderitems;
	}

	public void setOrderitems(Vector<OrderItem> orderitems) {
		this.orderitems = orderitems;
	}
	public void addOrderitems(OrderItem orderitem) {
		this.orderitems.add(orderitem) ;
	}

	public float getPaidprice() {
		return paidprice;
	}

	public void setPaidprice(float paidprice) {
		this.paidprice = paidprice;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	public String getReceivetime() {
		return receivetime;
	}

	public void setReceivetime(String receivetime) {
		this.receivetime = receivetime;
	}

	public float getTotalprice() {
		return totalprice;
	}

	public void setTotalprice(float totalprice) {
		this.totalprice = totalprice;
	}

	public float getTransferprice() {
		return transferprice;
	}

	public void setTransferprice(float transferprice) {
		this.transferprice = transferprice;
	}

	public float getUnpaidprice() {
		return unpaidprice;
	}

	public void setUnpaidprice(float unpaidprice) {
		this.unpaidprice = unpaidprice;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserphone() {
		return userphone;
	}

	public void setUserphone(String userphone) {
		this.userphone = userphone;
	}

	public String getUsertel() {
		return usertel;
	}

	public void setUsertel(String usertel) {
		this.usertel = usertel;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	public Hashtable<String, OrderItem> getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(Hashtable<String, OrderItem> orderItem) {
		this.orderItem = orderItem;
	}
	
}
