package com.wofu.ecommerce.customerservice;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.wofu.base.util.BusinessObject;
import com.wofu.common.tools.conv.Convert;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class WangWangCompare extends BusinessObject{
	
	private void doSearch() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		
		//×ÉÑ¯×Ü½ð¶î
		String sql="select b.csnick,sum(isnull(a.paymoney,0.00)) asktotalmoney "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,csnick,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+"  and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" and a.flag<>2  group by b.csnick ";
		if (tradecontactid.equals("1"))
		{
			sql=sql+" having count(*)>20";
		}
		
		sql=sql+" order by b.csnick";
		
		Vector vtasktotalmoeny=this.getDao().multiRowSelect(sql);
		
		//Î´×ÉÑ¯½ð¶î£½×Ü¶©µ¥½ð¶î-×ÉÑ¯×Ü½ð¶î
		
		//×ÉÑ¯È¡Ïû½ð¶î
		sql="select b.csnick,sum(isnull(a.paymoney,0.00)) paymoney "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,csnick,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+"  and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.flag=2 and datediff(second,a.buydate,a.canceldate)<3*24*60*60 and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" group by b.csnick";
		Vector vtaskcancelmoeny=this.getDao().multiRowSelect(sql);
		
		//×ÉÑ¯Î´¸¶¿î½ð¶î
		sql="select b.csnick,sum(isnull(a.paymoney,0.00)) paymoney "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,csnick,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+"  and valid=1 and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where (a.flag=0 or (a.flag=2 and datediff(second,a.buydate,a.canceldate)>=3*24*60*60)) and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" group by b.csnick";
		Vector vtaskunpaymoney=this.getDao().multiRowSelect(sql);
		
		//×ÉÑ¯¸¶¿î½ð¶î
		sql="select b.csnick,sum(isnull(a.paymoney,0.00)) paymoney,count(distinct a.customernick) paynum "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,csnick,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+"  and valid=1  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),ch.chatdate,120)>='"
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
		
		sql="select csnick,count(*) validreplynum from (select distinct tradecontactid,csnick,chatdate,customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
		+" and valid=1 and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),chatdate,120)>='"
		+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
		+"and convert(char(10),chatdate,120)<='"+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') a " 		
		+"group by csnick order by csnick";
		Vector vtvalidreplynum=this.getDao().multiRowSelect(sql);
		
		//×ÉÑ¯¸¶¿î×Ü½ð¶î
		sql="select sum(isnull(a.paymoney,0.00)) paymoney "
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
				+"and a.tradecontactid="+tradecontactid;
		String asktotalpaymoney=this.getDao().strSelect(sql);
		
		for(int i=0;i<vtasktotalmoeny.size();i++)
		{
			Hashtable ht=(Hashtable) vtasktotalmoeny.get(i);
			String csnick=ht.get("csnick").toString();

			ht.put("askcancelmoney", Double.valueOf(getcsnickMoney(vtaskcancelmoeny,csnick)));

			ht.put("askunpaymoney", Double.valueOf(getcsnickMoney(vtaskunpaymoney,csnick)));
	
			ht.put("askpaymoney", Double.valueOf(getcsnickMoney(vtaskpaymoney,csnick)));
	
			String askpaymoneyrate=String.valueOf(Double.valueOf(getcsnickMoney(vtaskpaymoney,csnick)).doubleValue()/Double.valueOf(asktotalpaymoney).doubleValue());
			
			ht.put("askpaymoneyrate", askpaymoneyrate);
			
			ht.put("askpaynum", Integer.valueOf(getcsnickBuyTime(vtaskpaymoney,csnick)));
			
			ht.put("validreplynum", Integer.valueOf(getcsnickReplyNum(vtvalidreplynum,csnick)));
						
			vtasktotalmoeny.setElementAt(ht, i);			
		}
		this.OutputStr(this.toJSONArray(vtasktotalmoeny));
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
	
	private int getcsnickReplyNum(Vector vt,String csnick)
	{
		int validreplynum=0;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (csnick.equalsIgnoreCase(ht.get("csnick").toString()))
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
