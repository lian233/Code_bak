package com.wofu.netshop.common.fenxiao.entity;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class CustomerDeliveryNumBook extends BusinessObject{
	private int id;
	private Date begingroutetime;
	private Date endroutetime;
	private int routespan;
	private String position;
	private String problem;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getBegingroutetime() {
		return begingroutetime;
	}
	public void setBegingroutetime(Date begingroutetime) {
		this.begingroutetime = begingroutetime;
	}
	public Date getEndroutetime() {
		return endroutetime;
	}
	public void setEndroutetime(Date endroutetime) {
		this.endroutetime = endroutetime;
	}
	public int getRoutespan() {
		return routespan;
	}
	public void setRoutespan(int routespan) {
		this.routespan = routespan;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getProblem() {
		return problem;
	}
	public void setProblem(String problem) {
		this.problem = problem;
	}
	
}
