package com.wofu.fenxiao.domain;

import java.util.List;

public class Login {
	private int id;
	private String name;
	private String cName;
	private int customerID;
	private String password;
	private String ip;
	private int status;//状态
	private String note;//备注
	private List<Part> part;//用户对应的角色列表
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		if (name==null){
			return null;
		}
		return name.trim();
	}
	public void setName(String name) {
		if (name==null){
			this.name = null;
		}else{
			this.name = name.trim();	
		}				
	}
	public String getCName() {
		return cName;
	}
	public void setCName(String name) {
		cName = name;
	}
	public int getCustomerID() {
		return customerID;
	}
	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public List<Part> getPart() {
		return part;
	}
	public void setPart(List<Part> part) {
		this.part = part;
	}
	
	
	
}
