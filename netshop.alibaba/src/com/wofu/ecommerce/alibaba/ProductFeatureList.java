package com.wofu.ecommerce.alibaba;

import com.wofu.base.util.BusinessObject;

public class ProductFeatureList extends BusinessObject {
	private long fid;
	private String name;
	private String value;
	private String values;
	
	public ProductFeatureList(){
		this.fid=1;
		this.name="";
		this.value="";
	}
	public long getFid() {
		return fid;
	}
	public void setFid(long fid) {
		this.fid = fid;
	}
	
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
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}
	
}
