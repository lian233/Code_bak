package com.wofu.ecommerce.aftersale;

import java.util.ArrayList;
import java.util.Date;

import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.JException;


public class ECS_ProblemSort extends PageBusinessObject {

	private int sortid;
	private int orderid;
	private String sortname;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private DataRelation ecsproblemsortofecsproblemsorts =new DataRelation("ecsproblemsortofecsproblemsort","com.wofu.ecommerce.aftersale.ECS_ProblemSort");
	

	
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
	public int getOrderid() {
		return orderid;
	}
	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}
	public int getSortid() {
		return sortid;
	}
	public void setSortid(int sortid) {
		this.sortid = sortid;
	}
	public String getSortname() {
		return sortname;
	}
	public void setSortname(String sortname) {
		this.sortname = sortname;
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
	
	public void delete() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.getEcsproblemsortofecsproblemsorts().getRelationData().size();i++)
		{
			ECS_ProblemSort problemsort=(ECS_ProblemSort) this.getEcsproblemsortofecsproblemsorts().getRelationData().get(i);
			
			String sql="select count(*) from ecs_aftersaleproblem where sortid="+problemsort.getSortid();
			if (this.getDao().intSelect(sql)>0)
				throw new JException("问题分类:"+problemsort.getSortname()+"已被使用,不能删除!");
			
			this.getDao().delete(problemsort);
		}
	}
	
	public void insert() throws Exception
	{
			
		this.getJSONData();

		ArrayList list=new ArrayList();
		for (int i=0;i<this.getEcsproblemsortofecsproblemsorts().getRelationData().size();i++)
		{
			ECS_ProblemSort problemsort=(ECS_ProblemSort) this.getEcsproblemsortofecsproblemsorts().getRelationData().get(i);
			
			//检查唯一性
			this.checkUnique(problemsort);

			problemsort.sortid=this.getDao().IDGenerator(problemsort, "sortid");
			problemsort.creator=this.getUserInfo().getName();
			problemsort.createtime=new Date();			
			problemsort.updator=this.getUserInfo().getName();
			problemsort.updatetime=new Date();
			this.getDao().insert(problemsort);
			list.add(problemsort);
			
		}
		this.OutputStr(this.toJSONArray(list));
	
	}
	public void update() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.getEcsproblemsortofecsproblemsorts().getRelationData().size();i++)
		{
			ECS_ProblemSort problemsort=(ECS_ProblemSort) this.getEcsproblemsortofecsproblemsorts().getRelationData().get(i);
			
			//检查唯一性
			this.checkUnique(problemsort);
			
			problemsort.updator=this.getUserInfo().getLogin();
			problemsort.updatetime=new Date();
			this.getDao().update(problemsort);
		}
	}
	
	public ECS_ProblemSort()
	{
		this.searchOrderFieldName="createtime";
		this.orderMode="asc";
		this.uniqueFields1="sortname";
	}
	public DataRelation getEcsproblemsortofecsproblemsorts() {
		return ecsproblemsortofecsproblemsorts;
	}
	public void setEcsproblemsortofecsproblemsorts(
			DataRelation ecsproblemsortofecsproblemsorts) {
		this.ecsproblemsortofecsproblemsorts = ecsproblemsortofecsproblemsorts;
	}
}
