package com.wofu.ecommerce.stockmanager;

import java.util.ArrayList;
import java.util.Date;

import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;

public class ECS_RationConfig extends PageBusinessObject {

	private int serialid;
	private int shoporgid;
	private int rationorgid;
	private double synstockrate;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;;
	
	private DataRelation ecsrationconfigofecsrationconfigs =new DataRelation("ecsrationconfigofecsrationconfig","com.wofu.ecommerce.stockmanager.ECS_RationConfig");

	public void delete() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecsrationconfigofecsrationconfigs.getRelationData().size();i++)
		{
			ECS_RationConfig rationconfig=(ECS_RationConfig) this.ecsrationconfigofecsrationconfigs.getRelationData().get(i);
		this.getDao().delete(rationconfig);
		}
	}
	
	public void insert() throws Exception
	{
			
		this.getJSONData();

		ArrayList list=new ArrayList();
		for (int i=0;i<this.ecsrationconfigofecsrationconfigs.getRelationData().size();i++)
		{
			ECS_RationConfig rationconfig=(ECS_RationConfig) this.ecsrationconfigofecsrationconfigs.getRelationData().get(i);
						
			//检查唯一性
			this.checkUnique(this);

			
			rationconfig.serialid=this.getDao().IDGenerator(rationconfig, "serialid");
			rationconfig.creator=this.getUserInfo().getName();
			rationconfig.createtime=new Date();			
			rationconfig.updator=this.getUserInfo().getName();
			rationconfig.updatetime=new Date();
			this.getDao().insert(rationconfig);
			list.add(rationconfig);			
		}
		this.OutputStr(this.toJSONArray(list));
	
	}
	public void update() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecsrationconfigofecsrationconfigs.getRelationData().size();i++)
		{
			ECS_RationConfig rationconfig=(ECS_RationConfig) this.ecsrationconfigofecsrationconfigs.getRelationData().get(i);
						
			//检查唯一性
			this.checkUnique(this);
			
			rationconfig.updator=this.getUserInfo().getLogin();
			rationconfig.updatetime=new Date();
			this.getDao().update(rationconfig);
		}
	}
	
	
	public int getSerialid() {
		return serialid;
	}

	public void setSerialid(int serialid) {
		this.serialid = serialid;
	}

	public int getShoporgid() {
		return shoporgid;
	}

	public void setShoporgid(int shoporgid) {
		this.shoporgid = shoporgid;
	}

	public int getRationorgid() {
		return rationorgid;
	}

	public void setRationorgid(int rationorgid) {
		this.rationorgid = rationorgid;
	}

	public double getSynstockrate() {
		return synstockrate;
	}

	public void setSynstockrate(double synstockrate) {
		this.synstockrate = synstockrate;
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

	public DataRelation getEcsrationconfigofecsrationconfigs() {
		return ecsrationconfigofecsrationconfigs;
	}

	public void setEcsrationconfigofecsrationconfigs(
			DataRelation ecsrationconfigofecsrationconfigs) {
		this.ecsrationconfigofecsrationconfigs = ecsrationconfigofecsrationconfigs;
	}

	
}
