package com.wofu.base.util;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public abstract class PageBusinessObject extends BusinessObject {

	private int currpage=1;
	private int pagesize=20;
	private int rowcount;
	private int pagecount;
	
	
	public void search() throws Exception
	{
		String reqdata = this.getReqData();	
		Properties prop=StringUtil.getIniProperties(reqdata);
		String sqlwhere=prop.getProperty("sqlwhere");
		String currpage=prop.getProperty("currpage");
		String pagesize=prop.getProperty("pagesize");
				
		if (prop.containsKey("ordermode"))
			this.orderMode=prop.getProperty("ordermode");

		if (prop.containsKey("searchorderfieldname"))
			this.searchOrderFieldName=prop.getProperty("searchorderfieldname");

		this.setPagesize(Integer.valueOf(pagesize).intValue());
		this.setCurrpage(Integer.valueOf(currpage).intValue());
		
		String tablename=this.getClass().getSimpleName();

		String sql="select * from "+tablename+" with(nolock) where 1=1 "+sqlwhere;
		this.getRequest().getSession().removeAttribute("search_sql_"+this.getModuleid());
		this.getRequest().getSession().setAttribute("search_sql_"+this.getModuleid(),sql);
		
		this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sql,this.searchOrderFieldName,this.orderMode)));
	}
	
	/**
	 * @param sql
	 * @param orderfields
	 * @param ordermode
	 * @return
	 * @throws Exception
	 */
	public Vector getPaginationData(String sql,String orderfields,String ordermode) throws Exception
	{
	
		if (currpage == 0) currpage = 1;
		if (pagesize == 0) pagesize = 20;
	
		this.rowcount = this.getDao()
				.intSelect("select count(*) from (" + sql + ") a");
		this.pagecount = (this.rowcount + this.pagesize - 1) / this.pagesize;
		if (this.currpage > this.pagecount) this.currpage = this.pagecount;
	

		int v_heiRownum=this.currpage*this.pagesize;
		int v_lowRownum=v_heiRownum-this.pagesize+1;
		
		boolean isSQL2000=false;
		if (this.getConnection().getMetaData().getDatabaseProductVersion().indexOf("Microsoft SQL Server  2000")>=0)
			isSQL2000=true;		
		
		if (isSQL2000)
		{
			if (!orderfields.equals(""))
			{
				sql="select identity(int,1,1) rownum,* into #temp from ("
					+sql+" ) a ";
				this.getDao().execute(sql);
				
				sql="select * from #temp "+" where rownum>="+v_lowRownum+" and rownum<="+v_heiRownum
					+" order by "+orderfields+" "+ordermode;
				
			}
			else
			{
				sql="select identity(int,1,1) rownum,* into #temp from ("
					+sql+") a ";
				this.getDao().execute(sql);
				
				sql="select * from #temp "+" where rownum>="+v_lowRownum+" and rownum<="+v_heiRownum;
			}
		}
		else
		{				
			if (!orderfields.equals(""))
				sql="select * from (select ROW_NUMBER() OVER(ORDER BY "+orderfields+" "+ordermode+") rownum,* from ("
					+sql+") a) b where rownum>="+v_lowRownum+" and rownum<="+v_heiRownum;
			else
			{
				String tablename = this.getClass().getSimpleName();
				String keysql="select keyname from ecs_idlist where tablename='"+tablename+"'";
				String keyfieldname=this.getDao().strSelect(keysql);
				if (keyfieldname.equals(""))
						throw new JException("Î´ÉèÖÃÅÅÐò×Ö¶Î»ò¹Ø¼ü×Ö¶Î!");
				
				sql="select * from (select ROW_NUMBER() OVER(ORDER BY "+keyfieldname+" asc) rownum,* from ("
				+sql+") a) b where rownum>="+v_lowRownum+" and rownum<="+v_heiRownum;
			}
		}
		
		
		
		Vector rList=this.getDao().multiRowSelect(sql);
		
		if (isSQL2000) this.getDao().execute("drop table #temp");
		
		return rList;
	}
	
	public  String toPaginationJSONArray(List lst) throws Exception
	{

		String jsondata=this.toJSONArray(lst);

		return "{currpage:"+this.currpage+",pagesize:"+this.pagesize+
			",rowcount:"+this.rowcount+",pagecount:"+this.pagecount+
			",data:"+jsondata+"}";
	}
	


	public int getCurrpage() {
		return currpage;
	}

	public void setCurrpage(int currpage) {
		this.currpage = currpage;
	}

	public int getPagecount() {
		return pagecount;
	}

	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getRowcount() {
		return rowcount;
	}

	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}
	
}
