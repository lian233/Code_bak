package com.wofu.base.interfacemanager;

import java.sql.Timestamp;
import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class ECS_IntfBusList extends BusinessObject {

	private int busid;
	private int bustype;
	private int interfaceid;
	private int executeflag;
	private Timestamp stime;
	
	

	public int getBusid() {
		return busid;
	}

	public void setBusid(int busid) {
		this.busid = busid;
	}

	public int getExecuteflag() {
		return executeflag;
	}

	public void setExecuteflag(int executeflag) {
		this.executeflag = executeflag;
	}

	public int getInterfaceid() {
		return interfaceid;
	}

	public void setInterfaceid(int interfaceid) {
		this.interfaceid = interfaceid;
	}


	public int getBustype() {
		return bustype;
	}

	public void setBustype(int bustype) {
		this.bustype = bustype;
	}

	public Timestamp getStime() {
		return stime;
	}

	public void setStime(Timestamp stime) {
		this.stime = stime;
	}

	

}
