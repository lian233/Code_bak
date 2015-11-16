package com.wofu.base.common;

import com.wofu.base.util.BusinessObject;

public class ECS_BusiTypeConfig extends BusinessObject {
	private int busitype;
	private String accclass;
	private String description;

	public int getBusitype() {
		return busitype;	
	}
	public void setBusitype(int busitype) {
		this.busitype = busitype;
	}
	public String getAccclass() {
		return accclass;	
	}
	public void setAccclass(String accclass) {
		this.accclass = accclass;
	}
	public String getDescription() {
		return description;	
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
