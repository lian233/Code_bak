package com.wofu.base.systemmanager;

import com.wofu.base.util.BusinessObject;

public class ECS_BusTypeConfig extends BusinessObject {

	private int bustype;
	private String invprocessor;
	public int getBustype() {
		return bustype;
	}
	public void setBustype(int bustype) {
		this.bustype = bustype;
	}
	public String getInvprocessor() {
		return invprocessor;
	}
	public void setInvprocessor(String invprocessor) {
		this.invprocessor = invprocessor;
	}
}
