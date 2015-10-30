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
import com.wofu.common.tools.util.log.Log;

public class CustomerServiceAchievement extends BusinessObject{

	private static final DecimalFormat df=new DecimalFormat("0.00");

	public void getWholeAchievement() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		//接待总数
		sql="select convert(char(10),chatdate,120) sdate,count(*) num from (select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
		+"  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
		+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
		+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') a " 		
		+"group by convert(char(10),chatdate,120) order by convert(char(10),chatdate,120)";
	
		Vector vttotalreplynum=this.getDao().multiRowSelect(sql);
	
		//询单总数
		sql="select convert(char(10),chatdate,120) sdate,count(*) num from (select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount  with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') a " 		
		+"group by convert(char(10),chatdate,120) order by convert(char(10),chatdate,120)";
		
		Vector vtvalidreplynum=this.getDao().multiRowSelect(sql);
		
		//当日下单金额(包括取消、退款)
		sql="select convert(char(10),a.buydate,120) sdate,count(distinct a.customernick) num,sum(paymoney) money from ConsumeCustomer a with(nolock), "
			+"(select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120)  "
			+"group by convert(char(10),buydate,120) ";
		Vector vtbuynum=this.getDao().multiRowSelect(sql);
	
		//最终下单金额(不包括取消、退款)
		sql="select convert(char(10),a.buydate,120) sdate,count(distinct a.customernick) num,sum(paymoney) money from ConsumeCustomer a with(nolock), "
			+"(select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and flag<>2 "
			+"group by convert(char(10),buydate,120) ";
		Vector vtfinalbuynum=this.getDao().multiRowSelect(sql);
		
		//付款金额(包含退款)
		sql="select convert(char(10),buydate,120) sdate,count(distinct a.customernick) num,sum(paymoney) money from ConsumeCustomer a with(nolock),"
			+"(select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.flag=1 and a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) "
			+"group by convert(char(10),buydate,120) ";
		Vector vtpaynum=this.getDao().multiRowSelect(sql);
		
		//最终付款金额(不包括退款)
		sql="select convert(char(10),buydate,120) sdate,count(distinct a.customernick) num,sum(paymoney) money from ConsumeCustomer a with(nolock),"
			+"(select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.flag=1 and returnmoney is null and a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) "
			+"group by convert(char(10),buydate,120) ";
		Vector vtfinalpaynum=this.getDao().multiRowSelect(sql);
		

		//总的最终付款金额(包含静默付款金额)
		sql="select convert(char(10),buydate,120) sdate,count(distinct customernick) totalfinalpaynum,sum(paymoney) totalfinalpaymoney from ConsumeCustomer with(nolock) "
			+"where flag=1 and returnmoney is null and tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"group by convert(char(10),buydate,120) order by convert(char(10),buydate,120)";
		Vector vtfinaltotalpaynum=this.getDao().multiRowSelect(sql);
		
