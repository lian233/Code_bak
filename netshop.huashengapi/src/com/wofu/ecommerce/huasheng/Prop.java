package com.wofu.ecommerce.huasheng;

import com.wofu.base.util.BusinessObject;

public class Prop extends BusinessObject {
	private String name="";
	private String value="";
	private int is_show;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getIs_show() {
		return is_show;
	}
	public void setIs_show(int is_show) {
		this.is_show = is_show;
	}
	
}
