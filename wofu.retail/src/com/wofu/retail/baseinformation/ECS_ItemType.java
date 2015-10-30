package com.wofu.retail.baseinformation;


import java.util.Date;

import com.wofu.base.util.PageBusinessObject;

public class ECS_ItemType extends PageBusinessObject {

	private int id;
	private String customid;
	private int barcodehead;
	private String initflag;
	private String name;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private int merchantid;

	public int getMerchantid() {
		return merchantid;
	}
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}
	public int getId() {
		return id;	
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBarcodehead() {
		return barcodehead;	
	}
	public void setBarcodehead(int barcodehead) {
		this.barcodehead = barcodehead;
	}
	public String getInitflag() {
		return initflag;	
	}
	public void setInitflag(String initflag) {
		this.initflag = initflag;
	}
	public String getName() {
		return name;	
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCustomid() {
		return customid;
	}
	public void setCustomid(String customid) {
		this.customid = customid;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getUpdator() {
		return updator;
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}

}
