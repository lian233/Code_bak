package com.wofu.ecommerce.customerservice;


import java.util.Date;
import java.util.Properties;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class ECS_Schedule extends BusinessObject {
	
	private int scheduleid;
	private int userid;
	private Date sdate;
	private int squadid;
	
	private DataRelation scheduleofschedules =new DataRelation("scheduleofschedule","com.wofu.ecommerce.customerservice.ECS_Schedule");

	public void getCustomerAccount() throws Exception
	{
		String sql="select userid,a.name from ecs_user a with(nolock),customerserviceaccount b with(nolock) where a.login=b.usernick and b.enabled=1";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	public void getSquad() throws Exception
	{
		String sql="select squadid,name from ecs_squad";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	public void search() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String startdate=prop.getProperty("startdate");
		String enddate=prop.getProperty("enddate");
		String sql="select userid,convert(char(10),sdate,120) sdate,a.squadid,b.name squadname "
			+"from ecs_schedule a with(nolock),ecs_squad b with(nolock) where a.squadid=b.squadid and sdate>='"+startdate+"' and sdate<='"+enddate+"' order by sdate";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	public void insert() throws Exception
	{
	
		this.getJSONData();
		for (int i=0;i<this.scheduleofschedules.getRelationData().size();i++)
		{
			ECS_Schedule schedule=(ECS_Schedule) this.scheduleofschedules.getRelationData().get(i);
			
			this.getDao().deleteByKeys(schedule, "userid,sdate");

			schedule.scheduleid=this.getDao().IDGenerator(schedule, "scheduleid");			
			this.getDao().insert(schedule);

		}
	}
	

	public int getScheduleid() {
		return scheduleid;
	}
	public void setScheduleid(int scheduleid) {
		this.scheduleid = scheduleid;
	}
	public Date getSdate() {
		return sdate;
	}
	public void setSdate(Date sdate) {
		this.sdate = sdate;
	}
	public int getSquadid() {
		return squadid;
	}
	public void setSquadid(int squadid) {
		this.squadid = squadid;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}

	public DataRelation getScheduleofschedules() {
		return scheduleofschedules;
	}

	public void setScheduleofschedules(DataRelation scheduleofschedules) {
		this.scheduleofschedules = scheduleofschedules;
	}


}
