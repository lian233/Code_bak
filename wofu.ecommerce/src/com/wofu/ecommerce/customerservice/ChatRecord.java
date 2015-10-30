package com.wofu.ecommerce.customerservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.wofu.base.util.BusinessObject;
import com.wofu.common.tools.conv.Base64;
import com.wofu.common.tools.conv.Convert;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StreamHelper;
import com.wofu.common.tools.util.StreamUtil;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class ChatRecord extends BusinessObject {
	
	private SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");

	public void getTradeContacts() throws Exception
	{
		String sql="select tradecontactid,tradecontacts from tradecontacts with(nolock)";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	public void getCSAmount() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);
		String tradecontactid=prop.getProperty("tradecontactid");
		String sql="select usernick,fullname from CustomerServiceAccount with(nolock) where roletype=0 and tradecontactid="+tradecontactid;
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	public void getAllCSAmount() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);
		String tradecontactid=prop.getProperty("tradecontactid");
		String sql="select usernick,fullname from CustomerServiceAccount with(nolock) where tradecontactid="+tradecontactid;
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	public void doSearch() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);
		String tradecontactid=prop.getProperty("tradecontactid");
		String csnick=prop.getProperty("csnick");
		String chatdate=prop.getProperty("chatdate");
		String valid=prop.getProperty("valid");
		String status=prop.getProperty("status");
		
		String sql="";
		String sqlwhere="";
		if (csnick.toLowerCase().indexOf("all")<0)
			sqlwhere=sqlwhere+" and csnick='"+csnick+"' ";
		if (valid.equalsIgnoreCase("0"))
			sqlwhere=sqlwhere+" and valid='"+valid+"' ";
		if (status.toLowerCase().indexOf("all")<0)
		{
			//未拍下
			if (status.equalsIgnoreCase("-1"))
				sqlwhere=sqlwhere+" and customernick not in(select customernick "
					+"from ConsumeCustomer with(nolock) where convert(char(10),buydate,120)='"+chatdate+"' and  tradecontactid="+tradecontactid+")";
			else if (status.equalsIgnoreCase("0")) //等待付款
				sqlwhere=sqlwhere+" and customernick in(select customernick "
					+"from ConsumeCustomer with(nolock) where convert(char(10),buydate,120)='"+chatdate+"' and flag=0 and  tradecontactid="+tradecontactid+")";
			else if (status.equalsIgnoreCase("1")) //已付款
				sqlwhere=sqlwhere+" and customernick in(select customernick "
					+"from ConsumeCustomer with(nolock) where convert(char(10),buydate,120)='"+chatdate+"' and flag=1 and  tradecontactid="+tradecontactid+")";
			else if (status.equalsIgnoreCase("2")) //取消
				sqlwhere=sqlwhere+" and customernick in(select customernick "
					+"from ConsumeCustomer with(nolock) where convert(char(10),buydate,120)='"+chatdate+"' and flag=2 and  tradecontactid="+tradecontactid+")";
		}
			
		sql="select tradecontactid,csnick,customernick,chatdate,valid,isresponse,"
			+"avgresponsesecond,totaltime,responsenum,isnull(convert(char(8),exceptstarttime,108),'') exceptstarttime , "
			+"isnull(convert(char(8),exceptendtime,108),'') exceptendtime from CustomerServiceChatpeers with(nolock) "
			+" where tradecontactid="+tradecontactid
			+" and convert(char(10),chatdate,120)='"+chatdate+"'";
		
		if (!sqlwhere.equalsIgnoreCase(""))
			sql=sql+sqlwhere;
		
		Vector vt=this.getDao().multiRowSelect(sql);
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			String customernick=ht.get("customernick").toString();
			sql="select count(*) from ConsumeCustomer with(nolock) where convert(char(10),buydate,120)='"
				+chatdate+"' and customernick='"+customernick+"' and  tradecontactid="+tradecontactid;
			if (this.getDao().intSelect(sql)==0)
				ht.put("status", "-1");
			else
			{
				if (status.toLowerCase().indexOf("all")<0)
					sql="select flag from ConsumeCustomer with(nolock) where convert(char(10),buydate,120)='"
						+chatdate+"' and customernick='"+customernick+"' and  tradecontactid="+tradecontactid
						+" and flag="+status;
				else
					sql="select flag from ConsumeCustomer with(nolock) where convert(char(10),buydate,120)='"
						+chatdate+"' and customernick='"+customernick+"' and  tradecontactid="+tradecontactid;
				String flag=this.getDao().strSelect(sql);
				ht.put("status", flag);
			}
			
			vt.setElementAt(ht,i);
		}
		this.OutputStr(this.toJSONArray(vt));		
	}
	public void getChatLog() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String csnick=prop.getProperty("csnick");

		String customernick=prop.getProperty("customernick");
		String chatdate=prop.getProperty("chatdate");
		
		String sql="select chatrecord from CustomerServiceChatpeers with(nolock)"
			+" where tradecontactid="+tradecontactid+" and csnick='"+csnick+"' "
			+" and chatdate='"+chatdate+"' and customernick='"+customernick+"'";
		Hashtable ht=this.getDao().oneRowSelect(sql);

		InputStream in=(InputStream) ht.get("chatrecord");
		String s=StreamUtil.InputStreamToStr(in, "GBK");		
		this.OutputStr(s);
	}
	public void doUpdate() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String csnick=prop.getProperty("csnick");
		String valid=prop.getProperty("valid");		
		String customernick=prop.getProperty("customernick");
		String chatdate=prop.getProperty("chatdate");
		
		checkRole();
		
		String sql="update CustomerServiceChatpeers set valid="+valid
			+" where tradecontactid="+tradecontactid+" and csnick='"+csnick+"' "
			+" and chatdate='"+chatdate+"' and customernick='"+customernick+"'";
		this.getDao().execute(sql);
	}
	private void checkRole() throws Exception
	{
		String sql="select count(*) from ecs_userrole with(nolock) where userid ="
			+this.getUserInfo().getUserid()+" and roleid in(1,7)";
		if (this.getDao().intSelect(sql)==0)
			throw new JException("你没有修改记录的权限,请联系系统管理员!");
	}
	public void doUpdateExcepttime() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String csnick=prop.getProperty("csnick");
		String exceptstarttime=prop.getProperty("exceptstarttime");
		String exceptendtime=prop.getProperty("exceptendtime");
		String totaltime=prop.getProperty("totaltime");
		String responsenum=prop.getProperty("responsenum");		
		String customernick=prop.getProperty("customernick");
		String chatdate=prop.getProperty("chatdate");
		
		long exceptsecond=timeformat.parse(exceptendtime).getTime()-timeformat.parse(exceptstarttime).getTime();
		
		long avgresponsesecond=(Long.valueOf(totaltime).longValue()-exceptsecond)/(Integer.valueOf(responsenum).intValue()*1000);
		
		String sql="update CustomerServiceChatpeers set exceptstarttime='"+exceptstarttime+"',"
			+"exceptendtime='"+exceptendtime+"',avgresponsesecond="+avgresponsesecond
			+" where tradecontactid="+tradecontactid+" and csnick='"+csnick+"' "
			+" and chatdate='"+chatdate+"' and customernick='"+customernick+"'";
		this.getDao().execute(sql);
		
		this.OutputStr(String.valueOf(avgresponsesecond));
	}
	private String[] recalculated(String tradecontactid,String csnick,
			String customernick,String chatdate,String exceptstarttime,String exceptendtime)
	throws Exception
	{
		boolean askflag=false;
		int responsenum=0;
		Calendar askcd = Calendar.getInstance();
		Calendar answercd = Calendar.getInstance();
		long totaltime=0;
		String asktime="";
		String answertime="";

		boolean isnotworktime=false; //是否为非工作时间提问

		
		String sql="select starttime,endtime from BusinessPeriod with(nolock) where tradecontactid="+tradecontactid;
		Hashtable httime=this.getDao().oneRowSelect(sql);
	
		String nonworkstarttime=httime.get("starttime").toString();
		String nonworkendtime=httime.get("endtime").toString();
		
		sql="select direction,msgtime,content from CustomerServiceChatlog with(nolock) "
			+"where tradecontactid="+tradecontactid+" and csnick='"+csnick+"' "
			+"and customernick='"+customernick+"' and chatdate='"+chatdate+"' "
			+"order by msgtime";
		Vector vt=this.getDao().multiRowSelect(sql);
		
		for (int i=0;i<vt.size();i++)
		{
			Hashtable msg=(Hashtable) vt.get(i);
			int direction=Integer.valueOf(msg.get("direction").toString()).intValue();
			String stime=msg.get("msgtime").toString();
		

			if (direction==1)
			{				
				asktime=stime;
				askflag=true;
				if ((this.timeformat.parse(asktime.substring(asktime.indexOf(" ")+1)).compareTo(this.timeformat.parse(nonworkstarttime))>0)
						|| (this.timeformat.parse(asktime.substring(asktime.indexOf(" ")+1)).compareTo(this.timeformat.parse(nonworkendtime))<0))
					isnotworktime=true;
				else
					isnotworktime=false;
			}
			else
			{
		
				if (askflag && !isnotworktime)
				{
					answertime=stime;
					responsenum=responsenum+1;					
					askcd.setTime(Formatter.parseDate(asktime, Formatter.DATE_TIME_FORMAT));
					answercd.setTime(Formatter.parseDate(answertime, Formatter.DATE_TIME_FORMAT));
					totaltime=totaltime+(answercd.getTimeInMillis()-askcd.getTimeInMillis());
					askflag=false;		
					isnotworktime=false;
				}
			}

		}		
		
		long avganswertime=0;
		if (responsenum!=0)
			avganswertime=totaltime/(responsenum*1000);
		
		return new String[]{String.valueOf(avganswertime),String.valueOf(responsenum)};
	}
	
	public void doTransaction(String action) throws Exception {
		
		if (action.equalsIgnoreCase("gettradecontacts"))
			getTradeContacts();
		if (action.equalsIgnoreCase("getcsamount"))
			getCSAmount();
		if (action.equalsIgnoreCase("getallcsamount"))
			getAllCSAmount();
		if (action.equalsIgnoreCase("search"))
			doSearch();	
		if (action.equalsIgnoreCase("getchatlog"))
			getChatLog();	
		if (action.equalsIgnoreCase("update"))
				doUpdate();
		if (action.equalsIgnoreCase("updateexcepttime"))
			doUpdateExcepttime();
		
	}

}
