package com.wofu.ecommerce.lenovo;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class Service extends BusinessObject{
	private String service_nickname="";
	private String comment="";
	private Date ctime;
	public String getService_nickname() {
		return service_nickname;
	}
	public void setService_nickname(String service_nickname) {
		this.service_nickname = service_nickname;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getCtime() {
		return ctime;
	}
	public void setCtime(Date ctime) {
		this.ctime = ctime;
	} 
	

}
