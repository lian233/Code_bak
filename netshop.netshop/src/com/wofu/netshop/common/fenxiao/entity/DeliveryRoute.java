package com.wofu.netshop.common.fenxiao.entity;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

/**
 * ¿ìµÝÂ·ÓÉ
 * @author Administrator
 *
 */
public class DeliveryRoute extends BusinessObject{
	private int id;
	private int deliveryid;
	private String deliverysheetid;
	private Date routetime;
	private String position;
	private String scantype;
	private String note;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDeliveryid() {
		return deliveryid;
	}
	public void setDeliveryid(int deliveryid) {
		this.deliveryid = deliveryid;
	}
	public String getDeliverysheetid() {
		return deliverysheetid;
	}
	public void setDeliverySheetid(String deliverySheetid) {
		this.deliverysheetid = deliverySheetid;
	}
	public Date getRoutetime() {
		return routetime;
	}
	public void setRoutetime(Date routetime) {
		this.routetime = routetime;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getScantype() {
		return scantype;
	}
	public void setScantype(String scantype) {
		this.scantype = scantype;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}
