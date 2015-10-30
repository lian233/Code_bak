package com.wofu.netshop.dangdang.fenxiao;

import java.util.Date;

public class OperateInfo {
	private String operateRole="" ; //操作角色
	private Date operateTime ;//操作时间 
	private String operateDetails ="";//操作
	private String returnOrderID="";//退货单编号
	private String exchangeOrderID="";//换货单编号
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
