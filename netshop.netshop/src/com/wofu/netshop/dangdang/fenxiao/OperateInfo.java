package com.wofu.netshop.dangdang.fenxiao;

import java.util.Date;

public class OperateInfo {
	private String operateRole="" ; //������ɫ
	private Date operateTime ;//����ʱ�� 
	private String operateDetails ="";//����
	private String returnOrderID="";//�˻������
	private String exchangeOrderID="";//���������
	public String getExchangeOrderID() {
		return exchangeOrderID;
	}
	public void setExchangeOrderID(String exchangeOrderID) {
		this.exchangeOrderID = exchangeOrderID;
	}
	public String getOperateDetails() {
		return operateDetails;
	}
	public void setOperateDetails(String operateDetails) {
		this.operateDetails = operateDetails;
	}
	public String getOperateRole() {
		return operateRole;
	}
	public void setOperateRole(String operateRole) {
		this.operateRole = operateRole;
	}
	public Date getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
	public String getReturnOrderID() {
		return returnOrderID;
	}
	public void setReturnOrderID(String returnOrderID) {
		this.returnOrderID = returnOrderID;
	}
}
