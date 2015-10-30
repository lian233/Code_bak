package com.wofu.ecommerce.dangdang;

import java.util.ArrayList;
import java.util.Date;

public class ReturnOrder {
	
	private String orderID = "" ;//ԭ������
	private String returnExchangeStatus = "" ;//״̬ 1:�˻�	2:����
	private String returnExchangeCode = "" ;//�˻�������
	private float orderMoney = 0.0f ; //�����ܽ��
	private Date orderTime ;
	private String orderStatus = ""; //����״̬1:������	2:�Ѵ���		3:����
	private String orderResult = ""; //������ 1:ͬ��		2:�ܾ�		3:����
	private String returnExchangeOrdersApprStatus = "" ; //�˻����������״̬ 0:�����	1:���ͨ��	2:��˲�ͨ��
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
