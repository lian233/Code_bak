package com.wofu.base.report;

import com.wofu.base.util.BusinessObject;

public class ECS_ReportGroup extends BusinessObject {
	
	private int groupid;
	private String groupname;
	private int merchantid;
	public int getGroupid() {
		return groupid;
	}
	
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public int getMerchantid() {
		return merchantid;
	}
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}
}
