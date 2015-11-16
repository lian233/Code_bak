package com.wofu.base.systemmanager;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class ECS_BusProcList extends BusinessObject {
	
	private int busid;
	private int bustype;
	private int executeflag;
	private Date stime;
	public int getBusid() {
		return busid;
	}
	public void setBusid(int busid) {
		this.busid = busid;
	}
	public int getBustype() {
		return bustype;
	}
	public void setBustype(int bustype) {
		this.bustype = bustype;
	}
	public int getExecuteflag() {
		return executeflag;
	}
	public void setExecuteflag(int executeflag) {
		this.executeflag = executeflag;
	}
	public Date getStime() {
		return stime;
	}
	public void setStime(Date stime) {
		this.stime = stime;
	}

}
