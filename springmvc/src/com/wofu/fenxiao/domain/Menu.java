package com.wofu.fenxiao.domain;

import java.util.List;

public class Menu {
	private int id; //菜单标识
	private int subSystemID; //系统标识
	private int moduleID; // 对应的模块标识
	private String caption; // 菜单显示名
	private String hint; // 菜单提示信息（热点信息）
	private String url; // 链接
	private int orderNo; // 显示排列顺序
	private int groupNo; // 分组，不同组的菜单出现在同一个菜单列表时，中间会出现分隔线。
	private int masterMenuID; // 上级菜单，0表示最上层菜单
	private int status;//状态
	private String note;//备注
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSubSystemID() {
		return subSystemID;
	}
	public void setSubSystemID(int subSystemID) {
		this.subSystemID = subSystemID;
	}
	public int getModuleID() {
		return moduleID;
	}
	public void setModuleID(int moduleID) {
		this.moduleID = moduleID;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}
	public int getGroupNo() {
		return groupNo;
	}
	public void setGroupNo(int groupNo) {
		this.groupNo = groupNo;
	}
	public int getMasterMenuID() {
		return masterMenuID;
	}
	public void setMasterMenuID(int masterMenuID) {
		this.masterMenuID = masterMenuID;
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

	
	
	
}
