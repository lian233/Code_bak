package com.wofu.fenxiao.domain;

import java.io.Serializable;

public class JsonResult implements Serializable{
	private int errorCode;  //错误代码     0代表成功  
	private String msg;     //错误信息
	private Object data;    //数据部分
	private Object pageInfo;//分页数据
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Object getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(Object pageInfo) {
		this.pageInfo = pageInfo;
	}
	
}