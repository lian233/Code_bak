	package com.wofu.intf.jw;

import com.wofu.base.util.BusinessObject;

public class PubCancelOrderResult extends BusinessObject{
	private String uuid="";
	private String orderCode="";  //订单号
	private String outerCode="";  //外部单号
	private String type="";       //是否取消成功
	private String isSuccess="";
	private String msg="";        //原因信息
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getOuterCode() {
		return outerCode;
	}
	public void setOuterCode(String outerCode) {
		this.outerCode = outerCode;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
