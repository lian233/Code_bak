package com.wofu.retail.baseinformation;

import java.util.Date;

import com.wofu.base.util.PageBusinessObject;

public class ECS_Kind extends PageBusinessObject {
	private int kindid;
	private String customid;
	private String name;
	private String note;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private int merchantid;

	public int getKindid() {
		return kindid;	
	}
	public void setKindid(int kindid) {
		this.kindid = kindid;
	}
	public String getCustomid() {
		return customid;	
	}
	public void setCustomid(String customid) {
		this.customid = customid;
	}
	public String getName() {
		return name;	
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNote() {
		return note;	
	}
	public void setNote(String note) {
		this.note = note;
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
	public int getMerchantid() {
		return merchantid;	
	}
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}
}
