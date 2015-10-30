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

public class CustomerServiceRecord extends BusinessObject{

	private static final DecimalFormat df=new DecimalFormat("0.00");

	public void getServiceRecord() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String csnick=prop.getProperty("csnick");
		String startdate=prop.getProperty("startdate");		
		String enddate=prop.getProperty("enddate");
		
		String sql="";
		if (csnick.equalsIgnoreCase("all"))
			sql="select tradecontactid,csnick,avgwaitingtime,nonreplynum,convert(char(10),sdate,120) sdate "
				+" from CustomerServiceRecord with(nolock) where convert(char(10),sdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),sdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and tradecontactid="+tradecontactid;
		else
			sql="select tradecontactid,csnick,avgwaitingtime,nonreplynum,convert(char(10),sdate,120) sdate "
				+" from CustomerServiceRecord with(nolock) where convert(char(10),sdate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),sdate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and tradecontactid="+tradecontactid+" and csnick='"+csnick+"'";
		Vector vt=this.getDao().multiRowSelect(sql);
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			String sdate=ht.get("sdate").toString();
			csnick=ht.get("csnick").toString();
	
			sql="select count(distinct customernick) buyernum,sum(paymoney) paymoney,count(tid) sheetcount,sum(goodsnum) goodsnum  from ConsumeCustomer with(nolock) where  tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)='"
				+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+"and customernick in(select distinct customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
				+" and csnick='"+csnick+"' and valid=1 and convert(char(10),chatdate,120)='"
				+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"')";
			Hashtable htbuy=this.getDao().oneRowSelect(sql);
			int buyernum=Integer.valueOf(htbuy.get("buyernum").toString()).intValue();
			int buyergoodsnum=Integer.valueOf(htbuy.get("goodsnum").toString()).intValue();
			int buyyersheetcount=Integer.valueOf(htbuy.get("sheetcount").toString()).intValue();
			String buymoney=htbuy.get("paymoney").toString();
			ht.put("buyernum", Integer.valueOf(buyernum));
			ht.put("buymoney", Double.valueOf(buymoney));
			
			if (buyernum==0)
				ht.put("buyergoodsnum", "0.00");
			else
				ht.put("buyergoodsnum", df.format(((float)buyergoodsnum/(float)buyernum)));
			
			if (buyernum==0)
				ht.put("buyunitprice", "0.00");
			else
				ht.put("buyunitprice", df.format((Float.valueOf(buymoney).floatValue()/(float)buyernum)));
			
			sql="select count(*) from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"'  and convert(char(10),chatdate,120)='"
			+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'";
			int replynum=this.getDao().intSelect(sql);
			ht.put("replynum", Integer.valueOf(replynum));
		
			sql="select count(*) from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
				+" and csnick='"+csnick+"' and valid=1 and convert(char(10),chatdate,120)='"
				+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'";
			int validreplynum=this.getDao().intSelect(sql);
			ht.put("validreplynum", Integer.valueOf(validreplynum));
			if (validreplynum==0)
				ht.put("transferrate","0.00");
			else
				ht.put("transferrate", df.format(((float)buyernum/(float)validreplynum)));	

