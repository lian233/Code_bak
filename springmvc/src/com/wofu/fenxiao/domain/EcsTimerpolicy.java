package com.wofu.fenxiao.domain;

import java.util.Date;

/**
 * 作业配置
 * @author Administrator
 *
 */
public class EcsTimerpolicy {
	private int id;
	private int active;
	private String clock;
	private int clocktype;
	private Date lastActive;
	private Date nextActive;
	private String executer;
	private String params;
	private String notes;
	private int activeTimes;
	private int ErrorCount;
	private String ErrorMessage;
	private int MaxRetry;
	private int Skip;
	private int dsid;
	private int flag;
	private String groupname;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
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
	public Date getLastActive() {
		return lastActive;
	}
	public void setLastActive(Date lastActive) {
		this.lastActive = lastActive;
	}
	public Date getNextActive() {
		return nextActive;
	}
	public void setNextActive(Date nextActive) {
		this.nextActive = nextActive;
	}
	public String getExecuter() {
		return executer;
	}
	public void setExecuter(String executer) {
		this.executer = executer;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public int getActiveTimes() {
		return activeTimes;
	}
	public void setActiveTimes(int activeTimes) {
		this.activeTimes = activeTimes;
	}
	public int getErrorCount() {
		return ErrorCount;
	}
	public void setErrorCount(int errorCount) {
		ErrorCount = errorCount;
	}
	public String getErrorMessage() {
		return ErrorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}
	public int getMaxRetry() {
		return MaxRetry;
	}
	public void setMaxRetry(int maxRetry) {
		MaxRetry = maxRetry;
	}
	public int getSkip() {
		return Skip;
	}
	public void setSkip(int skip) {
		Skip = skip;
	}
	public int getDsid() {
		return dsid;
	}
	public void setDsid(int dsid) {
		this.dsid = dsid;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	
	
}
