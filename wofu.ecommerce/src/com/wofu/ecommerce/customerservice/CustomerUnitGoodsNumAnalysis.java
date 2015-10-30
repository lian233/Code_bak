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

public class CustomerUnitGoodsNumAnalysis extends BusinessObject{

	private static final DecimalFormat df=new DecimalFormat("0.00");

	public void getWholeCustomerUnitGoodsNumAnalysis() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		//总客件数
		sql="select convert(char(10),buydate,120) sdate,sum(goodsnum) totalbuygoodsnum,count(distinct customernick) totalbuynum from ConsumeCustomer with(nolock) "
				+"where convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and tradecontactid="+tradecontactid+" and flag<>2  group by convert(char(10),buydate,120) order by convert(char(10),buydate,120)";
		Vector vttotalgoodsnum=this.getDao().multiRowSelect(sql);
		
		//付款客件数
		sql="select convert(char(10),buydate,120) sdate,sum(goodsnum) goodsnum,count(distinct customernick) paynum from ConsumeCustomer with(nolock) "
				+"where flag=1 and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and tradecontactid="+tradecontactid+" and flag<>2  group by convert(char(10),buydate,120)";
		Vector vtpaygoodsnum=this.getDao().multiRowSelect(sql);
		
		//咨询总客件数
		sql="select convert(char(10),a.buydate,120) sdate,sum(goodsnum) goodsnum,count(distinct a.customernick) paynum "
				+"from ConsumeCustomer a with(nolock), (select distinct tradecontactid,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+"  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" and a.flag<>2  group by convert(char(10),buydate,120)";
		Vector vtasktotalgoodsnum=this.getDao().multiRowSelect(sql);
		
		//咨询付款客件数
		sql="select convert(char(10),a.buydate,120) sdate,sum(goodsnum) goodsnum,count(distinct a.customernick) paynum "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+"  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and  convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.flag=1 and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" and a.flag<>2  group by convert(char(10),buydate,120)";
		Vector vtaskpaygoodsnum=this.getDao().multiRowSelect(sql);
	

