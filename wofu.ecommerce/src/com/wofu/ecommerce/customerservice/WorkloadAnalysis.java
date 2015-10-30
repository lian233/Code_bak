package com.wofu.ecommerce.customerservice;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.wofu.base.util.BusinessObject;
import com.wofu.common.tools.conv.Convert;
import com.wofu.common.tools.util.StreamUtil;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.Formatter;

public class WorkloadAnalysis extends BusinessObject{

	private static final DecimalFormat df=new DecimalFormat("0.00");

	public void getWholeWorkloadAnalysis() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		sql="select convert(char(10),chatdate,120) sdate,count(*) validreplynum,sum(answeramount) answeramount,"
			+"sum(responseamount) responseamount,sum(responsewords) responsewords,sum(avgresponsesecond)/count(*) avgresponsesecond "
			+"from (select tradecontactid,chatdate,customernick,sum(answeramount) answeramount,"
			+"sum(responseamount) responseamount,sum(responsewords) responsewords, "
			+"sum(avgresponsesecond)/count(*) avgresponsesecond from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and charindex(':',customernick)=0 and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' group by tradecontactid,chatdate,customernick) a "
			+"group by convert(char(10),chatdate,120) order by convert(char(10),chatdate,120)";
		
		Vector vtvalidreplynum=this.getDao().multiRowSelect(sql);

		sql="select convert(char(10),chatdate,120) sdate,count(*) noreplynum from (select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and isresponse=0 and charindex(':',customernick)=0 and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') a "
			+"group by convert(char(10),chatdate,120) order by convert(char(10),chatdate,120)";
		
		Vector vtnoreplynum=this.getDao().multiRowSelect(sql);

		for(int i=0;i<vtvalidreplynum.size();i++)
		{

			Hashtable ht=(Hashtable) vtvalidreplynum.get(i);
			String sdate=ht.get("sdate").toString();
				
			int noreplynum=getSdateNoReplyNum(vtnoreplynum,sdate);
			
			ht.put("noreplynum", Integer.valueOf(noreplynum));
		
		}
		this.OutputStr(this.toJSONArray(vtvalidreplynum));
		
	}
	private int getSdateNoReplyNum(Vector vt,String sdate)
	{
		int noreplynum=0;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (sdate.equalsIgnoreCase(ht.get("sdate").toString()))
			{
				noreplynum=Integer.valueOf(ht.get("noreplynum").toString()).intValue();
			}
		}
		return noreplynum;
	}
	
	public void getWangWangWorkloadCompare() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		sql="select csnick,count(*) validreplynum,sum(answeramount) answeramount,"
			+"sum(responseamount) responseamount,sum(responsewords) responsewords, "
			+"sum(avgresponsesecond)/count(*) avgresponsesecond from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick in(select usernick from CustomerServiceAccount  with(nolock)where roletype=0) and charindex(':',customernick)=0 and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"group by csnick ";
		
		if (tradecontactid.equals("1"))
		{
			sql=sql+" having count(*)>20";
		}
			
		sql=sql+" order by csnick";
		
		Vector vtvalidreplynum=this.getDao().multiRowSelect(sql);

		sql="select csnick,count(*) noreplynum from CustomerServiceChatpeers  with(nolock) where tradecontactid="+tradecontactid
			+" and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) "
			+"and isresponse=0 and charindex(':',customernick)=0 and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"group by csnick order by csnick";
		
		Vector vtnoreplynum=this.getDao().multiRowSelect(sql);

		for(int i=0;i<vtvalidreplynum.size();i++)
		{
			Hashtable ht=(Hashtable) vtvalidreplynum.get(i);
			String csnick=ht.get("csnick").toString();
				
			int noreplynum=getcsnickNoReplyNum(vtnoreplynum,csnick);
			
			ht.put("noreplynum", Integer.valueOf(noreplynum));
		
		}
		this.OutputStr(this.toJSONArray(vtvalidreplynum));
		
	}
	private int getcsnickNoReplyNum(Vector vt,String csnick)
	{
		int noreplynum=0;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (csnick.equalsIgnoreCase(ht.get("csnick").toString()))
			{
				noreplynum=Integer.valueOf(ht.get("noreplynum").toString()).intValue();
			}
		}
		return noreplynum;
	}
	public void getWangWangWorkload() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String csnick=prop.getProperty("csnick");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		sql="select convert(char(10),chatdate,120) sdate,count(*) validreplynum,sum(answeramount) answeramount,"
			+"sum(responseamount) responseamount,sum(responseamount) responseamount,sum(responsewords) responsewords, "
			+"sum(avgresponsesecond)/count(*) avgresponsesecond from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and charindex(':',customernick)=0 and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"group by convert(char(10),chatdate,120) ";
		
		if (tradecontactid.equals("1"))
		{
			sql=sql+" having count(*)>20";
		}
		
		sql=sql+"order by convert(char(10),chatdate,120)";
		
		Vector vtvalidreplynum=this.getDao().multiRowSelect(sql);

		sql="select convert(char(10),chatdate,120) sdate,count(*) noreplynum from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and isresponse=0 and charindex(':',customernick)=0 and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"group by convert(char(10),chatdate,120) order by convert(char(10),chatdate,120)";
		
		Vector vtnoreplynum=this.getDao().multiRowSelect(sql);

		for(int i=0;i<vtvalidreplynum.size();i++)
		{

			Hashtable ht=(Hashtable) vtvalidreplynum.get(i);
			String sdate=ht.get("sdate").toString();
				
			int noreplynum=getSdateNoReplyNum(vtnoreplynum,sdate);
			
			ht.put("noreplynum", Integer.valueOf(noreplynum));
		
		}
		this.OutputStr(this.toJSONArray(vtvalidreplynum));
		
	}
	
	public void doTransaction(String action) throws Exception {
		
		if (action.equalsIgnoreCase("getwholeworkloadanalysis"))
			getWholeWorkloadAnalysis();
		
		if (action.equalsIgnoreCase("getwangwangworkloadcompare"))
			getWangWangWorkloadCompare();
		
		if (action.equalsIgnoreCase("getwangwangworkload"))
			getWangWangWorkload();
		
		
	}
}
