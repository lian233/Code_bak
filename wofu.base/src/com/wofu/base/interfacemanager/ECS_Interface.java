package com.wofu.base.interfacemanager;

import com.wofu.base.util.BusinessObject;

public class ECS_Interface extends BusinessObject {

	private int interfaceid;
	private String interfacecode;
	private String interfacename;
	private String vertifycode;
	private int orgid;
	private int extinterfacestyle;
	private int extinterfaceid;
	private int enable;
	
	public void doTransaction(String action) throws Exception {
		

	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public int getExtinterfaceid() {
		return extinterfaceid;
	}

	public void setExtinterfaceid(int extinterfaceid) {
		this.extinterfaceid = extinterfaceid;
	}

	public int getExtinterfacestyle() {
		return extinterfacestyle;
	}

	public void setExtinterfacestyle(int extinterfacestyle) {
		this.extinterfacestyle = extinterfacestyle;
	}

	public String getInterfacecode() {
		return interfacecode;
	}

	public void setInterfacecode(String interfacecode) {
		this.interfacecode = interfacecode;
	}

	public int getInterfaceid() {
		return interfaceid;
	}

	public void setInterfaceid(int interfaceid) {
		this.interfaceid = interfaceid;
	}

	public String getInterfacename() {
		return interfacename;
	}

	public void setInterfacename(String interfacename) {
		this.interfacename = interfacename;
	}

	public int getOrgid() {
		return orgid;
	}

	public void setOrgid(int orgid) {
		this.orgid = orgid;
	}

	public String getVertifycode() {
		return vertifycode;
	}

	public void setVertifycode(String vertifycode) {
		this.vertifycode = vertifycode;
	}

}
