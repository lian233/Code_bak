package com.wofu.fenxiao.domain;

import java.util.List;

public class Part {
	private int id;
	private String name;
	private int status;//状态
	private String note;//备注
	private List<Login> login;//这个角色所有的用户
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name.trim();
	}
	public void setName(String name) {
		this.name = name.trim();
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public List<Login> getLogin() {
		return login;
	}
	public void setLogin(List<Login> login) {
		this.login = login;
	}
	
	

	
}
