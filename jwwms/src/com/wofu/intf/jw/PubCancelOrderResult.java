	package com.wofu.intf.jw;

import com.wofu.base.util.BusinessObject;

public class PubCancelOrderResult extends BusinessObject{
	private String uuid="";
	private String orderCode="";  //������
	private String outerCode="";  //�ⲿ����
	private String type="";       //�Ƿ�ȡ���ɹ�
	private String isSuccess="";
	private String msg="";        //ԭ����Ϣ
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
