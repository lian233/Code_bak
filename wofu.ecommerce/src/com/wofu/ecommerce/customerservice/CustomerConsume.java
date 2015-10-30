package com.wofu.ecommerce.customerservice;



import java.util.Properties;
import com.wofu.base.util.BusinessObject;
import com.wofu.common.tools.conv.Convert;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;


public class CustomerConsume extends BusinessObject {
	

	
	public void doSearch() throws Exception
	{
		String reqdata = new String(Convert.streamToBytes(this.getRequest().getInputStream()), "utf-8");
		Properties prop=StringUtil.getIniProperties(reqdata);
		String tradecontactid=prop.getProperty("tradecontactid");
		String startdate=prop.getProperty("startdate");
		String enddate=prop.getProperty("enddate");
		String csnick=prop.getProperty("csnick");
		String customernick=prop.getProperty("customernick");
		String ordercode=prop.getProperty("ordercode");
		String payflag=prop.getProperty("payflag");
		

		String sqlwhere="";
		if (!customernick.trim().equalsIgnoreCase(""))
			sqlwhere=sqlwhere+" and a.customernick='"+customernick+"' ";
		if (!ordercode.trim().equalsIgnoreCase(""))
			sqlwhere=sqlwhere+" and a.tid='"+ordercode+"' ";
		if (!payflag.trim().equalsIgnoreCase(""))
		{
			if (payflag.trim().equalsIgnoreCase("1"))
				sqlwhere=sqlwhere+" and flag="+payflag+" ";
			else
				sqlwhere=sqlwhere+" and flag in(0,2) ";
		}
		
		String sql="select a.tradecontactid,a.customernick,b.csnick,convert(char(19),a.buydate,120) buydate,"
				+"(case when a.paytime is null then '' else convert(char(19),a.paytime,120) end) paytime,"
				+"a.paymoney,a.goodsnum,tid,(case when a.flag=2 then 1 else 0 end) cancelflag, "				
				+"(case when returnmoney is null then 0 else 1 end) returnflag,"
				+"(case when flag=1 then 1 else 0 end) payflag "
				+"from ConsumeCustomer a with(nolock),CustomerServiceChatpeers b with(nolock) "
				+"where b.valid=1 and a.tradecontactid=b.tradecontactid and a.customernick=b.customernick "
				+"and convert(char(10),a.buydate,120)=convert(char(10),b.chatdate,120) "
				+"and a.tradecontactid="+tradecontactid+" and b.csnick='"+csnick+"' and "
				+"convert(char(10),a.buydate,120)>='"
				+Formatter.format(Formatter.parseDate(startdate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"'"
				+" and convert(char(10),a.buydate,120)<='"
				+Formatter.format(Formatter.parseDate(enddate, Formatter.DATE_FORMAT), Formatter.DATE_FORMAT)+"' "
				+sqlwhere;
		
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));		
	}
	
	
	public void doTransaction(String action) throws Exception {
		
		if (action.equalsIgnoreCase("search"))
			doSearch();	

		
	}

}
