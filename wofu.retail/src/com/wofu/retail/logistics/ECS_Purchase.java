package com.wofu.retail.logistics;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class ECS_Purchase extends BusinessObject {
	private int pid;
	private String purchasecode;
	private String refcode;
	private int orgid;
	private int supplierid;
	private int paytypeid;
	private Date delivedate;
	private int purday;
	private int status;
	private int flag;
	private String creator;
	private Date createtime;
	private String operator;
	private String updator;
	private Date updatetime;
	private String notes;

	public int getPid() {
		return pid;	
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getPurchasecode() {
		return purchasecode;	
	}
	public void setPurchasecode(String purchasecode) {
		this.purchasecode = purchasecode;
	}
	public String getRefcode() {
		return refcode;	
	}
	public void setRefcode(String refcode) {
		this.refcode = refcode;
	}
	public int getOrgid() {
		return orgid;	
	}
	public void setOrgid(int orgid) {
		this.orgid = orgid;
	}
	public int getSupplierid() {
		return supplierid;	
	}
	public void setSupplierid(int supplierid) {
		this.supplierid = supplierid;
	}
	public int getPaytypeid() {
		return paytypeid;	
	}
	public void setPaytypeid(int paytypeid) {
		this.paytypeid = paytypeid;
	}
	public Date getDelivedate() {
		return delivedate;	
	}
	public void setDelivedate(Date delivedate) {
		this.delivedate = delivedate;
	}
	public int getPurday() {
		return purday;	
	}
	public void setPurday(int purday) {
		this.purday = purday;
	}
	public int getStatus() {
		return status;	
	}
	public void setStatus(int status) {
		this.status = status;
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
	public String getOperator() {
		return operator;	
	}
	public void setOperator(String operator) {
		this.operator = operator;
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
	public String getNotes() {
		return notes;	
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}

}
