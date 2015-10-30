package com.wofu.retail.baseinformation;

import java.util.ArrayList;
import java.util.Date;

import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class ECS_Category extends PageBusinessObject {
	private int catid;
	private String customid;
	private String name;
	private String note;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private int parentcatid;
	private int catlevel;
	private int merchantid;
	
	private DataRelation ecscategoryofecscategorys =new DataRelation("ecscategoryofecscategory","com.wofu.retail.baseinformation.ECS_Category");
	
	public ECS_Category()
	{
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="name";
	}
	public void delete() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecscategoryofecscategorys.getRelationData().size();i++)
		{
			ECS_Category category=(ECS_Category) this.ecscategoryofecscategorys.getRelationData().get(i);
			
			String sql="select count(*) ecs_category where parentcatid="+category.getCatid();
			if (this.getDao().intSelect(sql)>0)
				throw new JException("请先删除分类【"+category.getName()+"】的子分类");
			
			sql="select count(*) from ecs_item where catid="+category.getCatid();
			if (this.getDao().intSelect(sql)>0)
				throw new JException("分类:【"+category.getName()+"】已被使用,不能删除!");
			
			this.getDao().delete(category);
		}
	}
	
	public void insert() throws Exception
	{
			
		this.getJSONData();

		ArrayList list=new ArrayList();
		for (int i=0;i<this.ecscategoryofecscategorys.getRelationData().size();i++)
		{
			ECS_Category category=(ECS_Category) this.ecscategoryofecscategorys.getRelationData().get(i);
			
			//检查唯一性
			this.checkUnique(this);

			
			category.catid=this.getDao().IDGenerator(category, "catid");
			category.creator=this.getUserInfo().getName();
			category.createtime=new Date();			
			category.updator=this.getUserInfo().getName();
			category.updatetime=new Date();
			category.merchantid=this.getUserInfo().getMerchantid();
			this.getDao().insert(category);
			list.add(category);
			
		}
		this.OutputStr(this.toJSONArray(list));
	
	}
	public void update() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecscategoryofecscategorys.getRelationData().size();i++)
		{
			ECS_Category category=(ECS_Category) this.ecscategoryofecscategorys.getRelationData().get(i);
			
			//检查唯一性
			this.checkUnique(this);
			
			category.updator=this.getUserInfo().getLogin();
			category.updatetime=new Date();
			this.getDao().update(category);
		}
	}
	
	public void getCats() throws Exception
	{
	
		String json=this.getJSONTree("ecs_category", "catid", "name", "parentcatid", "0"," and catid<>0");
		Log.info("ctasDate: "+json);
		this.OutputStr(json);
	}

	public int getCatid() {
		return catid;	
	}
	public void setCatid(int catid) {
		this.catid = catid;
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
	public int getParentcatid() {
		return parentcatid;	
	}
	public void setParentcatid(int parentcatid) {
		this.parentcatid = parentcatid;
	}
	public int getCatlevel() {
		return catlevel;	
	}
	public void setCatlevel(int catlevel) {
		this.catlevel = catlevel;
	}
	public int getMerchantid() {
		return merchantid;	
	}
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}
}
