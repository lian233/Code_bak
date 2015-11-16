package com.wofu.base.report;

import com.wofu.base.util.BusinessObject;

public class ECS_ReportFolder extends BusinessObject {
	private int folderid;
	private String foldername;
	private int groupid;
	private int flag;
	private int parentid;

	public int getFolderid() {
		return folderid;	
	}
	public void setFolderid(int folderid) {
		this.folderid = folderid;
	}
	public String getFoldername() {
		return foldername;	
	}
	public void setFoldername(String foldername) {
		this.foldername = foldername;
	}
	public int getGroupid() {
		return groupid;	
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
	public int getParentid() {
		return parentid;	
	}
	public void setParentid(int parentid) {
		this.parentid = parentid;
	}

	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}

}
