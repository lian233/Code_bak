package com.wofu.ecommerce.papago8;
import java.util.ArrayList;
import java.util.Date;
import com.wofu.base.util.BusinessObject;

public class ReturnOrder extends BusinessObject{
	private String orderCode = "" ;//ԭ������
	private float dealMoney=0.00f;//���׽��
	private float returnMoney=0.00f;//�˿���
	private Date applyTime ;        //����ʱ��
	private String statusDesc = ""; //�˻�״̬
	private String reason;           //�˻�ԭ��
	private String expressCompanyCode="";  //������˾����
	private String mailNo="";              //�˵���
	private ArrayList<ReturnOrderItem> itemList = new ArrayList<ReturnOrderItem>() ;
	public ArrayList<ReturnOrderItem> getItemList() {
		return itemList;
	}
	public void setItemList(ArrayList<ReturnOrderItem> itemList) {
		this.itemList = itemList;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public float getDealMoney() {
		return dealMoney;
	}
	public void setDealMoney(float dealMoney) {
		this.dealMoney = dealMoney;
	}
	public float getReturnMoney() {
		return returnMoney;
	}
	public void setReturnMoney(float returnMoney) {
		this.returnMoney = returnMoney;
	}

	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	public String getExpressCompanyCode() {
		return expressCompanyCode;
	}
	public void setExpressCompanyCode(String expressCompanyCode) {
		this.expressCompanyCode = expressCompanyCode;
	}
	public String getMailNo() {
		return mailNo;
	}
	public void setMailNo(String mailNo) {
		this.mailNo = mailNo;
	}
	
	
}
