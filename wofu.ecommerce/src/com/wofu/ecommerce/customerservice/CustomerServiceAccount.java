package com.wofu.ecommerce.customerservice;

import java.util.Properties;

import com.wofu.base.util.BusinessObject;
import com.wofu.common.tools.conv.Convert;
import com.wofu.common.tools.util.StringUtil;

public class CustomerServiceAccount extends BusinessObject {
	
	private int tradecontactid;
	private String usernick;
	private String fullname;
	private int roletype;
	private int enabled;
	
	
	private void doUpdate() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String tradecontactid=prop.getProperty("tradecontactid");
		String usernick=prop.getProperty("usernick");
		String fullname=prop.getProperty("fullname");
		String roletype=prop.getProperty("roletype");
		String enabled=prop.getProperty("enabled");
		
		this.tradecontactid=Integer.valueOf(tradecontactid).intValue();
		this.usernick=usernick;
		this.fullname=fullname;
		this.roletype=Integer.valueOf(roletype).intValue();
		this.enabled=Integer.valueOf(enabled).intValue();
		
		this.getDao().update(this, "fullname,roletype,enabled");
	}
	private void doDelete() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String tradecontactid=prop.getProperty("tradecontactid");
		String usernick=prop.getProperty("usernick");
		
		this.getDao().delete(this, "where tradecontactid="+tradecontactid+" and usernick='"+usernick+"'");
	}
	private void doInsert() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String tradecontactid=prop.getProperty("tradecontactid");
		String usernick=prop.getProperty("usernick");
		String fullname=prop.getProperty("fullname");
		String roletype=prop.getProperty("roletype");
		String enabled=prop.getProperty("enabled");
		
		this.tradecontactid=Integer.valueOf(tradecontactid).intValue();
		this.usernick=usernick;
		this.fullname=fullname;
		this.roletype=Integer.valueOf(roletype).intValue();
		this.enabled=Integer.valueOf(enabled).intValue();
		
		this.getDao().insert(this);
	}
	private void getCSAccount() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);
		String tradecontactid=prop.getProperty("tradecontactid");
		String sql="select tradecontactid,usernick,fullname,roletype "
			+"from CustomerServiceAccount with(nolock) where tradecontactid="+tradecontactid+" and roletype=0";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	private void doSearch() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);
		String tradecontactid=prop.getProperty("tradecontactid");
		String sql="select * from CustomerServiceAccount with(nolock) where tradecontactid="+tradecontactid;
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	public void doTransaction(String action) throws Exception {
		
		if (action.equalsIgnoreCase("getcsaccount"))
			getCSAccount();
		if (action.equalsIgnoreCase("delete"))
			doDelete();
		if (action.equalsIgnoreCase("insert"))
			doInsert();
		if (action.equalsIgnoreCase("update"))
			doUpdate();
		if (action.equalsIgnoreCase("search"))
			doSearch();
	}
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public int getRoletype() {
		return roletype;
	}
	public void setRoletype(int roletype) {
		this.roletype = roletype;
	}
	public int getTradecontactid() {
		return tradecontactid;
	}
	public void setTradecontactid(int tradecontactid) {
		this.tradecontactid = tradecontactid;
	}
	public String getUsernick() {
		return usernick;
	}
	public void setUsernick(String usernick) {
		this.usernick = usernick;
	}

}
