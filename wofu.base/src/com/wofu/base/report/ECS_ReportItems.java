package com.wofu.base.report;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class ECS_ReportItems extends BusinessObject{
	private int itemid;
	private String name;
	private int folderid;
	private int flag;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	
	
	public int getItemid() {
		return itemid;	
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public String getName() {
		return name;	
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFolderid() {
		return folderid;	
	}
	public void setFolderid(int folderid) {
		this.folderid = folderid;
	}
	public int getFlag() {
		return flag;	
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getCreator() {
		return creator;	
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreatetime() {
		return createtime;	
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getUpdator() {
		return updator;	
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}
	public Date getUpdatetime() {
		return updatetime;	
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

}
