package com.wofu.ecommerce.purchasemanager;


import java.util.Date;
import java.util.Properties;

import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.StringUtil;

import com.wofu.base.interfacemanager.IntfHelper;

public class ECS_Ret extends PageBusinessObject {
	
	private int rid;
	private String retcode;
	private int supplierid;
	private int orgid;
	private int badflag;
	private Date retdate;
	private int flag;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private String notes;
	private int merchantid;
	
	private DataRelation retitemofrets =new DataRelation("retitemofret","com.wofu.ecommerce.purchasemanager.ECS_RetItem");
	
	public ECS_Ret()
	{			
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="";
		this.exportQuerySQL="";
	}
	
	public void select() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String rid=prop.getProperty("rid");

		
		String sql="select * from ecs_ret with(nolock) where rid="+rid;
		this.getMapData(this.getDao().oneRowSelect(sql));
		

		sql="select * from ecs_retitem with(nolock) where rid="+rid;
		this.retitemofrets.setRelationData(this.getDao().multiRowSelect(sql));
	

		String s=this.toJSONObject();

		this.OutputStr(s);
	}
		
	public void insert() throws Exception
	{
		this.getJSONData();
		this.rid=this.getDao().IDGenerator(this, "rid");
		this.creator=this.getUserInfo().getLogin();
		this.createtime=new Date(System.currentTimeMillis());
		this.updatetime=new Date(System.currentTimeMillis());
		this.retdate=new Date(System.currentTimeMillis());
		this.updator=this.getUserInfo().getLogin();
		this.flag=0;

		this.merchantid=this.getUserInfo().getMerchantid();

		this.retcode=this.getDao().BusiCodeGenerator(this.getUserInfo().getMerchantid(), this, "retcode");
		
		this.getDao().insert(this);

		for (int i=0;i<this.retitemofrets.getRelationData().size();i++)
		{
			ECS_RetItem retitem=(ECS_RetItem) this.retitemofrets.getRelationData().get(i);
			retitem.setRid(this.rid);
			this.getDao().insert(retitem);
		}

		String sql="select * from ecs_ret where rid="+this.rid;
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	
	public void update() throws Exception
	{

		this.getJSONData();

		this.updatetime=new Date(System.currentTimeMillis());
		this.updator=this.getUserInfo().getLogin();	

		this.getDao().update(this);

		
		String sql="delete from ECS_RetItem where rid="+this.rid;
		this.getDao().execute(sql);
		
		for (int i=0;i<this.retitemofrets.getRelationData().size();i++)
		{
			ECS_RetItem retitem=(ECS_RetItem) this.retitemofrets.getRelationData().get(i);			
			retitem.setRid(this.rid);			
			this.getDao().insert(retitem);
		}
	
		sql="select * from ecs_ret where rid="+this.rid;
		
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	
	public void deleteBill() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String rid=prop.getProperty("rid");
		
		this.rid=Integer.valueOf(rid).intValue();
		
		this.flag=95;
		
		this.getDao().update(this, "flag");
	}
	
	public void checkBill() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String rid=prop.getProperty("rid");
		
		String sql="select * from ecs_ret where rid="+rid;
		this.getMapData(this.getDao().oneRowSelect(sql));
		
		this.flag=1;
		this.getDao().update(this);
		
		
		IntfHelper.setInterfaceSheetList(this.getDao(), this.orgid, this.rid, 2302);
		
		sql="select * from ecs_ret where rid="+this.rid;
		
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	
	public void getRetReason() throws Exception
	{
		String sql="select reasonid,name from ecs_retreason order by reasonid";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
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



	public int getBadflag() {
		return badflag;
	}

	public void setBadflag(int badflag) {
		this.badflag = badflag;
	}

	public String getRetcode() {
		return retcode;
	}

	public void setRetcode(String retcode) {
		this.retcode = retcode;
	}

	public Date getRetdate() {
		return retdate;
	}

	public void setRetdate(Date retdate) {
		this.retdate = retdate;
	}

	public DataRelation getRetitemofrets() {
		return retitemofrets;
	}

	public void setRetitemofrets(DataRelation retitemofrets) {
		this.retitemofrets = retitemofrets;
	}

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public DataRelation getretitemofrets() {
		return retitemofrets;
	}

	public void setretitemofrets(DataRelation retitemofrets) {
		this.retitemofrets = retitemofrets;
	}

	public int getOrgid() {
		return orgid;
	}

	public void setOrgid(int orgid) {
		this.orgid = orgid;
	}


	public String getretcode() {
		return retcode;
	}

	public void setretcode(String retcode) {
		this.retcode = retcode;
	}


	public int getSupplierid() {
		return supplierid;
	}

	public void setSupplierid(int supplierid) {
		this.supplierid = supplierid;
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

