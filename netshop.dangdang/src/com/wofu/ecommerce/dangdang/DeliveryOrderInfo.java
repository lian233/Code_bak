package com.wofu.ecommerce.dangdang;

import java.util.ArrayList;

public class DeliveryOrderInfo {
	private String orderID="";
	private String logisticsName="";
	private String logisticsTel="";
	private String logisticsOrderID="";
	private ArrayList<DeliveryOrderInfoItem> list = new ArrayList<DeliveryOrderInfoItem>() ;
	public ArrayList<DeliveryOrderInfoItem> getList() {
		return list;
	}
	public void setList(ArrayList<DeliveryOrderInfoItem> list) {
		this.list = list;
	}
	public String getLogisticsName() {
		return logisticsName;
	}
	public void setLogisticsName(String logisticsName) {
		this.logisticsName = logisticsName;
	}
	public String getLogisticsOrderID() {
		return logisticsOrderID;
	}
	public void setLogisticsOrderID(String logisticsOrderID) {
		this.logisticsOrderID = logisticsOrderID;
	}
	public String getLogisticsTel() {
		return logisticsTel;
	}
	public void setLogisticsTel(String logisticsTel) {
		this.logisticsTel = logisticsTel;
	}
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
}
