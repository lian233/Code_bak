package com.wofu.ecommerce.vjia;

import java.util.ArrayList;
import java.util.Hashtable;

public class DeliveryResult {
	private String orgid = "" ;//机构ID
	private String ordercode = "" ;//订单号
	private int status = -2 ;//状态 -2:未查询 -1: 疑是 0: 妥投 1: 退回
	private String isupdate = "" ;//是否已更新过状态 0: 未更新 1: 已更新  已更新过的订单不允许重复更新
	private boolean queryState = false ;
	private ArrayList<Hashtable<String, String>> deliveryNote = new ArrayList<Hashtable<String,String>>() ;//投递信息记录
	public ArrayList<Hashtable<String, String>> getDeliveryNote() {
		return deliveryNote;
	}
	public void setDeliveryNote(ArrayList<Hashtable<String, String>> deliveryNote) {
		this.deliveryNote = deliveryNote;
	}
	public String getIsupdate() {
		return isupdate;
	}
	public void setIsupdate(String isupdate) {
		this.isupdate = isupdate;
	}
	public String getOrdercode() {
		return ordercode;
	}
	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public boolean getQueryState() {
		return queryState;
	}
	public void setQueryState(boolean queryState) {
		this.queryState = queryState;
	}

	
}
