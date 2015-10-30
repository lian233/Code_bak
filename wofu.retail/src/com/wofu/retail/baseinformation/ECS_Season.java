package com.wofu.retail.baseinformation;

import java.util.ArrayList;
import java.util.Date;

import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.JException;

public class ECS_Season extends PageBusinessObject {
	private int seasonid;
	private String customid;
	private String name;
	private String note;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private int merchantid;

	private DataRelation ecsseasonofecsseasons =new DataRelation("ecsseasonofecsseason","com.wofu.retail.baseinformation.ECS_Season");
	
	
	public ECS_Season()
	{
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="name";
	}
	public void delete() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecsseasonofecsseasons.getRelationData().size();i++)
		{
			ECS_Season season=(ECS_Season) this.ecsseasonofecsseasons.getRelationData().get(i);
						
			String sql="select count(*) from ecs_item where seasonid="+season.getSeasonid();
			if (this.getDao().intSelect(sql)>0)
				throw new JException("季节:【"+season.getName()+"】已被使用,不能删除!");
			
			this.getDao().delete(season);
		}
	}
	
	public void insert() throws Exception
	{
			
		this.getJSONData();

		ArrayList list=new ArrayList();
		for (int i=0;i<this.ecsseasonofecsseasons.getRelationData().size();i++)
		{
			ECS_Season season=(ECS_Season) this.ecsseasonofecsseasons.getRelationData().get(i);
						
			//检查唯一性
			this.checkUnique(this);

			
			season.seasonid=this.getDao().IDGenerator(season, "seasonid");
			season.creator=this.getUserInfo().getName();
			season.createtime=new Date();			
			season.updator=this.getUserInfo().getName();
			season.updatetime=new Date();
			season.note=season.name;
			season.merchantid=this.getUserInfo().getMerchantid();
			this.getDao().insert(season);
			list.add(season);			
		}
		this.OutputStr(this.toJSONArray(list));
	
	}
	public void update() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecsseasonofecsseasons.getRelationData().size();i++)
		{
			ECS_Season season=(ECS_Season) this.ecsseasonofecsseasons.getRelationData().get(i);
						
			//检查唯一性
			this.checkUnique(this);
			
			season.updator=this.getUserInfo().getLogin();
			season.updatetime=new Date();
			this.getDao().update(season);
		}
	}
	
	
	public int getSeasonid() {
		return seasonid;	
	}
	public void setSeasonid(int seasonid) {
		this.seasonid = seasonid;
	}
	public String getCustomid() {
		return customid;	
	}
	public void setCustomid(String customid) {
		this.customid = customid;
	}
	public String getName() {
		return name;	
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNote() {
		return note;	
	}
	public void setNote(String note) {
		this.note = note;
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
	public int getMerchantid() {
		return merchantid;	
	}
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}
	public DataRelation getEcsseasonofecsseasons() {
		return ecsseasonofecsseasons;
	}
	public void setEcsseasonofecsseasons(DataRelation ecsseasonofecsseasons) {
		this.ecsseasonofecsseasons = ecsseasonofecsseasons;
	}
}
