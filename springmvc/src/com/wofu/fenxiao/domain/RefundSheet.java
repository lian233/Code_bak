package com.wofu.fenxiao.domain;

import java.util.Date;

public class RefundSheet {
	private Integer ID;
	private Integer CustomerID;
	private String SheetID;
	private String Delivery;
	private String DeliverySheetID;
	private Integer Flag;
	private String Editor;			
	private Date EditTime;		
	private String Checker;			
	private Date CheckTime;		
	private String Ender;			
	private Date EndTime;			
	private int TotalQty;		
	private double TotalAmount;	
	private double TotalRefundQty;
	private double TotalRefundAmount;
	private String Note;
	
	public Integer getID() {
		return ID;
	}
	public void setID(Integer id) {
		ID = id;
	}
	public Integer getCustomerID() {
		return CustomerID;
	}
	public void setCustomerID(Integer customerID) {
		CustomerID = customerID;
	}
	public String getSheetID() {
		return SheetID;
	}
	public void setSheetID(String sheetID) {
		SheetID = sheetID;
	}
	public String getDelivery() {
		return Delivery;
	}
	public void setDelivery(String delivery) {
		Delivery = delivery;
	}
	public String getDeliverySheetID() {
		return DeliverySheetID;
	}
	public void setDeliverySheetID(String deliverySheetID) {
		DeliverySheetID = deliverySheetID;
	}
	public Integer getFlag() {
		return Flag;
	}
	public void setFlag(Integer flag) {
		Flag = flag;
	}
	public String getEditor() {
		return Editor;
	}
	public void setEditor(String editor) {
		Editor = editor;
	}
	public Date getEditTime() {
		return EditTime;
	}
	public void setEditTime(Date editTime) {
		EditTime = editTime;
	}
	public String getChecker() {
		return Checker;
	}
	public void setChecker(String checker) {
		Checker = checker;
	}
	public Date getCheckTime() {
		return CheckTime;
	}
	public void setCheckTime(Date checkTime) {
		CheckTime = checkTime;
	}
	public String getEnder() {
		return Ender;
	}
	public void setEnder(String ender) {
		Ender = ender;
	}
	public Date getEndTime() {
		return EndTime;
	}
	public void setEndTime(Date endTime) {
		EndTime = endTime;
	}
	public int getTotalQty() {
		return TotalQty;
	}
	public void setTotalQty(int totalQty) {
		TotalQty = totalQty;
	}
	public double getTotalAmount() {
		return TotalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		TotalAmount = totalAmount;
	}
	public double getTotalRefundAmount() {
		return TotalRefundAmount;
	}
	public void setTotalRefundAmount(double totalRefundAmount) {
		TotalRefundAmount = totalRefundAmount;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public double getTotalRefundQty() {
		return TotalRefundQty;
	}
	public void setTotalRefundQty(double totalRefundQty) {
		TotalRefundQty = totalRefundQty;
	}
	
	
	
	
}
