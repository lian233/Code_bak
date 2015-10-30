package com.wofu.retail.baseinformation;

import java.util.ArrayList;
import java.util.Date;

import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.JException;

public class ECS_Color extends PageBusinessObject {
	
	private int colorid;
	private String customid;
	private String shortname;
	private String name;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private int merchantid;
	

	private DataRelation ecscolorofecscolors =new DataRelation("ecscolorofecscolor","com.wofu.retail.baseinformation.ECS_color");
	
	
	public ECS_Color()
	{
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="name";
	}
	public void delete() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecscolorofecscolors.getRelationData().size();i++)
		{
			ECS_Color color=(ECS_Color) this.ecscolorofecscolors.getRelationData().get(i);
						
			String sql="select count(*) from ecs_itemsku where colorid="+color.getColorid();
			if (this.getDao().intSelect(sql)>0)
				throw new JException("颜色:【"+color.getName()+"】已被使用,不能删除!");
			
			this.getDao().delete(color);
		}
	}
	
	public void insert() throws Exception
	{
			
		this.getJSONData();

		ArrayList list=new ArrayList();
		for (int i=0;i<this.ecscolorofecscolors.getRelationData().size();i++)
		{
			ECS_Color color=(ECS_Color) this.ecscolorofecscolors.getRelationData().get(i);
						
			//检查唯一性
			this.checkUnique(this);

			
			color.colorid=this.getDao().IDGenerator(color, "colorid");
			color.creator=this.getUserInfo().getName();
			color.createtime=new Date();			
			color.updator=this.getUserInfo().getName();
			color.updatetime=new Date();
			color.merchantid=this.getUserInfo().getMerchantid();
			this.getDao().insert(color);
			list.add(color);			
		}
		this.OutputStr(this.toJSONArray(list));
	
	}
	public void update() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecscolorofecscolors.getRelationData().size();i++)
		{
			ECS_Color color=(ECS_Color) this.ecscolorofecscolors.getRelationData().get(i);
						
			//检查唯一性
			this.checkUnique(this);
			
			color.updator=this.getUserInfo().getLogin();
			color.updatetime=new Date();
			this.getDao().update(color);
		}
	}
	
	
	public int getColorid() {
		return colorid;
	}
	public void setColorid(int colorid) {
		this.colorid = colorid;
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
	public String getCustomid() {
		return customid;
	}
	public void setCustomid(String customid) {
		this.customid = customid;
	}
	public int getMerchantid() {
		return merchantid;
	}
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
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
