package com.wofu.base.job.timer;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class ECS_TimerPolicy extends BusinessObject {
	  private int id;
	  private int active;
	  private String clock;	
	  private int clocktype;
	  private Date lastactive;
	  private Date nextactive;
	  private String executer;
	  private String params;
	  private String notes;	 
	  private int activetimes;
	  private int errorcount;
	  private String errormessage;
	  private int maxretry;	  
	  private int skip;
	  private int dsid;
	  private int flag;
	  private String groupname ;
	  
	public int getDsid() {
		return dsid;
	}
	public void setDsid(int dsid) {
		this.dsid = dsid;
	}

	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public int getActivetimes() {
		return activetimes;
	}
	public void setActivetimes(int activetimes) {
		this.activetimes = activetimes;
	}
	public String getClock() {
		return clock;
	}
	public void setClock(String clock) {
		this.clock = clock;
	}
	public int getClocktype() {
		return clocktype;
	}
	public void setClocktype(int clocktype) {
		this.clocktype = clocktype;
	}
	public int getErrorcount() {
		return errorcount;
	}
	public void setErrorcount(int errorcount) {
		this.errorcount = errorcount;
	}
	public String getErrormessage() {
		return errormessage;
	}
	public void setErrormessage(String errormessage) {
		this.errormessage = errormessage;
	}
	public String getExecuter() {
		return executer;
	}
	public void setExecuter(String executer) {
		this.executer = executer;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getLastactive() {
		return lastactive;
	}
	public void setLastactive(Date lastactive) {
		this.lastactive = lastactive;
	}
	public int getMaxretry() {
		return maxretry;
	}
	public void setMaxretry(int maxretry) {
		this.maxretry = maxretry;
	}
	public Date getNextactive() {
		return nextactive;
	}
	public void setNextactive(Date nextactive) {
		this.nextactive = nextactive;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public int getSkip() {
		return skip;
	}
	public void setSkip(int skip) {
		this.skip = skip;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public String getGroupname() {
		return groupname;
	}
	
}
