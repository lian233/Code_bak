package com.wofu.ecommerce.salemanager;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.wofu.base.util.BusinessObject;
import com.wofu.common.tools.conv.Convert;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class SaleShop extends BusinessObject {

	private void doSearch() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		//总订单金额
		String sql="select convert(char(10),buydate,120) sdate,sum(paymoney) totalmoney from ConsumeCustomer "
				+"where convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and tradecontactid="+tradecontactid+" and flag<>2  group by convert(char(10),buydate,120) order by convert(char(10),buydate,120)";
		Vector vttotalmoeny=this.getDao().multiRowSelect(sql);
		
		
		//取消金额
		sql="select convert(char(10),buydate,120) sdate,sum(isnull(paymoney,0.00)) paymoney from ConsumeCustomer "
				+"where flag=2 and datediff(second,buydate,canceldate)<3*24*60*60 and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and tradecontactid="+tradecontactid+" group by convert(char(10),buydate,120)";
		Vector vtcancelmoney=this.getDao().multiRowSelect(sql);
		
		//退款金额
		sql="select convert(char(10),buydate,120) sdate,sum(isnull(returnmoney,0.00)) paymoney from ConsumeCustomer "
				+"where convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and tradecontactid="+tradecontactid+" group by convert(char(10),buydate,120)";
		Vector vtreturnmoney=this.getDao().multiRowSelect(sql);
		
		//未付款金额
		sql="select convert(char(10),buydate,120) sdate,sum(isnull(paymoney,0.00)) paymoney from ConsumeCustomer "
				+"where (flag=0 or (flag=2 and datediff(second,buydate,canceldate)>=3*24*60*60)) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and tradecontactid="+tradecontactid+" group by convert(char(10),buydate,120)";
		Vector vtunpaymoeny=this.getDao().multiRowSelect(sql);
		
		//付款金额
		sql="select convert(char(10),buydate,120) sdate,sum(isnull(paymoney,0.00)) paymoney,count(distinct customernick) paynum from ConsumeCustomer "
				+"where flag=1 and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and tradecontactid="+tradecontactid+" group by convert(char(10),buydate,120)";
		Vector vtpaymoeny=this.getDao().multiRowSelect(sql);
		
		
		//咨询总金额
		sql="select convert(char(10),a.buydate,120) sdate,sum(isnull(a.paymoney,0.00)) paymoney "
				+"from ConsumeCustomer a,(select distinct tradecontactid,customernick,chatdate from CustomerServiceChatpeers ch "
				+"where ch.tradecontactid="+tradecontactid+"  and csnick in(select usernick from CustomerServiceAccount where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" and a.flag<>2 group by convert(char(10),buydate,120)";
		Vector vtasktotalmoeny=this.getDao().multiRowSelect(sql);
		
		//未咨询金额＝总订单金额-咨询总金额
		
		//咨询取消金额
		sql="select convert(char(10),a.buydate,120) sdate,sum(isnull(a.paymoney,0.00)) paymoney "
				+"from ConsumeCustomer a,(select distinct tradecontactid,customernick,chatdate from CustomerServiceChatpeers ch "
				+"where ch.tradecontactid="+tradecontactid+"  and csnick in(select usernick from CustomerServiceAccount where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.flag=2 and datediff(second,a.buydate,a.canceldate)<3*24*60*60 and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" group by convert(char(10),buydate,120)";
		Vector vtaskcancelmoeny=this.getDao().multiRowSelect(sql);
		
		//咨询未付款金额
		sql="select convert(char(10),a.buydate,120) sdate,sum(isnull(a.paymoney,0.00)) paymoney "
				+"from ConsumeCustomer a,(select distinct tradecontactid,customernick,chatdate from CustomerServiceChatpeers ch "
				+"where ch.tradecontactid="+tradecontactid+"  and csnick in(select usernick from CustomerServiceAccount where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where (a.flag=0 or (a.flag=2 and datediff(second,a.buydate,a.canceldate)>=3*24*60*60)) and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" group by convert(char(10),buydate,120)";
		Vector vtaskunpaymoney=this.getDao().multiRowSelect(sql);
		
		//咨询付款金额
		sql="select convert(char(10),a.buydate,120) sdate,sum(isnull(a.paymoney,0.00)) paymoney,count(distinct a.customernick) paynum "
				+"from ConsumeCustomer a,(select distinct tradecontactid,customernick,chatdate from CustomerServiceChatpeers ch "
				+"where ch.tradecontactid="+tradecontactid+"  and csnick in(select usernick from CustomerServiceAccount where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.flag=1 and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" group by convert(char(10),buydate,120)";
		Vector vtaskpaymoney=this.getDao().multiRowSelect(sql);
		
		sql="select convert(char(10),chatdate,120) sdate,count(*) validreplynum from (select distinct tradecontactid,chatdate,customernick from CustomerServiceChatpeers where tradecontactid="+tradecontactid
		+" and valid=1  and csnick in(select usernick from CustomerServiceAccount where roletype=0) and convert(char(10),chatdate,120)>='"
		+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
		+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') a " 		
		+"group by convert(char(10),chatdate,120) order by convert(char(10),chatdate,120)";
		Vector vtvalidreplynum=this.getDao().multiRowSelect(sql);
		
		for(int i=0;i<vttotalmoeny.size();i++)
		{
			Hashtable ht=(Hashtable) vttotalmoeny.get(i);
			String sdate=ht.get("sdate").toString();
			double totalmoney=Double.valueOf(ht.get("totalmoney").toString()).doubleValue();
	
			ht.put("cancelmoney", Double.valueOf(getSdateMoney(vtcancelmoney,sdate)));
		
			ht.put("returnmoney", Double.valueOf(getSdateMoney(vtreturnmoney,sdate)));
			
			ht.put("unpaymoney", Double.valueOf(getSdateMoney(vtunpaymoeny,sdate)));
		
			ht.put("paymoney", Double.valueOf(getSdateMoney(vtpaymoeny,sdate)));
			
			ht.put("paynum", Integer.valueOf(getSdateBuyTime(vtpaymoeny,sdate)));
					
			ht.put("asktotalmoney", Double.valueOf(getSdateMoney(vtasktotalmoeny,sdate)));
			
			ht.put("unasktotalmoney", Double.valueOf(totalmoney-getSdateMoney(vtasktotalmoeny,sdate)));
		
			ht.put("askcancelmoney", Double.valueOf(getSdateMoney(vtaskcancelmoeny,sdate)));

			ht.put("askunpaymoney", Double.valueOf(getSdateMoney(vtaskunpaymoney,sdate)));
	
			ht.put("askpaymoney", Double.valueOf(getSdateMoney(vtaskpaymoney,sdate)));
	
			ht.put("askpaynum", Integer.valueOf(getSdateBuyTime(vtaskpaymoney,sdate)));
			
			ht.put("validreplynum", Integer.valueOf(getSdateReplyNum(vtvalidreplynum,sdate)));
						
			vttotalmoeny.setElementAt(ht, i);			
		}
		this.OutputStr(this.toJSONArray(vttotalmoeny));
		
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
	
	private int getSdateReplyNum(Vector vt,String sdate)
	{
		int validreplynum=0;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (sdate.equalsIgnoreCase(ht.get("sdate").toString()))
			{
				validreplynum=Integer.valueOf(ht.get("validreplynum").toString()).intValue();
			}
		}
		return validreplynum;
	}
	
	public void doTransaction(String action) throws Exception {		
		if (action.equalsIgnoreCase("search"))
			doSearch();	
	}

}
