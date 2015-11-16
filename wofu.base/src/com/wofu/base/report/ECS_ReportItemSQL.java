package com.wofu.base.report;

import java.io.InputStream;

import com.wofu.base.util.BusinessObject;

public class ECS_ReportItemSQL extends BusinessObject {
	private int itemid;
	private InputStream presql;
	private InputStream clearsql;
	public InputStream getClearsql() {
		return clearsql;
	}
	public void setClearsql(InputStream clearsql) {
		this.clearsql = clearsql;
	}
	public int getItemid() {
		return itemid;
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public InputStream getPresql() {
		return presql;
	}
	public void setPresql(InputStream presql) {
		this.presql = presql;
	}
}
