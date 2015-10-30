package com.wofu.retail.baseinformation;

import java.util.ArrayList;
import java.util.Date;


import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.JException;

public class ECS_Size extends PageBusinessObject {
	private int sizeid;
	private String customid;
	private String shortname;
	private String name;
	private int measuretypeid;
	private String custommeasuretypeid;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private int merchantid;
	

	private DataRelation ecssizeofecssizes =new DataRelation("ecssizeofecssize","com.wofu.retail.baseinformation.ECS_size");
	
	
	public ECS_Size()
	{
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="name";
	}
	public void delete() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecssizeofecssizes.getRelationData().size();i++)
		{
			ECS_Size size=(ECS_Size) this.ecssizeofecssizes.getRelationData().get(i);
						
			String sql="select count(*) from ecs_itemsku where sizeid="+size.getSizeid();
			if (this.getDao().intSelect(sql)>0)
				throw new JException("品牌:【"+size.getName()+"】已被使用,不能删除!");
			
			this.getDao().delete(size);
		}
	}
	
	public void insert() throws Exception
	{
			
		this.getJSONData();

		ArrayList list=new ArrayList();
		for (int i=0;i<this.ecssizeofecssizes.getRelationData().size();i++)
		{
			ECS_Size size=(ECS_Size) this.ecssizeofecssizes.getRelationData().get(i);
						
			//检查唯一性
			this.checkUnique(this);

			
			size.sizeid=this.getDao().IDGenerator(size, "sizeid");
			size.creator=this.getUserInfo().getName();
			size.createtime=new Date();			
			size.updator=this.getUserInfo().getName();
			size.updatetime=new Date();
			size.merchantid=this.getUserInfo().getMerchantid();
			this.getDao().insert(size);
			list.add(size);			
		}
		this.OutputStr(this.toJSONArray(list));
	
	}
	public void update() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecssizeofecssizes.getRelationData().size();i++)
		{
			ECS_Size size=(ECS_Size) this.ecssizeofecssizes.getRelationData().get(i);
						
			//检查唯一性
			this.checkUnique(this);
			
			size.updator=this.getUserInfo().getLogin();
			size.updatetime=new Date();
			this.getDao().update(size);
		}
	}
	

	public int getSizeid() {
		return sizeid;	
	}
	public void setSizeid(int sizeid) {
		this.sizeid = sizeid;
	}
	public String getCustomid() {
		return customid;	
	}
	public void setCustomid(String customid) {
		this.customid = customid;
	}
	public String getShortname() {
		return shortname;	
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public String getName() {
		return name;	
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMeasuretypeid() {
		return measuretypeid;	
	}
	public void setMeasuretypeid(int measuretypeid) {
		this.measuretypeid = measuretypeid;
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
	public String getCustommeasuretypeid() {
		return custommeasuretypeid;
	}
	public void setCustommeasuretypeid(String custommeasuretypeid) {
		this.custommeasuretypeid = custommeasuretypeid;
	}
}
