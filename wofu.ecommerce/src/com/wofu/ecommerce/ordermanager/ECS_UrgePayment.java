package com.wofu.ecommerce.ordermanager;

import java.util.Properties;

import com.wofu.base.util.BusinessObject;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;

import com.wofu.common.tools.util.RemoteHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class ECS_UrgePayment extends BusinessObject {
	
	public void sendSms() throws Exception
	{
		String reqdata = this.getReqData();		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String startdate=prop.getProperty("startdate");
		String enddate=prop.getProperty("enddate");
		String content=prop.getProperty("content");
		
		String requesturl="http://whzf007904.chinaw3.com/TinyWebServer?ptype=senddata&pname=getRDSOrderInfo&requestname=ecsurgepayment&uid=";
		
		String data="startdate="+startdate+"\nenddate="+enddate;
		String orderinfos=RemoteHelper.sendRequest(requesturl,data,"GBK");
		
		
		
		JSONArray jsonarr=new JSONArray(orderinfos);
		
		for (int i=0;i<jsonarr.length();i++)
		{
			JSONObject jsobj=jsonarr.getJSONObject(i);
			String phone=jsobj.getString("receiver_mobile").trim();
			//String tid=jsobj.getString("tid").trim();
			//String created=jsobj.getString("created").trim();
			//String buyernick=jsobj.getString("buyer_nick").trim();
			
			
			if (phone==null || phone.equalsIgnoreCase("") 
					|| phone.trim().length()!=11) continue;

			String sql="insert into SmsNotify0(msgtype,mobile,msg,content,state,noticetime) "
				+" values(101,'"+phone.trim()+"','"+content+"','"+content+"',0,getdate())";
			
			this.getDao().execute(sql);
				
			Log.info("´ß¿î, ÊÖ»úºÅÂë:"+phone);	
		}
		//this.OutputStr("{\"totalnum\":\""+jsonarr.length()+"\"}");
	}
	
	public void getRDSOrderInfo() throws Exception
	{
		String reqdata = this.getReqData();		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String startdate=prop.getProperty("startdate");
		String enddate=prop.getProperty("enddate");
	
		int sellerid=422229230;
		
		String sql="select isnull(receiver_mobile,'') receiver_mobile from sys_info.dbo.orders "
			+"where is_main=1 and status in('WAIT_BUYER_PAY','TRADE_NO_CREATE_PAY') and convert(char(19),created,120)>='"+startdate+"' "
			+"and convert(char(19),created,120)<='"+enddate+"' and seller_id="+sellerid
			+" and buyer_nick not in(select buyer_nick from sys_info.dbo.orders "
			+" where is_main=1 and status in('WAIT_BUYER_CONFIRM_GOODS','WAIT_SELLER_SEND_GOODS') "
			+" and seller_id="+sellerid+") order by created";
		
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}

}