		for(int i=0;i<vttotalgoodsnum.size();i++)
		{
			Hashtable ht=(Hashtable) vttotalgoodsnum.get(i);
			String sdate=ht.get("sdate").toString();
				
			ht.put("totalpaygoodsnum", Integer.valueOf(getSdateGoodsNum(vtpaygoodsnum,sdate)));
			
			ht.put("totalpaynum", Integer.valueOf(getSdateBuyTime(vtpaygoodsnum,sdate)));
			
			ht.put("asktotalgoodsnum", Integer.valueOf(getSdateGoodsNum(vtasktotalgoodsnum,sdate)));
			
			ht.put("askbuynum", Integer.valueOf(getSdateBuyTime(vtasktotalgoodsnum,sdate)));
			
			ht.put("askpaygoodsnum", Integer.valueOf(getSdateGoodsNum(vtaskpaygoodsnum,sdate)));
			
			ht.put("askpaynum", Integer.valueOf(getSdateBuyTime(vtaskpaygoodsnum,sdate)));
		}
		this.OutputStr(this.toJSONArray(vttotalgoodsnum));
		
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
	private int getSdateGoodsNum(Vector vt,String sdate)
	{
		int goodsnum=0;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (sdate.equalsIgnoreCase(ht.get("sdate").toString()))
			{
				goodsnum=Integer.valueOf(ht.get("goodsnum").toString()).intValue();
			}
		}
		return goodsnum;
	}
	
	public void getWangWangCustomerGoodsNumCompare() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
				
		String sql="";
		
		//咨询总客件数
		if(tradecontactid.equals("1"))
		{
			sql="select b.csnick,sum(goodsnum) asktotalgoodsnum,count(distinct a.customernick) askbuynum "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,csnick,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+"  and csnick in"
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
			sql="select b.csnick,sum(goodsnum) asktotalgoodsnum,count(distinct a.customernick) askbuynum "
				+"from ConsumeCustomer a with(nolock),(select tradecontactid,csnick,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+"  and csnick in"
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
		
		sql=sql+"order by csnick";
		
		
		Vector vtasktotalgoodsnum=this.getDao().multiRowSelect(sql);
		
		//咨询付款客件数
		sql="select b.csnick,sum(goodsnum) goodsnum,count(distinct a.customernick) paynum "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,csnick,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+"  and csnick in(select usernick from CustomerServiceAccount with(nolock) where roletype=0) and  convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.flag=1 and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" and a.flag<>2  group by b.csnick";
		Vector vtaskpaygoodsnum=this.getDao().multiRowSelect(sql);
		

		for(int i=0;i<vtasktotalgoodsnum.size();i++)
		{

			Hashtable ht=(Hashtable) vtasktotalgoodsnum.get(i);
			String csnick=ht.get("csnick").toString();
							
			ht.put("askpaygoodsnum", Integer.valueOf(getcsnickgoodsnum(vtaskpaygoodsnum,csnick)));
			
			ht.put("askpaynum", Integer.valueOf(getcsnickBuyTime(vtaskpaygoodsnum,csnick)));
		
		}
		this.OutputStr(this.toJSONArray(vtasktotalgoodsnum));
		
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
	private int getcsnickgoodsnum(Vector vt,String csnick)
	{
		int goodsnum=0;
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			if (csnick.equalsIgnoreCase(ht.get("csnick").toString()))
			{
				goodsnum=Integer.valueOf(ht.get("goodsnum").toString()).intValue();
			}
		}
		return goodsnum;
	}

	public void getWangWangCustomerGoodsNum() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String csnick=prop.getProperty("csnick");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
	
		//咨询总客件数
		if (tradecontactid.equals("1"))
		{
			sql="select convert(char(10),chatdate,120) sdate,sum(goodsnum) asktotalgoodsnum,count(distinct a.customernick) askbuynum "
				+"from ConsumeCustomer a with(nolock),(select tradecontactid,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+" and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and csnick='"+csnick+"' and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') b "
				+"where a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" and a.flag<>2  "
				+"group by convert(char(10),chatdate,120) ";
		}else
		{
			sql="select convert(char(10),chatdate,120) sdate,sum(goodsnum) asktotalgoodsnum,count(distinct a.customernick) askbuynum "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+" and convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and csnick='"+csnick+"' and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" and a.flag<>2  "
				+"group by convert(char(10),chatdate,120) ";
		
		}
		
		sql=sql+" order by convert(char(10),chatdate,120)";
		
		Vector vtasktotalgoodsnum=this.getDao().multiRowSelect(sql);
		
		//咨询付款客件数
		sql="select convert(char(10),chatdate,120) sdate,sum(goodsnum) goodsnum,count(distinct a.customernick) paynum "
				+"from ConsumeCustomer a with(nolock),(select distinct tradecontactid,csnick,customernick,chatdate from CustomerServiceChatpeers ch with(nolock) "
				+"where ch.tradecontactid="+tradecontactid+" and  convert(char(10),ch.chatdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and csnick='"+csnick+"' and convert(char(10),ch.chatdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' ) b "
				+"where a.flag=1 and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) and convert(char(10),buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and a.tradecontactid="+tradecontactid+" and a.flag<>2 group by convert(char(10),chatdate,120)";
		Vector vtaskpaygoodsnum=this.getDao().multiRowSelect(sql);
		

		for(int i=0;i<vtasktotalgoodsnum.size();i++)
		{

			Hashtable ht=(Hashtable) vtasktotalgoodsnum.get(i);
			String sdate=ht.get("sdate").toString();
							
			ht.put("askpaygoodsnum", Double.valueOf(getSdateGoodsNum(vtaskpaygoodsnum,sdate)));

			ht.put("askpaynum", Integer.valueOf(getSdateBuyTime(vtaskpaygoodsnum,sdate)));
		
		}
		this.OutputStr(this.toJSONArray(vtasktotalgoodsnum));
		
	}
	
	public void doTransaction(String action) throws Exception {
		
		if (action.equalsIgnoreCase("getwholecustomerunitgoodsnumanalysis"))
			getWholeCustomerUnitGoodsNumAnalysis();

		if (action.equalsIgnoreCase("getwangwangcustomergoodsnumcompare"))
			getWangWangCustomerGoodsNumCompare();
		
		if (action.equalsIgnoreCase("getwangwangcustomergoodsnum"))
			getWangWangCustomerGoodsNum();
		
		
	}
}
