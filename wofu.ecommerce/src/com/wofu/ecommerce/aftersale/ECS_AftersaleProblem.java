package com.wofu.ecommerce.aftersale;

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


public class ECS_AftersaleProblem extends PageBusinessObject {
	
	private int problemid;
	private int outorgid;
	private int inorgid;
	private String ordercode;
	private String customernick;
	private String linkman;
	private String linktele;
	private String postcompany;
	private String postcode;
	private String description;
	private int sortid;
	private int status;
	private int isurgent;
	private int isclaimant;
	private int isfreepostfee;
	private int followupnum;
	private String tranpostcompany;
	private String tranpostcode;
	private String followcontent;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	
	private DataRelation ecsaftersaleproblemofecsaftersaleproblems =new DataRelation("ecsaftersaleproblemofecsaftersaleproblem","com.wofu.ecommerce.aftersale.ECS_AftersaleProblem");

	public ECS_AftersaleProblem()
	{
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="postcode";
		this.uniqueFields2="ordercode";
		
		String postSQL="select ltrim(rtrim(companycode)) companycode,name companyname from ecs_express ";
		
		this.exportQuerySQL="select a.ordercode,b.orgname as outorgid,a.followcontent,"
			+"a.customernick,a.linkman,a.linktele,f.orgname as inorgid,isnull(e.companyname,'') as tranpostcompany,a.tranpostcode,"
			+"c.sortname as sortid,d.companyname as postcompany,"
			+"a.postcode,a.description,case when a.status=0 then '登记' when a.status=1 then '跟进中' "
			+"when a.status=2 then '已处理' else '完结' end as status,"
			+"case when a.isurgent=1 then '是' else '否' end as isurgent,"
			+"case when a.isclaimant=1 then '是' else '否' end as isclaimant,"
			+"case when a.isfreepostfee=1 then '是' else '否' end as isfreepostfee,"
			+"a.followupnum,a.creator,a.createtime,a.updator,a.updatetime "
			+"from {searchSQL} a,ecs_org b,ecs_problemsort c,"
			+"("+postSQL+") d,("+postSQL+") e,ecs_org f "
			+"where a.outorgid=b.orgid and a.sortid=c.sortid and a.inorgid=f.orgid "
			+"and a.postcompany=d.companycode and a.tranpostcompany*=e.companycode";
	}
	
