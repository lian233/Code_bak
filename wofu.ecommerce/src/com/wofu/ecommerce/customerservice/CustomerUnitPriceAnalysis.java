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

public class CustomerUnitPriceAnalysis extends BusinessObject{

	private static final DecimalFormat df=new DecimalFormat("0.00");

	public void getWholeCustomerUnitPriceAnalysis() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		//总订单
		sql="select convert(char(10),buydate,120) sdate,sum(paymoney) totalbuymoney,"
				+"count(distinct customernick) totalbuynum from ConsumeCustomer with(nolock) "
				+"where convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and tradecontactid="+tradecontactid+" and flag<>2 and returnmoney is null  "
				+"group by convert(char(10),buydate,120) order by convert(char(10),buydate,120)";
		Vector vttotalmoeny=this.getDao().multiRowSelect(sql);
		

		//咨询总金额
		sql="select convert(char(10),a.buydate,120) sdate,sum(isnull(a.paymoney,0.00)) paymoney,count(distinct a.customernick) paynum "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,customernick,chatdate from CustomerServiceChatpeers ch  with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+" and valid=1  and csnick in(select usernick from CustomerServiceAccount where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" and a.flag<>2 and returnmoney is null group by convert(char(10),buydate,120)";
		Vector vtasktotalmoney=this.getDao().multiRowSelect(sql);
		
		//付款金额
		sql="select convert(char(10),buydate,120) sdate,sum(isnull(paymoney,0.00)) paymoney,count(distinct customernick) paynum from ConsumeCustomer with(nolock) "
				+"where flag=1  and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and tradecontactid="+tradecontactid+" group by convert(char(10),buydate,120)";
		Vector vtpaymoney=this.getDao().multiRowSelect(sql);
		
		//咨询付款金额
		sql="select convert(char(10),a.buydate,120) sdate,sum(isnull(a.paymoney,0.00)) paymoney,count(distinct a.customernick) paynum "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+"  and valid=1 and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.flag=1  and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" group by convert(char(10),buydate,120)";
		
		Vector vtaskpaymoney=this.getDao().multiRowSelect(sql);
		

