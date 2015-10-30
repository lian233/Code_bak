package com.wofu.retail.baseinformation;

import com.wofu.base.util.BusinessObject;

public class ECS_ItemImage extends BusinessObject {
	
	private int itemid;
	private int fileid;
	private String description;
	private int flag;
	
	public int getFileid() {
		return fileid;
	}
	public void setFileid(int fileid) {
		this.fileid = fileid;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getItemid() {
		return itemid;
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
