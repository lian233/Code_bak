package com.wofu.base.interfacemanager;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class ECS_DownNote extends BusinessObject {

	private int noteid;
	private String owner;
	private String ownerid;
	private int bustype;
	private Date notetime;
	private Date handletime;
	private int flag;
	
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getOwnerid() {
		return ownerid;
	}
	public void setOwnerid(String ownerid) {
		this.ownerid = ownerid;
	}
	public void backup() throws Exception
	{
		ECS_DownNoteBak ecs_downnotebak=new ECS_DownNoteBak();
		this.copyTo(ecs_downnotebak);
		ecs_downnotebak.setHandletime(new Date());
		this.getDao().insert(ecs_downnotebak);
		this.getDao().delete(this);
	}
	public void doTransaction(String action) throws Exception {
		
		
	}

	public Date getHandletime() {
		return handletime;
	}
	public void setHandletime(Date handletime) {
		this.handletime = handletime;
	}
	public Date getNotetime() {
		return notetime;
	}
	public void setNotetime(Date notetime) {
		this.notetime = notetime;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public int getNoteid() {
		return noteid;
	}
	public void setNoteid(int noteid) {
		this.noteid = noteid;
	}
	public int getBustype() {
		return bustype;
	}
	public void setBustype(int bustype) {
		this.bustype = bustype;
	}


}
