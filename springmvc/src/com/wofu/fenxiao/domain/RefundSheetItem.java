package com.wofu.fenxiao.domain;

import java.sql.Date;

public class RefundSheetItem {
	private Integer ID;
	private String SheetID;	
	private String OuterSkuID;
	private Integer FactQty;
	private int NotifyQty;		
	private double NotifyPrice;		
	private double FactPrice;
	private String Note;
	public Integer getID() {
		return ID;
	}
	public void setID(Integer id) {
		ID = id;
	}
	public String getSheetID() {
		return SheetID;
	}
	public void setSheetID(String sheetID) {
		SheetID = sheetID;
	}
	public String getOuterSkuID() {
		return OuterSkuID;
	}
	public void setOuterSkuID(String outerSkuID) {
		OuterSkuID = outerSkuID;
	}
	public Integer getFactQty() {
		return FactQty;
	}
	public void setFactQty(Integer factQty) {
		FactQty = factQty;
	}
	public int getNotifyQty() {
		return NotifyQty;
	}
	public void setNotifyQty(int notifyQty) {
		NotifyQty = notifyQty;
	}
	public double getNotifyPrice() {
		return NotifyPrice;
	}
	public void setNotifyPrice(double notifyPrice) {
		NotifyPrice = notifyPrice;
	}
	public double getFactPrice() {
		return FactPrice;
	}
	public void setFactPrice(double factPrice) {
		FactPrice = factPrice;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	
	
		
}
