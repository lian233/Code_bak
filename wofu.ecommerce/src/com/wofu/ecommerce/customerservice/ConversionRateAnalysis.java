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

public class ConversionRateAnalysis extends BusinessObject{

	private static final DecimalFormat df=new DecimalFormat("0.00");

	public void getWholeConversionRateAnalysis() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		sql="select convert(char(10),chatdate,120) sdate,count(*) validreplynum "
			+"from (select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1 and csnick in(select usernick from CustomerServiceAccount where roletype=0)  and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') a " 		
		+"group by convert(char(10),chatdate,120) order by convert(char(10),chatdate,120)";
		
		Vector vtvalidreplynum=this.getDao().multiRowSelect(sql);
		
		sql="select convert(char(10),a.buydate,120) sdate,count(distinct a.customernick) buyernum from ConsumeCustomer a with(nolock), "
			+"(select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1 and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120)"
			+"group by convert(char(10),buydate,120) ";
		Vector vtbuyernum=this.getDao().multiRowSelect(sql);
	

		sql="select convert(char(10),buydate,120) sdate,count(distinct a.customernick) paynum from ConsumeCustomer a with(nolock),"
			+"(select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where  a.flag=1 and a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) "
			+"group by convert(char(10),buydate,120) ";
		Vector vtpaynum=this.getDao().multiRowSelect(sql);
		
		for(int i=0;i<vtvalidreplynum.size();i++)
		{

			Hashtable ht=(Hashtable) vtvalidreplynum.get(i);
			String sdate=ht.get("sdate").toString();
			
			int validreplynum=Integer.valueOf(ht.get("validreplynum").toString()).intValue();
	
			int buyernum=getSdateBuyerNum(vtbuyernum,sdate);
			
			ht.put("buyernum", Integer.valueOf(buyernum));
		
			int paynum=getSdatePayNum(vtpaynum,sdate);
			
			ht.put("paynum", Integer.valueOf(paynum));
		
		}
		this.OutputStr(this.toJSONArray(vtvalidreplynum));
		
	}
	private int getSdateBuyerNum(Vector vt,String sdate)
	{
		int buyernum=0;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (sdate.equalsIgnoreCase(ht.get("sdate").toString()))
			{
				buyernum=Integer.valueOf(ht.get("buyernum").toString()).intValue();
			}
		}
		return buyernum;
	}
	private int getSdatePayNum(Vector vt,String sdate)
	{
		int paynum=0;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (sdate.equalsIgnoreCase(ht.get("sdate").toString()))
			{
				paynum=Integer.valueOf(ht.get("paynum").toString()).intValue();
			}
		}
		return paynum;
	}
	

	public void getConversionRateCompare() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		sql="select csnick,count(*) validreplynum from (select distinct tradecontactid,chatdate,csnick,customernick "
			+"from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') a " 		
		+"group by csnick ";
		
		if (tradecontactid.equalsIgnoreCase("1"))
		{
			sql=sql+" having count(*)>20";
		}
		sql=sql+" order by csnick";
		
		Vector vtvalidreplynum=this.getDao().multiRowSelect(sql);
		
		sql="select csnick,count(distinct a.customernick) buyernum from ConsumeCustomer a with(nolock), "
			+"(select distinct tradecontactid,chatdate,csnick,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) "
			+"group by csnick ";
		Vector vtbuyernum=this.getDao().multiRowSelect(sql);
	

		sql="select csnick,count(distinct a.customernick) paynum from ConsumeCustomer a with(nolock),"
			+"(select distinct tradecontactid,csnick,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where  a.flag=1 and a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) "
			+"group by csnick ";
		Vector vtpaynum=this.getDao().multiRowSelect(sql);
		
		for(int i=0;i<vtvalidreplynum.size();i++)
		{

			Hashtable ht=(Hashtable) vtvalidreplynum.get(i);
			String csnick=ht.get("csnick").toString();
			
			int validreplynum=Integer.valueOf(ht.get("validreplynum").toString()).intValue();
	
			int buyernum=getcsnickBuyerNum(vtbuyernum,csnick);
			
			ht.put("buyernum", Integer.valueOf(buyernum));
		
			int paynum=getcsnickPayNum(vtpaynum,csnick);
			
			ht.put("paynum", Integer.valueOf(paynum));
		
		}
		this.OutputStr(this.toJSONArray(vtvalidreplynum));
		
	}
	
	private int getcsnickBuyerNum(Vector vt,String csnick)
	{
		int buyernum=0;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (csnick.equalsIgnoreCase(ht.get("csnick").toString()))
			{
				buyernum=Integer.valueOf(ht.get("buyernum").toString()).intValue();
			}
		}
		return buyernum;
	}
	private int getcsnickPayNum(Vector vt,String csnick)
	{
		int paynum=0;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (csnick.equalsIgnoreCase(ht.get("csnick").toString()))
			{
				paynum=Integer.valueOf(ht.get("paynum").toString()).intValue();
			}
		}
		return paynum;
	}
	public void getWangWangConversionRate() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String csnick=prop.getProperty("csnick");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		sql="select convert(char(10),chatdate,120) sdate,count(*) validreplynum from "
			+"(select distinct tradecontactid,chatdate,customernick from "
			+"CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1 and csnick='"+csnick+"' and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') a " 		
		+"group by convert(char(10),chatdate,120) ";
		
		if (tradecontactid.equalsIgnoreCase("1"))
		{
			sql=sql+" having count(*)>20";
		}
		
		sql=sql+"order by convert(char(10),chatdate,120)";
		
		Vector vtvalidreplynum=this.getDao().multiRowSelect(sql);
		
		sql="select convert(char(10),a.buydate,120) sdate,count(distinct a.customernick) buyernum from ConsumeCustomer a with(nolock), "
			+"(select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1 and csnick='"+csnick+"' and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120)"
			+"group by convert(char(10),buydate,120) ";
		Vector vtbuyernum=this.getDao().multiRowSelect(sql);
	

		sql="select convert(char(10),buydate,120) sdate,count(distinct a.customernick) paynum from ConsumeCustomer a with(nolock),"
			+"(select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1 and csnick='"+csnick+"' and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where  a.flag=1  and a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) "
			+"group by convert(char(10),buydate,120) ";
		Vector vtpaynum=this.getDao().multiRowSelect(sql);
		
		for(int i=0;i<vtvalidreplynum.size();i++)
		{

			Hashtable ht=(Hashtable) vtvalidreplynum.get(i);
			String sdate=ht.get("sdate").toString();
			
			int validreplynum=Integer.valueOf(ht.get("validreplynum").toString()).intValue();
	
			int buyernum=getSdateBuyerNum(vtbuyernum,sdate);
			
			ht.put("buyernum", Integer.valueOf(buyernum));
		
			int paynum=getSdatePayNum(vtpaynum,sdate);
			
			ht.put("paynum", Integer.valueOf(paynum));
		
		}
		this.OutputStr(this.toJSONArray(vtvalidreplynum));
		
	}
	public void doTransaction(String action) throws Exception {
		
		if (action.equalsIgnoreCase("getwholeconversionrateanalysis"))
			getWholeConversionRateAnalysis();
		if (action.equalsIgnoreCase("getconversionratecompare"))
			getConversionRateCompare();
		if (action.equalsIgnoreCase("getwangwangconversionrate"))
			getWangWangConversionRate();
		
		
	}
}