			sql="select count(distinct customernick) paynum,sum(paymoney) paymoney,count(tid) sheetcount,sum(goodsnum) goodsnum from ConsumeCustomer with(nolock) where tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)='"
			+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and flag=1 and customernick in(select distinct customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and valid=1 and convert(char(10),chatdate,120)='"
			+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') ";
			Hashtable htpay=this.getDao().oneRowSelect(sql);
			int paynum=Integer.valueOf(htpay.get("paynum").toString()).intValue();
			int paygoodsnum=Integer.valueOf(htpay.get("goodsnum").toString()).intValue();
			int paysheetcount=Integer.valueOf(htbuy.get("sheetcount").toString()).intValue();
			String paymoney=htpay.get("paymoney").toString();
			ht.put("paynum", Integer.valueOf(paynum));
			ht.put("paymoney", Double.valueOf(paymoney));
			
			if (buyernum==0)
				ht.put("payrate","0.00");
			else
				ht.put("payrate", df.format(((float)paynum/(float)buyernum)));
			
			if (paynum==0)
				ht.put("paygoodsnum", "0.00");
			else
				ht.put("paygoodsnum", df.format(((float)paygoodsnum/(float)paynum)));
			
			if (paynum==0)
				ht.put("payunitprice", "0.00");
			else
				ht.put("payunitprice", df.format((Float.valueOf(paymoney).floatValue()/(float)paynum)));
			

			sql="select count(distinct customernick) cancelnum,isnull(sum(paymoney),0.00) cancelpaymoney from ConsumeCustomer with(nolock) where tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)='"
			+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and flag=2 and datediff(second,buydate,canceldate)<3*24*60*60 and customernick in(select distinct customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and valid=1 and convert(char(10),chatdate,120)='"
			+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') "
			+"and customernick not in(select distinct customernick cancelnum from ConsumeCustomer where tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)='"
			+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and flag in(0,1) and customernick in(select distinct customernick from CustomerServiceChatpeers  with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and valid=1 and convert(char(10),chatdate,120)='"
			+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'))";
			Hashtable htcancelpay=this.getDao().oneRowSelect(sql);
			int cancelnum=Integer.valueOf(htcancelpay.get("cancelnum").toString()).intValue();
			String cancelmoney=htcancelpay.get("cancelpaymoney").toString();
			ht.put("cancelnum", Integer.valueOf(cancelnum));
			ht.put("cancelmoney", Double.valueOf(cancelmoney));
			

			sql="select count(distinct customernick) unpaynum,isnull(sum(paymoney),0.00) unpaymoney from ConsumeCustomer with(nolock) where tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)='"
			+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and (flag=0 or (flag=2 and datediff(second,buydate,canceldate)>=3*24*60*60)) and customernick in(select distinct customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and valid=1 and convert(char(10),chatdate,120)='"
			+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"') "
			+"and customernick not in(select distinct customernick cancelnum from ConsumeCustomer where tradecontactid="+tradecontactid+" and convert(char(10),buydate,120)='"
			+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
			+"and flag=1 and customernick in(select distinct customernick from CustomerServiceChatpeers with(nolock) where tradecontactid="+tradecontactid
			+" and csnick='"+csnick+"' and valid=1 and convert(char(10),chatdate,120)='"
			+Formatter.format(Formatter.parseDate(sdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'))";
			Hashtable htunpay=this.getDao().oneRowSelect(sql);
			int unpaynum=Integer.valueOf(htunpay.get("unpaynum").toString()).intValue();
			String unpaymoney=htunpay.get("unpaymoney").toString();
			ht.put("unpaynum", Integer.valueOf(unpaynum));
			ht.put("unpaymoney", Double.valueOf(unpaymoney));
			
			vt.setElementAt(ht,i);
		}
		for(int j=0;j<vt.size();j++)
		{
			Hashtable htcs=(Hashtable) vt.get(j);
			int csreplynum=Integer.valueOf(htcs.get("replynum").toString()).intValue();
			
			if ((csreplynum<20)&&(htcs.get("tradecontactid").toString().equals("1")))
			{
				vt.remove(j);
				j--;
			}
		}
		this.OutputStr(this.toJSONArray(vt));
		
	}
	
	public void getNoReplyChatlog() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);

		String tradecontactid=prop.getProperty("tradecontactid");
		String csnick=prop.getProperty("csnick");		
		String chatdate=prop.getProperty("chatdate");
		String sql="select noreplynick from CustomerServiceNoReplyNick with(nolock) "
				+" where tradecontactid="+tradecontactid+" and csnick='"+csnick+"' "
				+" and convert(char(10),sdate,120)='"+Formatter.format(Formatter.parseDate(chatdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'";
		List lst=this.getDao().oneListSelect(sql);
		StringBuffer strbuf=new StringBuffer();
		for (int i=0;i<lst.size();i++)
		{
			String customernick=prop.getProperty("customernick");
			sql="select chatrecord from CustomerServiceChatpeers with(nolock)"
				+" where tradecontactid="+tradecontactid+" and csnick='"+csnick+"' "
				+" and chatdate='"+chatdate+"' and customernick='"+customernick+"'";
			Hashtable ht=this.getDao().oneRowSelect(sql);
	
			InputStream in=(InputStream) ht.get("chatrecord");
			strbuf.append(StreamUtil.InputStreamToStr(in, "GBK"));	
			strbuf.append(StringUtil.NEW_LINE);
		}
		this.OutputStr(strbuf.toString());
	}
	
	public void doTransaction(String action) throws Exception {
		
		if (action.equalsIgnoreCase("getservicerecord"))
			getServiceRecord();
		if (action.equalsIgnoreCase("getnoreplychatlog"))
			getNoReplyChatlog();	
	}
}
