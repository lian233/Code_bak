package com.wofu.base.report;

import java.io.InputStream;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class ECS_ReportItemQuerySQL extends BusinessObject {
	private int itemid;
	private int queryid;
	private String queryname;
	private InputStream querysql;
	
	private DataRelation columnofquerys =new DataRelation("columnofquery","com.wofu.base.report.ECS_ReportItemQueryColumn");
		

	public int getItemid() {
		return itemid;	
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public int getQueryid() {
		return queryid;	
	}
	public void setQueryid(int queryid) {
		this.queryid = queryid;
	}
	public String getQueryname() {
		return queryname;	
	}
	public void setQueryname(String queryname) {
		this.queryname = queryname;
	}
	public InputStream getQuerysql() {
		return querysql;	
	}
	public void setQuerysql(InputStream querysql) {
		this.querysql = querysql;
	}
	public DataRelation getColumnofquerys() {
		return columnofquerys;
	}
	public void setColumnofquerys(DataRelation columnofquerys) {
		this.columnofquerys = columnofquerys;
	}

}
