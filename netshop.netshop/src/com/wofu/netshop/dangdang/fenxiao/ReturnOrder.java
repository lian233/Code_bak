package com.wofu.netshop.dangdang.fenxiao;

import java.util.ArrayList;
import java.util.Date;

public class ReturnOrder {
	
	private String orderID = "" ;//原订单号
	private String returnExchangeStatus = "" ;//状态 1:退货	2:换货
	private String returnExchangeCode = "" ;//退换货单号
	private float orderMoney = 0.0f ; //订单总金额
	private Date orderTime ;
	private String orderStatus = ""; //处理状态1:待处理	2:已处理		3:延期
	private String orderResult = ""; //处理结果 1:同意		2:拒绝		3:延期
	private String returnExchangeOrdersApprStatus = "" ; //退换货申请审核状态 0:待审核	1:审核通过	2:审核不通过
	private ArrayList<ReturnOrderItem> itemList = new ArrayList<ReturnOrderItem>() ;
	public ArrayList<ReturnOrderItem> getItemList() {
		return itemList;
	}
	public void setItemList(ArrayList<ReturnOrderItem> itemList) {
		this.itemList = itemList;
	}
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public float getOrderMoney() {
		return orderMoney;
	}
	public void setOrderMoney(float orderMoney) {
		this.orderMoney = orderMoney;
	}
	public String getOrderResult() {
		return orderResult;
	}
	public void setOrderResult(String orderResult) {
		this.orderResult = orderResult;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public Date getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	public String getReturnExchangeCode() {
		return returnExchangeCode;
	}
	public void setReturnExchangeCode(String returnExchangeCode) {
		this.returnExchangeCode = returnExchangeCode;
	}
	public String getReturnExchangeOrdersApprStatus() {
		return returnExchangeOrdersApprStatus;
	}
	public void setReturnExchangeOrdersApprStatus(
			String returnExchangeOrdersApprStatus) {
		this.returnExchangeOrdersApprStatus = returnExchangeOrdersApprStatus;
	}
	public String getReturnExchangeStatus() {
		return returnExchangeStatus;
	}
	public void setReturnExchangeStatus(String returnExchangeStatus) {
		this.returnExchangeStatus = returnExchangeStatus;
	}
}