	public void getSort() throws Exception
	{
		String sql="select sortid,sortname from ecs_problemsort order by orderid";
		
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	public void delete() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.getEcsaftersaleproblemofecsaftersaleproblems().getRelationData().size();i++)
		{
			ECS_AftersaleProblem aftersaleproblem=(ECS_AftersaleProblem) this.getEcsaftersaleproblemofecsaftersaleproblems().getRelationData().get(i);
					
			this.getDao().delete(aftersaleproblem);
		}
	}
	public void getOrderInfo() throws Exception
	{
		String reqdata = this.getReqData();		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String fieldname=prop.getProperty("fieldname");
		String fieldvalue=prop.getProperty("fieldvalue");
		
		String sql="select refordercode ordercode,outorgid,inorgid,"
			+"customernick,linktele,linkman,delivery postcompany,"
			+"deliverysheetid postcode from ecs_order  "
			+"where flag=10 and "+fieldname+"='"+fieldvalue+"'";
		Hashtable ht=this.getDao().oneRowSelect(sql);
		
		sql="select count(*) from ecs_trandelivery where oridelivery='"
			+ht.get("postcompany")+"' and orideliverysheetid='"+ht.get("postcode")+"'";
		if (this.getDao().intSelect(sql)>0)
		{
			sql="select delivery as postcompany,deliverysheetid as postcode from ecs_trandelivery where oridelivery='"
				+ht.get("postcompany")+"' and orideliverysheetid='"+ht.get("postcode")+"'";
			Hashtable httran=this.getDao().oneRowSelect(sql);
						
			ht.putAll(httran);
		}
		this.OutputStr(this.toJSONObject(ht));
	}
	
	public void update() throws Exception
	{
		
		this.getJSONData();
		for (int i=0;i<this.getEcsaftersaleproblemofecsaftersaleproblems().getRelationData().size();i++)
		{
			ECS_AftersaleProblem aftersaleproblem=(ECS_AftersaleProblem) this.getEcsaftersaleproblemofecsaftersaleproblems().getRelationData().get(i);
			
			//检查唯一性
			this.checkUnique(aftersaleproblem);
						
			aftersaleproblem.updator=this.getUserInfo().getName();
			aftersaleproblem.updatetime=new Date();
			
	
			this.getDao().update(aftersaleproblem);
		}
	
	}
	
	public void insert() throws Exception
	{
		
		this.getJSONData();
		ArrayList list=new ArrayList();
		for (int i=0;i<this.getEcsaftersaleproblemofecsaftersaleproblems().getRelationData().size();i++)
		{
			ECS_AftersaleProblem aftersaleproblem=(ECS_AftersaleProblem) this.getEcsaftersaleproblemofecsaftersaleproblems().getRelationData().get(i);
			
			//检查唯一性
			this.checkUnique(aftersaleproblem);
			
			aftersaleproblem.problemid=this.getDao().IDGenerator(aftersaleproblem, "problemid");
			
			aftersaleproblem.creator=this.getUserInfo().getName();
			aftersaleproblem.createtime=new Date();
			aftersaleproblem.updator=this.getUserInfo().getName();
			aftersaleproblem.updatetime=new Date();
			
			String followcontent=aftersaleproblem.followcontent;
			if (!followcontent.equals(""))
			{
				aftersaleproblem.status=1;
				aftersaleproblem.followupnum=1;
				aftersaleproblem.followcontent=this.getUserInfo().getName()+" "
					+Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)
					+" "+aftersaleproblem.followcontent.concat("%enter%");
			}
	
			this.getDao().insert(aftersaleproblem);
			
			if (!aftersaleproblem.followcontent.equals(""))
			{
				ECS_ProblemFollowup problemfollowup=new ECS_ProblemFollowup();
				
				problemfollowup.setFollowid(this.getDao().IDGenerator(problemfollowup, "followid"));
				problemfollowup.setProblemid(aftersaleproblem.problemid);
				problemfollowup.setNotes(followcontent);
				problemfollowup.setCreator(this.getUserInfo().getName());
				problemfollowup.setCreatetime(new Date());
	
				this.getDao().insert(problemfollowup);
			}
			
			
			list.add(aftersaleproblem);
			
		}
		
		this.OutputStr(this.toJSONArray(list));	
	}
	

	private String getFollowupContent(String problemid) throws Exception
	{		
		StringBuffer followupcontent=new StringBuffer();
		
		String sql="select creator,convert(char(19),createtime,120) createtime,notes from ecs_problemfollowup "
			+" where problemid="+problemid
			+" order by followid";
		Vector vtn=this.getDao().multiRowSelect(sql);
		for (int i=0;i<vtn.size();i++)
		{

			Hashtable htn=(Hashtable) vtn.get(i);

			followupcontent.append(htn.get("creator").toString());		
			followupcontent.append(" ");
			followupcontent.append(htn.get("createtime").toString());
			followupcontent.append(" ");
			followupcontent.append(htn.get("notes").toString());
			followupcontent.append("%enter%");
		}

		return followupcontent.toString();
		
	}
	
	/*
	private String getFollowupContent(String problemid) throws Exception
	{		
		StringBuffer followupcontent=new StringBuffer();
		followupcontent.append("<span style='font-weight:bold;font-size:10px/1.5;'>问题描述</span><br>");
		followupcontent.append("<span style='font:10px/1.5 tahoma, arial, 宋体;'>");
		
		String sql="select description from ecs_aftersaleproblem where problemid="+problemid;
		
		followupcontent.append(this.getDao().strSelect(sql)+"</span><br>");
		
		followupcontent.append("<span style='font-weight:bold;font-size:10px/1.5;'>跟进信息</span><br>");
		followupcontent.append("<div style='border:#eaeaea 1px solid; background-color:#fbfbfc;line-height:200%;'>");		
		followupcontent.append("<ul>");	
		
		sql="select creator,convert(char(19),createtime,120) createtime,notes from ecs_problemfollowup "
			+" where problemid="+problemid
			+" order by followid";
		Vector vtn=this.getDao().multiRowSelect(sql);
		for (int i=0;i<vtn.size();i++)
		{

			Hashtable htn=(Hashtable) vtn.get(i);
			followupcontent.append("<li>");
			followupcontent.append("<span style='font:10px/1.5 tahoma, arial, 宋体;'>");
			followupcontent.append(htn.get("creator").toString());
			followupcontent.append("&nbsp;");
			followupcontent.append(htn.get("createtime").toString());
			followupcontent.append("&nbsp;");
			followupcontent.append(htn.get("notes").toString());
			followupcontent.append("</span>");
			followupcontent.append("</li>");
		}
		
		followupcontent.append("</ul>");
		followupcontent.append("</div>");
		
		return followupcontent.toString();
		
	}
	*/
	
	public void followup() throws Exception
	{
		String reqdata = this.getReqData();		
		
		Properties prop=StringUtil.getIniProperties(reqdata);
		int problemid=Integer.valueOf(prop.getProperty("problemid")).intValue();
		String notes=prop.getProperty("notes");
		
		String sql="select status from ecs_aftersaleproblem where problemid="+problemid;
		if (this.getDao().intSelect(sql)==100)
		{
			throw new JException("状态不对,该问题已完结!");
		}
					
		ECS_ProblemFollowup problemfollowup=new ECS_ProblemFollowup();
				
		problemfollowup.setFollowid(this.getDao().IDGenerator(problemfollowup, "followid"));
		problemfollowup.setProblemid(problemid);
		problemfollowup.setNotes(notes);
		problemfollowup.setCreator(this.getUserInfo().getName());
		problemfollowup.setCreatetime(new Date());

		
		this.getDao().insert(problemfollowup);
		
		this.getDataByID(problemid);
		this.problemid=problemid;				
		this.status=1;
		this.followupnum=this.followupnum+1;
		this.updator=this.getUserInfo().getName();
		this.updatetime=new Date();
		
		this.followcontent=this.getFollowupContent(String.valueOf(problemid));
		
		this.getDao().update(this, "followcontent,status,followupnum,updator,updatetime");
		
		this.OutputStr(StringUtil.replace(this.followcontent,"%enter%","\\r\\n"));
		
	}
	
	public void cover() throws Exception
	{
		String reqdata = this.getReqData();		
		
		Properties prop=StringUtil.getIniProperties(reqdata);
		int problemid=Integer.valueOf(prop.getProperty("problemid")).intValue();
		String notes=prop.getProperty("notes");
		
		String sql="select status from ecs_aftersaleproblem where problemid="+problemid;
		if (this.getDao().intSelect(sql)==100)
		{
			throw new JException("状态不对,该问题已完结!");
		}
			
		sql="select count(*) from ecs_problemfollowup where problemid="+problemid;
		
		if(this.getDao().intSelect(sql)==0)
		{
			throw new JException("不存在跟进信息!");
		}
		
		sql="select max(followid) from ecs_problemfollowup where problemid="+problemid;
		int followid=this.getDao().intSelect(sql);
		
		ECS_ProblemFollowup problemfollowup=new ECS_ProblemFollowup();
		
		problemfollowup.getDataByID(followid);
				
		problemfollowup.setNotes(notes);
		problemfollowup.setCreator(this.getUserInfo().getName());
		problemfollowup.setCreatetime(new Date());

		
		this.getDao().update(problemfollowup);
		
		this.getDataByID(problemid);
		this.problemid=problemid;				
		this.status=1;
		this.updator=this.getUserInfo().getName();
		this.updatetime=new Date();
		
		this.getDao().update(this, "status,updator,updatetime");
		
		this.OutputStr(this.getFollowupContent(String.valueOf(problemid)));
		
	}
	
	
	public void getDelivery() throws Exception
	{
		String sql="select ltrim(rtrim(companycode)) companycode,name companyname from ecs_express ";
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



	public String getCustomernick() {
		return customernick;
	}

	public void setCustomernick(String customernick) {
		this.customernick = customernick;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DataRelation getEcsaftersaleproblemofecsaftersaleproblems() {
		return ecsaftersaleproblemofecsaftersaleproblems;
	}

	public void setEcsaftersaleproblemofecsaftersaleproblems(
			DataRelation ecsaftersaleproblemofecsaftersaleproblems) {
		this.ecsaftersaleproblemofecsaftersaleproblems = ecsaftersaleproblemofecsaftersaleproblems;
	}

	public int getFollowupnum() {
		return followupnum;
	}

	public void setFollowupnum(int followupnum) {
		this.followupnum = followupnum;
	}

	public String getOrdercode() {
		return ordercode;
	}

	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}

	public int getOutorgid() {
		return outorgid;
	}

	public void setOutorgid(int outorgid) {
		this.outorgid = outorgid;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getPostcompany() {
		return postcompany;
	}

	public void setPostcompany(String postcompany) {
		this.postcompany = postcompany;
	}

	public int getProblemid() {
		return problemid;
	}

	public void setProblemid(int problemid) {
		this.problemid = problemid;
	}

	public int getSortid() {
		return sortid;
	}

	public void setSortid(int sortid) {
		this.sortid = sortid;
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

	public int getIsurgent() {
		return isurgent;
	}

	public void setIsurgent(int isurgent) {
		this.isurgent = isurgent;
	}

	public int getIsfreepostfee() {
		return isfreepostfee;
	}

	public void setIsfreepostfee(int isfreepostfee) {
		this.isfreepostfee = isfreepostfee;
	}

	public String getFollowcontent() {
		return followcontent;
	}

	public void setFollowcontent(String followcontent) {
		this.followcontent = followcontent;
	}

	public int getIsclaimant() {
		return isclaimant;
	}

	public void setIsclaimant(int isclaimant) {
		this.isclaimant = isclaimant;
	}

	public String getLinktele() {
		return linktele;
	}

	public void setLinktele(String linktele) {
		this.linktele = linktele;
	}



	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getTranpostcode() {
		return tranpostcode;
	}

	public void setTranpostcode(String tranpostcode) {
		this.tranpostcode = tranpostcode;
	}

	public String getTranpostcompany() {
		return tranpostcompany;
	}

	public void setTranpostcompany(String tranpostcompany) {
		this.tranpostcompany = tranpostcompany;
	}

	public int getInorgid() {
		return inorgid;
	}

	public void setInorgid(int inorgid) {
		this.inorgid = inorgid;
	}
}