		for(int i=0;i<vtfinaltotalpaynum.size();i++)
		{

			Hashtable ht=(Hashtable) vtfinaltotalpaynum.get(i);
			String sdate=ht.get("sdate").toString();
				
			ht.put("totalreplynum", Integer.valueOf(getSdateNum(vttotalreplynum,sdate)));
			ht.put("validreplynum", Integer.valueOf(getSdateNum(vtvalidreplynum,sdate)));
			
			ht.put("buynum", Integer.valueOf(getSdateNum(vtbuynum,sdate)));
			ht.put("buymoney", Double.valueOf(getSdateMoney(vtbuynum,sdate)));
			
			ht.put("finalbuynum", Integer.valueOf(getSdateNum(vtfinalbuynum,sdate)));
			ht.put("finalbuymoney", Double.valueOf(getSdateMoney(vtfinalbuynum,sdate)));
			
			ht.put("paynum", Integer.valueOf(getSdateNum(vtpaynum,sdate)));
			ht.put("paymoney", Double.valueOf(getSdateMoney(vtpaynum,sdate)));
			
			ht.put("finalpaynum", Integer.valueOf(getSdateNum(vtfinalpaynum,sdate)));
			ht.put("finalpaymoney", Double.valueOf(getSdateMoney(vtfinalpaynum,sdate)));
		
		}
		this.OutputStr(this.toJSONArray(vtfinaltotalpaynum));
		
	}
	private int getSdateNum(Vector vt,String sdate)
	{
		int num=0;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (sdate.equalsIgnoreCase(ht.get("sdate").toString()))
			{
				num=Integer.valueOf(ht.get("num").toString()).intValue();
			}
		}
		return num;
	}
	private double getSdateMoney(Vector vt,String sdate)
	{
		double money=0.00;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (sdate.equalsIgnoreCase(ht.get("sdate").toString()))
			{
				money=Double.valueOf(ht.get("money").toString()).doubleValue();
			}
		}
		return money;
	}
	
	public void getAchievementCompare() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		//接待总数
		sql="select csnick,count(*) totalreplynum from (select distinct tradecontactid,"
			+"csnick,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') a " 		
			+"group by csnick ";
		
		if (tradecontactid.equals("1"))
		{
			sql=sql+" having count(*)>20";
		}
	
		sql=sql+"order by csnick";
		
		Vector vttotalreplynum=this.getDao().multiRowSelect(sql);
	
		//询单总数
		sql="select csnick,count(*) num from (select distinct tradecontactid,csnick,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') a " 		
			+"group by csnick";
		
		Vector vtvalidreplynum=this.getDao().multiRowSelect(sql);
		
		//当日下单金额(包括取消、退款)
		sql="select csnick,count(distinct a.customernick) num,sum(paymoney) money from ConsumeCustomer a with(nolock), "
			+"(select distinct tradecontactid,chatdate,csnick,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120)  "
			+"group by csnick ";
		Vector vtbuynum=this.getDao().multiRowSelect(sql);
	
		//最终下单金额(不包括取消、退款)
		sql="select csnick,count(distinct a.customernick) num,sum(paymoney) money from ConsumeCustomer a with(nolock), "
			+"(select distinct tradecontactid,csnick,chatdate,customernick from CustomerServiceChatpeers  with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and flag<>2 "
			+"group by csnick ";
		Vector vtfinalbuynum=this.getDao().multiRowSelect(sql);
		
		//付款金额(包含退款)
		sql="select csnick,count(distinct a.customernick) num,sum(paymoney) money from ConsumeCustomer a with(nolock),"
			+"(select distinct tradecontactid,csnick,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.flag=1 and a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) "
			+"group by csnick ";
		Vector vtpaynum=this.getDao().multiRowSelect(sql);
		
		//最终付款金额(不包括退款)
		sql="select csnick,count(distinct a.customernick) num,sum(paymoney) money from ConsumeCustomer a with(nolock),"
			+"(select distinct tradecontactid,csnick,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.flag=1 and returnmoney is null and a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) "
			+"group by csnick ";
		Vector vtfinalpaynum=this.getDao().multiRowSelect(sql);
		
		//客服总付款
		sql="select count(distinct a.customernick) totalfinalpaynum,sum(paymoney) totalfinalpaymoney from ConsumeCustomer a with(nolock),"
			+"(select distinct tradecontactid,csnick,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.flag=1 and returnmoney is null and a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) ";
		Hashtable httotalfinalpay=this.getDao().oneRowSelect(sql);
		
		String totalfinalpaynum=httotalfinalpay.get("totalfinalpaynum").toString();
		String totalfinalpaymoney=httotalfinalpay.get("totalfinalpaymoney").toString();
		
		for(int i=0;i<vttotalreplynum.size();i++)
		{

			Hashtable ht=(Hashtable) vttotalreplynum.get(i);
			String csnick=ht.get("csnick").toString();
				
			ht.put("validreplynum", Integer.valueOf(getcsnickNum(vtvalidreplynum,csnick)));
			
			ht.put("buynum", Integer.valueOf(getcsnickNum(vtbuynum,csnick)));
			ht.put("buymoney", Double.valueOf(getcsnickMoney(vtbuynum,csnick)));
			
			String payrate=String.valueOf(Double.valueOf(getcsnickMoney(vtfinalpaynum,csnick)).doubleValue()/Double.valueOf(totalfinalpaymoney).doubleValue());
			
			ht.put("payrate", payrate);
			
			ht.put("finalbuynum", Integer.valueOf(getcsnickNum(vtfinalbuynum,csnick)));
			ht.put("finalbuymoney", Double.valueOf(getcsnickMoney(vtfinalbuynum,csnick)));
			
			ht.put("paynum", Integer.valueOf(getcsnickNum(vtpaynum,csnick)));
			ht.put("paymoney", Double.valueOf(getcsnickMoney(vtpaynum,csnick)));
			
			ht.put("finalpaynum", Integer.valueOf(getcsnickNum(vtfinalpaynum,csnick)));
			ht.put("finalpaymoney", Double.valueOf(getcsnickMoney(vtfinalpaynum,csnick)));
		
		}
		this.OutputStr(this.toJSONArray(vttotalreplynum));
		
	}
	
	public void getWangWangAchievement() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String csnick=prop.getProperty("csnick");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		//接待总数
		sql="select convert(char(10),chatdate,120) sdate,count(*) totalreplynum from (select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') a " 		
			+"group by convert(char(10),chatdate,120) ";
		
		if(tradecontactid.equals("1"))
		{
			sql=sql+" having count(*) >20";
		}
		
		sql=sql+" order by convert(char(10),chatdate,120)";
		
		Vector vttotalreplynum=this.getDao().multiRowSelect(sql);
	
		//询单总数
		sql="select convert(char(10),chatdate,120) sdate,count(*) num from (select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and valid=1 and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') a " 		
		+"group by convert(char(10),chatdate,120) order by convert(char(10),chatdate,120)";
		
		Vector vtvalidreplynum=this.getDao().multiRowSelect(sql);
		
		//当日下单金额(包括取消、退款)
		sql="select convert(char(10),a.buydate,120) sdate,count(distinct a.customernick) num,sum(paymoney) money from ConsumeCustomer a with(nolock), "
			+"(select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and valid=1 and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120)  "
			+"group by convert(char(10),buydate,120) ";
		Vector vtbuynum=this.getDao().multiRowSelect(sql);
	
		//最终下单金额(不包括取消、退款)
		sql="select convert(char(10),a.buydate,120) sdate,count(distinct a.customernick) num,sum(paymoney) money from ConsumeCustomer a with(nolock), "
			+"(select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and valid=1 and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and flag<>2 "
			+"group by convert(char(10),buydate,120) ";
		Vector vtfinalbuynum=this.getDao().multiRowSelect(sql);
		
		//付款金额(包含退款)
		sql="select convert(char(10),buydate,120) sdate,count(distinct a.customernick) num,sum(paymoney) money from ConsumeCustomer a with(nolock),"
			+"(select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and valid=1 and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.flag=1 and a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) "
			+"group by convert(char(10),buydate,120) ";
		Vector vtpaynum=this.getDao().multiRowSelect(sql);
		
		//最终付款金额(不包括退款)
		sql="select convert(char(10),buydate,120) sdate,count(distinct a.customernick) num,sum(paymoney) money from ConsumeCustomer a with(nolock),"
			+"(select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and valid=1 and convert(char(10),chatdate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
			+"where a.flag=1 and returnmoney is null and a.tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)>='"
			+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and convert(char(10),buydate,120)<='"
			+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
			+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) "
			+"group by convert(char(10),buydate,120) ";
		Vector vtfinalpaynum=this.getDao().multiRowSelect(sql);
		
		
		for(int i=0;i<vttotalreplynum.size();i++)
		{

			Hashtable ht=(Hashtable) vttotalreplynum.get(i);
			String sdate=ht.get("sdate").toString();
				
			ht.put("validreplynum", Integer.valueOf(getSdateNum(vtvalidreplynum,sdate)));
			
			ht.put("buynum", Integer.valueOf(getSdateNum(vtbuynum,sdate)));
			ht.put("buymoney", Double.valueOf(getSdateMoney(vtbuynum,sdate)));
			
			ht.put("finalbuynum", Integer.valueOf(getSdateNum(vtfinalbuynum,sdate)));
			ht.put("finalbuymoney", Double.valueOf(getSdateMoney(vtfinalbuynum,sdate)));
			
			ht.put("paynum", Integer.valueOf(getSdateNum(vtpaynum,sdate)));
			ht.put("paymoney", Double.valueOf(getSdateMoney(vtpaynum,sdate)));
			
			ht.put("finalpaynum", Integer.valueOf(getSdateNum(vtfinalpaynum,sdate)));
			ht.put("finalpaymoney", Double.valueOf(getSdateMoney(vtfinalpaynum,sdate)));
		
		}
		this.OutputStr(this.toJSONArray(vttotalreplynum));
		
	}
	private int getcsnickNum(Vector vt,String csnick)
	{
		int num=0;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (csnick.equalsIgnoreCase(ht.get("csnick").toString()))
			{
				num=Integer.valueOf(ht.get("num").toString()).intValue();
			}
		}
		return num;
	}
	private double getcsnickMoney(Vector vt,String csnick)
	{
		double money=0.00;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (csnick.equalsIgnoreCase(ht.get("csnick").toString()))
			{
				money=Double.valueOf(ht.get("money").toString()).doubleValue();
			}
		}
		return money;
	}
	public void doTransaction(String action) throws Exception {
		
		if (action.equalsIgnoreCase("getwholeachievement"))
			getWholeAchievement();
		
		if (action.equalsIgnoreCase("getachievementcompare"))
			getAchievementCompare();
		
		if (action.equalsIgnoreCase("getwangwangachievement"))
			getWangWangAchievement();
	
	}
}
