package com.wofu.base.report;

import com.wofu.base.util.BusinessObject;

public class ECS_ReportItemQueryColumn extends BusinessObject {
	private int colid;
	private int queryid;
	private int itemid;
	private String fieldname;
	private String title;
	private int width;
	private String format;
	private String renderer;
	private int grouptype;
	private String groupfield;
	private int sumtype;
	private String sumfield;
	private int displayflag;

	public int getQueryid() {
		return queryid;	
	}
	public void setQueryid(int queryid) {
		this.queryid = queryid;
	}
	public String getFieldname() {
		return fieldname;	
	}
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	public String getTitle() {
		return title;	
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getWidth() {
		return width;	
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getFormat() {
		return format;	
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getRenderer() {
		return renderer;	
	}
	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}
	public int getGrouptype() {
		return grouptype;	
	}
	public void setGrouptype(int grouptype) {
		this.grouptype = grouptype;
	}
	public String getGroupfield() {
		return groupfield;	
	}
	public void setGroupfield(String groupfield) {
		this.groupfield = groupfield;
	}
	public int getSumtype() {
		return sumtype;	
	}
	public void setSumtype(int sumtype) {
		this.sumtype = sumtype;
	}
	public String getSumfield() {
		return sumfield;	
	}
	public void setSumfield(String sumfield) {
		this.sumfield = sumfield;
	}
	public int getDisplayflag() {
		return displayflag;	
	}
	public void setDisplayflag(int displayflag) {
		this.displayflag = displayflag;
	}
	public int getColid() {
		return colid;
	}
	public void setColid(int colid) {
		this.colid = colid;
	}
	public int getItemid() {
		return itemid;
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}


}
