package com.wofu.base.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;


public class ECS_Attribute extends PageBusinessObject {
	

	private int attrid;
	private String name;
	private int scope;
	private int valuemode;
	private int inputtype;
	private int selectedtype;
	private int multiselect;
	private String selectvalues;
	private int status;	
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private int merchantid;
	
	private DataRelation ecsattributeofecsattributes =new DataRelation("ecsattributeofecsattribute","com.wofu.ecommerce.aftersale.ECS_AftersaleProblem");

	public ECS_Attribute()
	{
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="name";
		
		
		this.exportQuerySQL="select a.name,case when a.scope=1 then '商品' else '无' end as scope,"
			+"case when a.valuemode=1 then '输入' else '选择' end as valuemode,"
			+"case when a.inputtype=1 then '字符串' else '数字' end as inputtype,"
			+"case when a.selectedtype=1 then '下拉选择' when a.selecttype=2 then '逻辑选择' "
			+"when a.selecttype=3 then '单项选择' when a.selecttype=4 then '多项选择' else '日期选择' end as selecttype,"
			+"case when a.multiselect=1 then '是' else '否' end as multiselect,"
			+"case when a.status=1 then '是' else '否' end as status,"
			+"a.selectvalues,a.creator,a.createtime,a.updator,a.updatetime "
			+"from {searchSQL} a";
		
	}
	

	
	public void delete() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.getecsattributeofecsattributes().getRelationData().size();i++)
		{
			ECS_Attribute attribute=(ECS_Attribute) this.getecsattributeofecsattributes().getRelationData().get(i);
					
			this.getDao().delete(attribute);
		}
	}
	
	
	
	public void update() throws Exception
	{
		
		this.getJSONData();
		for (int i=0;i<this.getecsattributeofecsattributes().getRelationData().size();i++)
		{
			ECS_Attribute attribute=(ECS_Attribute) this.getecsattributeofecsattributes().getRelationData().get(i);
			
			//检查唯一性
			this.checkUnique(this);
						
			attribute.updator=this.getUserInfo().getName();
			attribute.updatetime=new Date();
			
	
			this.getDao().update(attribute);
		}
	
	}
	
	public void insert() throws Exception
	{
		
		this.getJSONData();
		ArrayList list=new ArrayList();
		for (int i=0;i<this.getecsattributeofecsattributes().getRelationData().size();i++)
		{
			ECS_Attribute attribute=(ECS_Attribute) this.getecsattributeofecsattributes().getRelationData().get(i);
			
			//检查唯一性
			this.checkUnique(this);
			
			attribute.attrid=this.getDao().IDGenerator(attribute, "attrid");
			
			attribute.creator=this.getUserInfo().getName();
			attribute.createtime=new Date();
			attribute.updator=this.getUserInfo().getName();
			attribute.updatetime=new Date();
			
		
			this.getDao().insert(attribute);
			
	
			
			list.add(attribute);
			
		}
		
		this.OutputStr(this.toJSONArray(list));	
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

	public DataRelation getecsattributeofecsattributes() {
		return ecsattributeofecsattributes;
	}

	public void setecsattributeofecsattributes(
			DataRelation ecsattributeofecsattributes) {
		this.ecsattributeofecsattributes = ecsattributeofecsattributes;
	}

	public int getAttrid() {
		return attrid;
	}

	public void setAttrid(int attrid) {
		this.attrid = attrid;
	}

	public int getInputtype() {
		return inputtype;
	}

	public void setInputtype(int inputtype) {
		this.inputtype = inputtype;
	}

	public int getMultiselect() {
		return multiselect;
	}

	public void setMultiselect(int multiselect) {
		this.multiselect = multiselect;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public int getSelectedtype() {
		return selectedtype;
	}

	public void setSelectedtype(int selectedtype) {
		this.selectedtype = selectedtype;
	}

	public String getSelectvalues() {
		return selectvalues;
	}

	public void setSelectvalues(String selectvalues) {
		this.selectvalues = selectvalues;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public int getValuemode() {
		return valuemode;
	}

	public void setValuemode(int valuemode) {
		this.valuemode = valuemode;
	}



	public DataRelation getEcsattributeofecsattributes() {
		return ecsattributeofecsattributes;
	}



	public void setEcsattributeofecsattributes(
			DataRelation ecsattributeofecsattributes) {
		this.ecsattributeofecsattributes = ecsattributeofecsattributes;
	}



	public int getMerchantid() {
		return merchantid;
	}



	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}

}
