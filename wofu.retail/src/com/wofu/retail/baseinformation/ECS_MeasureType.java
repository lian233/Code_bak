package com.wofu.retail.baseinformation;

import java.util.ArrayList;
import java.util.Date;

import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.JException;

public class ECS_MeasureType extends PageBusinessObject {
	private int id;
	private String customid;
	private String name;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private int merchantid;

	private DataRelation ecsmeasuretypeofecsmeasuretypes =new DataRelation("ecsmeasuretypeofecsmeasuretype","com.wofu.retail.baseinformation.ECS_measuretype");
	
	
	public ECS_MeasureType()
	{
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="name";
	}
	public void delete() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecsmeasuretypeofecsmeasuretypes.getRelationData().size();i++)
		{
			ECS_MeasureType measuretype=(ECS_MeasureType) this.ecsmeasuretypeofecsmeasuretypes.getRelationData().get(i);
						
			String sql="select count(*) from ecs_item where measuretype="+measuretype.getId();
			if (this.getDao().intSelect(sql)>0)
				throw new JException("度量类型:【"+measuretype.getName()+"】已被使用,不能删除!");
			
			sql="select count(*) from ecs_size where measuretypeid="+measuretype.getId();
			if (this.getDao().intSelect(sql)>0)
				throw new JException("度量类型:【"+measuretype.getName()+"】已被使用,不能删除!");
			
			this.getDao().delete(measuretype);
		}
	}
	
	public void insert() throws Exception
	{
			
		this.getJSONData();

		ArrayList list=new ArrayList();
		for (int i=0;i<this.ecsmeasuretypeofecsmeasuretypes.getRelationData().size();i++)
		{
			ECS_MeasureType measuretype=(ECS_MeasureType) this.ecsmeasuretypeofecsmeasuretypes.getRelationData().get(i);
						
			//检查唯一性
			this.checkUnique(this);

			
			measuretype.id=this.getDao().IDGenerator(measuretype, "id");
			measuretype.creator=this.getUserInfo().getName();
			measuretype.createtime=new Date();			
			measuretype.updator=this.getUserInfo().getName();
			measuretype.updatetime=new Date();
			measuretype.merchantid=this.getUserInfo().getMerchantid();
			this.getDao().insert(measuretype);
			list.add(measuretype);			
		}
		this.OutputStr(this.toJSONArray(list));
	
	}
	public void update() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecsmeasuretypeofecsmeasuretypes.getRelationData().size();i++)
		{
			ECS_MeasureType measuretype=(ECS_MeasureType) this.ecsmeasuretypeofecsmeasuretypes.getRelationData().get(i);
						
			//检查唯一性
			this.checkUnique(this);
			
			measuretype.updator=this.getUserInfo().getLogin();
			measuretype.updatetime=new Date();
			this.getDao().update(measuretype);
		}
	}
	
	public int getId() {
		return id;	
	}
	public void setId(int id) {
		this.id = id;
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
}
