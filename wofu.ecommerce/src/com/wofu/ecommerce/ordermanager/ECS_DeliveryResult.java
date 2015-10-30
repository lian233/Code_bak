package com.wofu.ecommerce.ordermanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class ECS_DeliveryResult extends PageBusinessObject {
	
	private int orgid;
	private String ordercode;
	private String companycode;
	private String outsid;
	private String trancompanycode;
	private String tranoutsid;
	private int status;
	private int isupdate;
	private int resultflag;
	private String msg;
	private Date createtime;
	private Date updatetime;

	private DataRelation deliveryresultofdeliveryresults =new DataRelation("deliveryresultofdeliveryresult","com.wofu.ecommerce.ordermanager.ECS_DeliveryResult");

	public int getOrgid() {
		return orgid;	
	}
	public void setOrgid(int orgid) {
		this.orgid = orgid;
	}
	public String getOrdercode() {
		return ordercode;	
	}
	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}
	public int getStatus() {
		return status;	
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getIsupdate() {
		return isupdate;	
	}
	public void setIsupdate(int isupdate) {
		this.isupdate = isupdate;
	}
	public int getResultflag() {
		return resultflag;	
	}
	public void setResultflag(int resultflag) {
		this.resultflag = resultflag;
	}
	public String getMsg() {
		return msg;	
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Date getCreatetime() {
		return createtime;	
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getUpdatetime() {
		return updatetime;	
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}


	public void update() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.deliveryresultofdeliveryresults.getRelationData().size();i++)
		{
			ECS_DeliveryResult deliveryresult=(ECS_DeliveryResult) this.deliveryresultofdeliveryresults.getRelationData().get(i);
			String sql="update ecs_deliveryresult set status="+deliveryresult.getStatus()+
						" where orgid="+deliveryresult.getOrgid()+" and ordercode='"+deliveryresult.getOrdercode()+"'";
			this.getDao().execute(sql);
		}
	}
	
	public void updateDeliveryResult() throws Exception
	{
		String reqdata = this.getReqData();		
		
		
		Properties prop=StringUtil.getIniProperties(reqdata);
		int orgid=Integer.valueOf(prop.getProperty("orgid")).intValue();
		int status=Integer.valueOf(prop.getProperty("status")).intValue();
		String orders=prop.getProperty("orders");
		

		
		String[] orderarr=orders.split(",");
		
		ArrayList orderlist=new ArrayList();
		
		/*	1.检查订单是否已更新过
		 * 	2.检查订单状态是否与实际相符		 
		 */		
		for (int i=0;i<orderarr.length;i++)
		{
			String ordercode=orderarr[i];
			String sql="select count(*) from ecs_deliveryresult "
				+"where orgid="+orgid+" and ordercode='"+ordercode+"' and isupdate=1";
			if (this.getDao().intSelect(sql)>0)	
				throw new JException("不允许订单重复更新配送状态,订单号:"+ordercode);
			
			if (status==0)
			{
				sql="select count(*) from ecs_deliveryresult "
					+"where orgid="+orgid+" and ordercode='"+ordercode+"' and status=1";
			}
			else
			{
				sql="select count(*) from ecs_deliveryresult "
					+"where orgid="+orgid+" and ordercode='"+ordercode+"' and status=0";
			}
			
			if (this.getDao().intSelect(sql)==0)	
				throw new JException("订单状态与该操作不符,订单号:"+ordercode);
			
			Hashtable ht=new Hashtable();
			ht.put("ordercode", ordercode);
			ht.put("status", String.valueOf(status));			
			orderlist.add(ht);
			
		}
		

		
		List updateresultlist=null;
		if (orgid==25)  //V+
		{
			
			updateresultlist=com.wofu.ecommerce.vjia.Distributor.updateVjiaDeliveryResult(this.getDao().getConnection(), orderlist);
			
		}
		else if (orgid==28)  //dangdang
		{
			updateresultlist=com.wofu.ecommerce.dangdang.Distributor.updateDangdangDeliveryResult(this.getDao().getConnection(), orderlist);
		}
			
		
		for (int j=0;j<updateresultlist.size();j++)
		{
			Hashtable htresult=(Hashtable) updateresultlist.get(j);
			
			String sql="update ecs_deliveryresult set isupdate=1,"
				+"resultflag="+htresult.get("resultflag").toString()
				+",msg='"+htresult.get("msg").toString()+"',updatetime='"+Formatter.format(new Date(),Formatter.DATE_TIME_MS_FORMAT)+"' "
				+" where orgid="+orgid+" and ordercode='"+htresult.get("ordercode").toString()+"'";
			this.getDao().execute(sql);
			
			htresult.put("isupdate", "1");
			htresult.put("orgid", String.valueOf(orgid));
			htresult.put("updatetime", Formatter.format(new Date(),Formatter.DATE_TIME_MS_FORMAT));
			
			updateresultlist.set(j, htresult);
		}
		
		this.OutputStr(this.toJSONArray(updateresultlist));
		
	}
	
	public void search() throws Exception
	{
		String reqdata = this.getReqData();		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String sqlwhere=prop.getProperty("sqlwhere");
		String currpage=prop.getProperty("currpage");
		String pagesize=prop.getProperty("pagesize");

		this.setPagesize(Integer.valueOf(pagesize).intValue());
		this.setCurrpage(Integer.valueOf(currpage).intValue());
		
		String sql="select * from ecs_deliveryresult with(nolock) where 1=1 "+sqlwhere;
		Vector vtd=this.getPaginationData(sql, "orgid,ordercode","asc");
		for (int i=0;i<vtd.size();i++)
		{
			Hashtable htd=(Hashtable) vtd.get(i);
		
			String logisticsinfo=getDeliveryNote(htd.get("orgid").toString(),htd.get("ordercode").toString());

			htd.put("logisticsinfo", logisticsinfo);
			
			vtd.set(i,htd);
	
		}
	
		this.OutputStr(this.toJSONArray(vtd));
	}
	
	private String getDeliveryNote(String orgid,String ordercode) throws Exception
	{		
		StringBuffer logisticsinfo=new StringBuffer();
		logisticsinfo.append("<div style='border:#eaeaea 1px solid; background-color:#fbfbfc;line-height:150%;'");
		logisticsinfo.append("<ul>");	
		
		String sql="select convert(char(19),proctime,120) proctime,note from ecs_deliverynote "
			+"where orgid="+orgid +" and ordercode='"+ordercode+"' "
			+"order by serialid";
		Vector vtn=this.getDao().multiRowSelect(sql);
		for (int i=0;i<vtn.size();i++)
		{

			Hashtable htn=(Hashtable) vtn.get(i);
			logisticsinfo.append("<li>");
			logisticsinfo.append("<span style='font:8px/1.5 tahoma, arial, 宋体;'>");
			logisticsinfo.append(htn.get("proctime").toString());
			logisticsinfo.append("&nbsp;");
			logisticsinfo.append(htn.get("note").toString());
			logisticsinfo.append("</span>");
			logisticsinfo.append("</li>");
		}
		
		logisticsinfo.append("</ul>");
		logisticsinfo.append("</div>");
		
		return logisticsinfo.toString();
		
	}
	
	public void getDeliveryNote() throws Exception
	{
		String reqdata = this.getReqData();		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String orgid=prop.getProperty("orgid");
		String ordercode=prop.getProperty("ordercode");
		String sql="select convert(char(19),proctime,120) proctime,note from ecs_deliverynote "
			+"where orgid="+orgid +" and ordercode='"+ordercode+"' "
			+"order by serialid";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	public DataRelation getDeliveryresultofdeliveryresults() {
		return deliveryresultofdeliveryresults;
	}
	public void setDeliveryresultofdeliveryresults(
			DataRelation deliveryresultofdeliveryresults) {
		this.deliveryresultofdeliveryresults = deliveryresultofdeliveryresults;
	}
	public String getCompanycode() {
		return companycode;
	}
	public void setCompanycode(String companycode) {
		this.companycode = companycode;
	}
	public String getOutsid() {
		return outsid;
	}
	public void setOutsid(String outsid) {
		this.outsid = outsid;
	}
	public String getTrancompanycode() {
		return trancompanycode;
	}
	public void setTrancompanycode(String trancompanycode) {
		this.trancompanycode = trancompanycode;
	}
	public String getTranoutsid() {
		return tranoutsid;
	}
	public void setTranoutsid(String tranoutsid) {
		this.tranoutsid = tranoutsid;
	}
	
}
