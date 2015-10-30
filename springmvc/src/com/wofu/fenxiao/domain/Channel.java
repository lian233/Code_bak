package com.wofu.fenxiao.domain;

public class Channel {
	private int ID;
	private String Name;
	private String Code;
	private String Status;
	private String Note;
	private String url;
	private String AppUrl;
	
	public String getAppUrl() {
		return AppUrl;
	}
	public void setAppUrl(String appUrl) {
		AppUrl = appUrl;
	}
	public int getID() {
		return ID;
	}
	public void setID(int id) {
		ID = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
