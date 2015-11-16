package com.wofu.base.report;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
import com.wofu.common.tools.util.StreamUtil;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class ECS_ReportTemplate extends BusinessObject {
	
	private int itemid;
	private String name;
	private int folderid;
	private int flag;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private int merchantid;
	private InputStream presql;
	private InputStream clearsql;
	
	private DataRelation querysqlofitems =new DataRelation("querysqlofitem","com.wofu.base.report.ECS_ReportItemQuerySQL");
	private DataRelation condofitems =new DataRelation("condofitem","com.wofu.base.report.ECS_ReportItemCond");

	public void getGroup() throws Exception
	{
		String sql="select groupid,groupname from ecs_reportgroup where merchantid="+this.getUserInfo().getMerchantid();
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	public void addGroup() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String groupname=prop.getProperty("groupname");
		
		ECS_ReportGroup reportgroup=new ECS_ReportGroup();
		reportgroup.setGroupid(this.getDao().IDGenerator(reportgroup, "groupid"));
		reportgroup.setGroupname(groupname);
		reportgroup.setMerchantid(this.getUserInfo().getMerchantid());
		this.getDao().insert(reportgroup);
		

		String sql="select groupid,groupname from ecs_reportgroup where merchantid="+this.getUserInfo().getMerchantid();
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	public void renameGroup() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		int groupid=Integer.valueOf(prop.getProperty("groupid")).intValue();
		String groupname=prop.getProperty("groupname");
		
		ECS_ReportGroup reportgroup=new ECS_ReportGroup();
		reportgroup.setGroupid(groupid);
		reportgroup.setGroupname(groupname);
		this.getDao().update(reportgroup, "groupname");
		
		String sql="select groupid,groupname from ecs_reportgroup where merchantid="+this.getUserInfo().getMerchantid();
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	public void getFolder() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String groupid=prop.getProperty("groupid");
		
		String json=this.getJSONTree("ecs_reportfolder", "folderid", "foldername", "parentid", "0"," and groupid="+groupid+" and flag=0");
		this.OutputStr(json);
	}
	
	public void getFolderItems() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String folderid=prop.getProperty("folderid");
		StringBuffer strbuf = new StringBuffer();
		
		String sql = "select count(*) from ecs_reportitems  with(nolock) "
				+ " where folderid='" + folderid + "' ";
		if (this.getDao().intSelect(sql) > 0) {
			strbuf.append("[");
			sql = "select itemid,name from ecs_reportitems with(nolock) where folderid='" + folderid + "' ";
			Vector vt = this.getDao().multiRowSelect(sql);
			for (int i = 0; i < vt.size(); i++) {
				Hashtable ht = (Hashtable) vt.get(i);
				String idvalue = ht.get("itemid").toString();
				String namevalue = ht.get("name").toString();

				strbuf.append("{");
				strbuf.append("name:'" + namevalue + "',id:'" + idvalue + "'");
				
				strbuf.append(",leaf:true");
				strbuf.append("},");
			}
			strbuf.deleteCharAt(strbuf.length() - 1);
			strbuf.append("]");
		} else
			strbuf.append("0");
		
		this.OutputStr(strbuf.toString());
	}
	
	public void addFolder() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		int parentid=Integer.valueOf(prop.getProperty("parentid")).intValue();
		int groupid=Integer.valueOf(prop.getProperty("groupid")).intValue();
		String foldername=prop.getProperty("foldername");
		
		ECS_ReportFolder folder=new ECS_ReportFolder();
		folder.setGroupid(groupid);
		folder.setParentid(parentid);
		folder.setFolderid(this.getDao().IDGenerator(folder, "folderid"));
		folder.setFoldername(foldername);
		folder.setFlag(0);
		
		this.getDao().insert(folder);
		
		this.OutputStr("{name:'"+folder.getFoldername()+"',id:"+folder.getFolderid()+",leaf:true}");
		
	}
	
	public void removeFolder() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		int folderid=Integer.valueOf(prop.getProperty("folderid")).intValue();

		
		ECS_ReportFolder folder=new ECS_ReportFolder();
		folder.setFolderid(folderid);
		folder.setFlag(1);
		
		this.getDao().update(folder, "flag");
		
		String sql="update ecs_reportitems set flag=1 where folderid="+folderid;
		
		this.getDao().execute(sql);
	}
	
	public void renameFolder() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		int folderid=Integer.valueOf(prop.getProperty("folderid")).intValue();
		String foldername=prop.getProperty("foldername");

		
		ECS_ReportFolder folder=new ECS_ReportFolder();
		folder.setFolderid(folderid);
		folder.setFoldername(foldername);

		this.getDao().update(folder, "foldername");

	}
	
	public void removeReport() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		int itemid=Integer.valueOf(prop.getProperty("itemid")).intValue();

		String sql="update ecs_reportitems set flag=1 where itemid="+itemid;
		
		this.getDao().execute(sql);
	}
	
	public void getReportItems() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		int folderid=Integer.valueOf(prop.getProperty("folderid")).intValue();
		
		String sql="select * from ecs_reportitems  "
			+"where folderid="+folderid +" and flag=0 ";
		
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	public void getItemInfo() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		int itemid=Integer.valueOf(prop.getProperty("itemid")).intValue();
		
		String sql="select a.*,b.presql,b.clearsql from ecs_reportitems a,ecs_reportitemsql b "
			+"where a.itemid="+itemid +" and a.itemid=b.itemid";
		
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	
	public void select() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		int itemid=Integer.valueOf(prop.getProperty("itemid")).intValue();
		
		String sql="select a.*,b.presql,b.clearsql from ecs_reportitems a,ecs_reportitemsql b "
			+"where a.itemid="+itemid +" and a.itemid=b.itemid";
		
		this.getMapData(this.getDao().oneRowSelect(sql));
		
		sql="select * from ecs_reportitemquerysql where itemid="+itemid;
		this.querysqlofitems.setRelationData(this.getDao().multiRowSelect(sql));
		
		for (int i=0;i<this.querysqlofitems.getRelationData().size();i++)
		{
			ECS_ReportItemQuerySQL reportitemquerysql=(ECS_ReportItemQuerySQL) this.querysqlofitems.getRelationData().get(i);
			sql="select * from ecs_reportitemquerycolumn where queryid="+reportitemquerysql.getQueryid()+" and itemid="+itemid;
			reportitemquerysql.getColumnofquerys().setRelationData(this.getDao().multiRowSelect(sql));
			this.querysqlofitems.getRelationData().set(i, reportitemquerysql);			
		}
		
		sql="select * from ecs_reportitemcond where itemid="+itemid;
		this.condofitems.setRelationData(this.getDao().multiRowSelect(sql));
		

		this.OutputStr(this.toJSONObject());
		
	}
	
	public void getQueryInfo() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		int queryid=Integer.valueOf(prop.getProperty("queryid")).intValue();
		int reportitemid=Integer.valueOf(prop.getProperty("itemid")).intValue();
		
		ECS_ReportItemQuerySQL query=new ECS_ReportItemQuerySQL();
		String sql="select * from ecs_reportitemquerysql where queryid="+queryid+" and itemid="+reportitemid;
		query.getMapData(this.getDao().oneRowSelect(sql));
		
		sql="select * from ecs_reportitemquerycolumn where queryid="+queryid+" and itemid="+reportitemid;
		
		query.getColumnofquerys().setRelationData(this.getDao().multiRowSelect(sql));
		
		this.OutputStr(query.toJSONObject());
		
	}
	public void insert() throws Exception
	{
		this.getJSONData();
		
		ECS_ReportItems reportitem=new ECS_ReportItems();		
		this.copyTo(reportitem);
		reportitem.setItemid(this.getDao().IDGenerator(reportitem, "itemid"));
		reportitem.setCreator(this.getUserInfo().getLogin());
		reportitem.setCreatetime(new Date());
		reportitem.setUpdator(this.getUserInfo().getLogin());
		reportitem.setUpdatetime(new Date());
		
		this.getDao().insert(reportitem);
		
		
		ECS_ReportItemSQL reportitemsql=new ECS_ReportItemSQL();
		//this.copyTo(reportitemsql);		
		reportitemsql.setPresql(new ByteArrayInputStream(StreamUtil.InputStreamToStr(this.getPresql(), "GBK").getBytes()));
		reportitemsql.setClearsql(new ByteArrayInputStream(StreamUtil.InputStreamToStr(this.getClearsql(), "GBK").getBytes()));
		reportitemsql.setItemid(reportitem.getItemid());
		this.getDao().insert(reportitemsql);
		

		for (int i=0;i<this.getQuerysqlofitems().getRelationData().size();i++)
		{
			ECS_ReportItemQuerySQL reportitemquerysql=(ECS_ReportItemQuerySQL) this.getQuerysqlofitems().getRelationData().get(i);
			reportitemquerysql.setItemid(reportitem.getItemid());
			reportitemquerysql.setQueryid(-reportitemquerysql.getQueryid());  //改成正的序号
			this.getDao().insert(reportitemquerysql);

			
			for (int j=0;j<reportitemquerysql.getColumnofquerys().getRelationData().size();j++)
			{
				ECS_ReportItemQueryColumn reportitemquerycolumn=(ECS_ReportItemQueryColumn) reportitemquerysql.getColumnofquerys().getRelationData().get(j);
				reportitemquerycolumn.setItemid(reportitem.getItemid());
				reportitemquerycolumn.setQueryid(reportitemquerysql.getQueryid()); 
				this.getDao().insert(reportitemquerycolumn);
		
			}
		}
		
		
		for (int k=0;k<this.getCondofitems().getRelationData().size();k++)
		{
			ECS_ReportItemCond reportitemcond=(ECS_ReportItemCond) this.getCondofitems().getRelationData().get(k);
			reportitemcond.setItemid(reportitem.getItemid());
			
			this.getDao().insert(reportitemcond);
		
		}
				
		this.OutputStr(reportitem.toJSONObject());
		
	}
	public void update() throws Exception
	{
		this.getJSONData();
		
		ECS_ReportItems reportitem=new ECS_ReportItems();		
		this.copyTo(reportitem);
		reportitem.setUpdator(this.getUserInfo().getLogin());
		reportitem.setUpdatetime(new Date());
		
		this.getDao().update(reportitem);
		
		
		ECS_ReportItemSQL reportitemsql=new ECS_ReportItemSQL();
		
		reportitemsql.setPresql(new ByteArrayInputStream(StreamUtil.InputStreamToStr(this.getPresql(), "GBK").getBytes()));
		reportitemsql.setClearsql(new ByteArrayInputStream(StreamUtil.InputStreamToStr(this.getClearsql(), "GBK").getBytes()));
		reportitemsql.setItemid(reportitem.getItemid());
		this.getDao().updateByKeys(reportitemsql, "itemid");
		
		String sql="delete from ecs_reportitemquerycolumn where itemid="+reportitem.getItemid();
		this.getDao().execute(sql);
		
		sql="delete from ecs_reportitemquerysql where itemid="+reportitem.getItemid();
		this.getDao().execute(sql);
		
		for (int i=0;i<this.getQuerysqlofitems().getRelationData().size();i++)
		{
			ECS_ReportItemQuerySQL reportitemquerysql=(ECS_ReportItemQuerySQL) this.getQuerysqlofitems().getRelationData().get(i);

			reportitemquerysql.setItemid(reportitem.getItemid());

			this.getDao().insert(reportitemquerysql);
			
		
			for (int j=0;j<reportitemquerysql.getColumnofquerys().getRelationData().size();j++)
			{
				ECS_ReportItemQueryColumn reportitemquerycolumn=(ECS_ReportItemQueryColumn) reportitemquerysql.getColumnofquerys().getRelationData().get(j);
				
				reportitemquerycolumn.setItemid(reportitem.getItemid());

				this.getDao().insert(reportitemquerycolumn);
							
			}
		}
		
		sql="delete from ecs_reportitemcond where itemid="+reportitem.getItemid();
		this.getDao().execute(sql);
		
		for (int k=0;k<this.getCondofitems().getRelationData().size();k++)
		{
			ECS_ReportItemCond reportitemcond=(ECS_ReportItemCond) this.getCondofitems().getRelationData().get(k);
			
			reportitemcond.setItemid(reportitem.getItemid());
			
			this.getDao().insert(reportitemcond);

		
		}
				
		this.OutputStr(reportitem.toJSONObject());
	}
	
	public void getReportCond() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		int itemid=Integer.valueOf(prop.getProperty("itemid")).intValue();
		
		String sql="select * from ecs_reportitemcond where itemid="+itemid;
		
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	public void getRenderer() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String renderer=prop.getProperty("renderer");
		
		String data=this.toJSONArray(this.getDao().multiRowSelect(renderer));
		
		
		String model=this.toJSONArray(this.getDao().getSQLMeta(renderer));
		
		
		this.OutputStr("{\"data\":"+data+",\"model\":"+model+"}");
		
	}
	
	public void getReportQuery() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String itemid=prop.getProperty("itemid");
		
		String sql="select * from ecs_reportitemquerysql where itemid="+itemid;
		
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	public void getReportCols() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String itemid=prop.getProperty("itemid");
		String queryid=prop.getProperty("queryid");
		
		String sql="select * from ecs_reportitemquerycolumn where itemid="+itemid+" and queryid="+queryid;
		
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	public void getRunResult() throws Exception
	{
		String reqdata = this.getReqData();

		Properties prop=StringUtil.getIniProperties(reqdata);
		String itemid=prop.getProperty("itemid");
		String queryid=prop.getProperty("queryid");
		

		String sql="select querysql from ecs_reportitemquerysql where itemid="+itemid +" and queryid="+queryid;
		Hashtable htquery=this.getDao().oneRowSelect(sql);
		
		String querysql=StringUtil.replace(StreamUtil.InputStreamToStr((InputStream) htquery.get("querysql"), "GBK"),"%enter%", " ");

		querysql=StringUtil.replace(querysql, "%singlequot%", "'");
		
		sql="select presql,clearsql from ecs_reportitemsql where itemid="+itemid;
		Hashtable htitem=this.getDao().oneRowSelect(sql);
		
		String presql=StringUtil.replace(StreamUtil.InputStreamToStr((InputStream) htitem.get("presql"), "GBK"),"%enter%", " ");
		String clearsql=StringUtil.replace(StreamUtil.InputStreamToStr((InputStream) htitem.get("clearsql"), "GBK"),"%enter%", " ");
		
		presql=StringUtil.replace(presql, "%singlequot%", "'");
		clearsql=StringUtil.replace(clearsql, "%singlequot%", "'");
		
		for(Iterator it=prop.keySet().iterator();it.hasNext();)
		{
			String condid=(String) it.next();
			
			if (condid.equalsIgnoreCase("itemid")
				||condid.equalsIgnoreCase("queryid")) continue;
			
			String condvalue=prop.getProperty(condid);
			
			sql="select * from ecs_reportitemcond "
				+"where itemid="+itemid+"  and condid="+condid;
			
			Hashtable htcond=this.getDao().oneRowSelect(sql);
			
			if (htcond.get("logistic").toString().equals("2")
				||htcond.get("logistic").toString().equals("9"))
			{
				String[] betweens=condvalue.split(",");
				
				if (!htcond.get("queryfield").toString().equals(""))
				{					
					if (htcond.get("logistic").toString().equals("2"))
						querysql=querysql.concat(" and "+htcond.get("queryfield").toString()+" between '"+betweens[0]+"' and '"+betweens[1]+"'");
					else
						querysql=querysql.concat(" and not "+htcond.get("queryfield").toString()+" between '"+betweens[0]+"' and '"+betweens[1]+"'");
				}
						
				
				if (!htcond.get("replaceexpression").toString().equals(""))
				{
					String[] expressions=htcond.get("replaceexpression").toString().split(",");
					
							
					presql=StringUtil.replace(presql,expressions[0],betweens[0]);
					presql=StringUtil.replace(presql,expressions[1],betweens[1]);
					
					clearsql=StringUtil.replace(clearsql,expressions[0],betweens[0]);
					clearsql=StringUtil.replace(clearsql,expressions[1],betweens[1]);
					
					querysql=StringUtil.replace(querysql,expressions[0],betweens[0]);
					querysql=StringUtil.replace(querysql,expressions[1],betweens[1]);					
				}
				
			}
			else
			{
				if (!htcond.get("queryfield").toString().equals(""))
				{					
					int logistic=Integer.valueOf(htcond.get("logistic").toString()).intValue();
					
					String condstr="";
					if (logistic==0)
					{
						condstr="='"+condvalue+"'";
					}else if (logistic==1)
					{
						condstr="in ("+condvalue+")";
					}else if (logistic==3)
					{
						condstr="like '%"+condvalue+"%'";
					}else if (logistic==4)
					{
						condstr="<>'"+condvalue+"'";
					}else if (logistic==5)
					{
						condstr=">'"+condvalue+"'";
					}else if (logistic==6)
					{
						condstr=">='"+condvalue+"'";
					}else if (logistic==7)
					{
						condstr="<'"+condvalue+"'";
					}else if (logistic==8)
					{
						condstr="<='"+condvalue+"'";
					}else if (logistic==10)
					{
						condstr="not like '%"+condvalue+"%'";
					}
					
					querysql=querysql.concat(" and "+condstr);
				}
				
				if (!htcond.get("replaceexpression").toString().equals(""))
				{
					
					presql=StringUtil.replace(presql,htcond.get("replaceexpression").toString(),condvalue);
					clearsql=StringUtil.replace(clearsql,htcond.get("replaceexpression").toString(),condvalue);
					querysql=StringUtil.replace(querysql,htcond.get("replaceexpression").toString(),condvalue);
				}
			}
		}
		
		
		if (!clearsql.equals(""))
			this.getDao().execute(clearsql);
		if (!presql.equals(""))
			this.getDao().execute(presql);
		
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(querysql)));
	}
	
	public int getItemid() {
		return itemid;	
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public InputStream getPresql() {
		return presql;	
	}
	public void setPresql(InputStream presql) {
		this.presql = presql;
	}
	public InputStream getClearsql() {
		return clearsql;	
	}
	public void setClearsql(InputStream clearsql) {
		this.clearsql = clearsql;
	}

	public DataRelation getCondofitems() {
		return condofitems;
	}

	public void setCondofitems(DataRelation condofitems) {
		this.condofitems = condofitems;
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

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getFolderid() {
		return folderid;
	}

	public void setFolderid(int folderid) {
		this.folderid = folderid;
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

	public DataRelation getQuerysqlofitems() {
		return querysqlofitems;
	}

	public void setQuerysqlofitems(DataRelation querysqlofitems) {
		this.querysqlofitems = querysqlofitems;
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