		for(int i=0;i<vttotalmoeny.size();i++)
		{

			Hashtable ht=(Hashtable) vttotalmoeny.get(i);
			String sdate=ht.get("sdate").toString();
							
			ht.put("asktotalmoney", Double.valueOf(getSdateMoney(vtasktotalmoney,sdate)));
			
			ht.put("askbuynum", Integer.valueOf(getSdateBuyTime(vtasktotalmoney,sdate)));
			
			ht.put("totalpaymoney", Double.valueOf(getSdateMoney(vtpaymoney,sdate)));
			
			ht.put("totalpaynum", Integer.valueOf(getSdateBuyTime(vtpaymoney,sdate)));
			
			ht.put("askpaymoney", Double.valueOf(getSdateMoney(vtaskpaymoney,sdate)));
			
			ht.put("askpaynum", Integer.valueOf(getSdateBuyTime(vtaskpaymoney,sdate)));
		
		}
		this.OutputStr(this.toJSONArray(vttotalmoeny));
		
	}
	private int getSdateBuyTime(Vector vt,String sdate)
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
	private double getSdateMoney(Vector vt,String sdate)
	{
		double paymoney=0.00;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (sdate.equalsIgnoreCase(ht.get("sdate").toString()))
			{
				paymoney=Double.valueOf(ht.get("paymoney").toString()).doubleValue();
			}
		}
		return paymoney;
	}
	
	public void getWangWangCustomerUnitPriceCompare() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
		
		//咨询总金额
		if (tradecontactid.equals("1"))
		{
			sql="select b.csnick,sum(isnull(a.paymoney,0.00)) asktotalmoney,count(distinct a.customernick) askbuynum "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,csnick,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+"  and valid=1 and csnick in"
				+"(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" and a.flag<>2 group by csnick ";
		}
		else
		{
			sql="select b.csnick,sum(isnull(a.paymoney,0.00)) asktotalmoney,count(distinct a.customernick) askbuynum "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,csnick,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+"  and valid=1 and csnick in"
				+"(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" and a.flag<>2 group by csnick ";
		}
		
		sql=sql+" order by csnick";
		
		Vector vtasktotalmoney=this.getDao().multiRowSelect(sql);
		
		
		//咨询付款金额
		sql="select b.csnick,sum(isnull(a.paymoney,0.00)) paymoney,count(distinct a.customernick) paynum "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,csnick,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+" and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.flag=1 and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" group by b.csnick";
		Vector vtaskpaymoney=this.getDao().multiRowSelect(sql);
		

		for(int i=0;i<vtasktotalmoney.size();i++)
		{

			Hashtable ht=(Hashtable) vtasktotalmoney.get(i);
			String csnick=ht.get("csnick").toString();
							
			ht.put("askpaymoney", Double.valueOf(getcsnickMoney(vtaskpaymoney,csnick)));
			
			ht.put("askpaynum", Integer.valueOf(getcsnickBuyTime(vtaskpaymoney,csnick)));
		
		}
		this.OutputStr(this.toJSONArray(vtasktotalmoney));
		
	}
	
	private int getcsnickBuyTime(Vector vt,String csnick)
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
	private double getcsnickMoney(Vector vt,String csnick)
	{
		double paymoney=0.00;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (csnick.equalsIgnoreCase(ht.get("csnick").toString()))
			{
				paymoney=Double.valueOf(ht.get("paymoney").toString()).doubleValue();
			}
		}
		return paymoney;
	}

	public void getWangWangCustomerUnitPrice() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String csnick=prop.getProperty("csnick");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		//	咨询总金额
		sql="select convert(char(10),a.buydate,120) sdate,sum(isnull(a.paymoney,0.00)) asktotalmoney,count(distinct a.customernick) askbuynum "
				+"from ConsumeCustomer a with(nolock),CustomerServiceChatpeers b with(nolock) "
				+"where a.tradecontactid=b.tradecontactid and valid=1 and a.customernick=b.customernick and b.csnick='"+csnick+"' "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" and a.flag<>2  "
				+"group by convert(char(10),a.buydate,120) ";

		if (tradecontactid.equals("1"))
		{
			sql=sql+" having count(distinct a.customernick)>20";
		}
		sql=sql+" order by convert(char(10),a.buydate,120)";
		
		Vector vtasktotalmoney=this.getDao().multiRowSelect(sql);
		
		
		//咨询付款金额
		sql="select convert(char(10),a.buydate,120) sdate,sum(isnull(a.paymoney,0.00)) paymoney,count(distinct a.customernick) paynum "
				+"from ConsumeCustomer a with(nolock),CustomerServiceChatpeers b with(nolock) "
				+"where a.flag=1 and a.tradecontactid=b.tradecontactid and valid=1 and a.customernick=b.customernick and b.csnick='"+csnick+"' "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" group by convert(char(10),a.buydate,120)";
		Vector vtaskpaymoney=this.getDao().multiRowSelect(sql);
		

		for(int i=0;i<vtasktotalmoney.size();i++)
		{

			Hashtable ht=(Hashtable) vtasktotalmoney.get(i);
			String sdate=ht.get("sdate").toString();
							
			ht.put("askpaymoney", Double.valueOf(getSdateMoney(vtaskpaymoney,sdate)));

			ht.put("askpaynum", Integer.valueOf(getSdateBuyTime(vtaskpaymoney,sdate)));
		
		}

		this.OutputStr(this.toJSONArray(vtasktotalmoney));
		
	}
	
	public void doTransaction(String action) throws Exception {
		
		if (action.equalsIgnoreCase("getwholecustomerunitpriceanalysis"))
			getWholeCustomerUnitPriceAnalysis();
		
		if (action.equalsIgnoreCase("getwangwangcustomerunitpricecompare"))
			getWangWangCustomerUnitPriceCompare();
		
		if (action.equalsIgnoreCase("getwangwangcustomerunitprice"))
			getWangWangCustomerUnitPrice();
		
		
	}
}
