package com.wofu.base.report;

import com.wofu.base.util.BusinessObject;

public class ECS_ReportItemCond extends BusinessObject {
	private int condid;
	private int itemid;
	private String title;
	private int type;
	private String queryfield;
	private int logistic;
	private String defaultvalue;
	private int ismust;
	private String renderer;
	private String replaceexpression;
	private String applyquery;
	private int width;

	public int getItemid() {
		return itemid;	
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public String getTitle() {
		return title;	
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getType() {
		return type;	
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getQueryfield() {
		return queryfield;	
	}
	public void setQueryfield(String queryfield) {
		this.queryfield = queryfield;
	}
	public int getLogistic() {
		return logistic;	
	}
	public void setLogistic(int logistic) {
		this.logistic = logistic;
	}
	public String getDefaultvalue() {
		return defaultvalue;	
	}
	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}
	public int getIsmust() {
		return ismust;	
	}
	public void setIsmust(int ismust) {
		this.ismust = ismust;
	}
	public String getRenderer() {
		return renderer;	
	}
	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}
	public String getReplaceexpression() {
		return replaceexpression;	
	}
	public void setReplaceexpression(String replaceexpression) {
		this.replaceexpression = replaceexpression;
	}
	public String getApplyquery() {
		return applyquery;	
	}
	public void setApplyquery(String applyquery) {
		this.applyquery = applyquery;
	}
	public int getWidth() {
		return width;	
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getCondid() {
		return condid;
	}
	public void setCondid(int condid) {
		this.condid = condid;
	}

}
