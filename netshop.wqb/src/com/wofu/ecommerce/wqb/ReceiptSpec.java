package com.wofu.ecommerce.wqb;

import com.wofu.base.util.BusinessObject;

public class ReceiptSpec extends BusinessObject{
	private String ReceiptType;//��Ʊ����
	private String ReceiptName;//��Ʊ̧ͷ
	private String ReceiptDetails;//��Ʊ����
	public String getReceiptType() {
		return ReceiptType;
	}
	public void setReceiptType(String receiptType) {
		ReceiptType = receiptType;
	}
	public String getReceiptName() {
		return ReceiptName;
	}
	public void setReceiptName(String receiptName) {
		ReceiptName = receiptName;
	}
	public String getReceiptDetails() {
		return ReceiptDetails;
	}
	public void setReceiptDetails(String receiptDetails) {
		ReceiptDetails = receiptDetails;
	}
	
}
