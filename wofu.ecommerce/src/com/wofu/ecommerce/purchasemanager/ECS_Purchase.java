package com.wofu.ecommerce.purchasemanager;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.wofu.base.right.ECS_Org;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.conv.Secret;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.retail.customer.ECS_Delivery;
import com.wofu.retail.customer.ECS_DeliveryItem;
import com.wofu.base.interfacemanager.IntfHelper;

public class ECS_Purchase extends PageBusinessObject {
	
	private int pid;
	private String purchasecode;
	private String refcode;
	private int supplierid;
	private int orgid;
	private Date delivedate;
	private int flag;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private String notes;
	private int merchantid;
	
	private DataRelation purchaseitemofpurchases =new DataRelation("purchaseitemofpurchase","com.wofu.ecommerce.purchasemanager.ECS_PurchaseItem");
	
	public ECS_Purchase()
	{			
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="refcode";
		this.exportQuerySQL="";
	}
	
	public void select() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String pid=prop.getProperty("pid");

		
		String sql="select * from ecs_purchase with(nolock) where pid="+pid;
		this.getMapData(this.getDao().oneRowSelect(sql));
		

		sql="select * from ecs_purchaseitem with(nolock) where pid="+pid;
		this.purchaseitemofpurchases.setRelationData(this.getDao().multiRowSelect(sql));
	

		String s=this.toJSONObject();

		this.OutputStr(s);
	}
		
	public void insert() throws Exception
	{
		this.getJSONData();
		this.pid=this.getDao().IDGenerator(this, "pid");
		this.creator=this.getUserInfo().getLogin();
		this.createtime=new Date(System.currentTimeMillis());
		this.updatetime=new Date(System.currentTimeMillis());
		this.delivedate=new Date(System.currentTimeMillis());
		this.updator=this.getUserInfo().getLogin();
		this.flag=0;

		this.merchantid=this.getUserInfo().getMerchantid();

		this.purchasecode=this.getDao().BusiCodeGenerator(this.getUserInfo().getMerchantid(), this, "purchasecode");
		
		this.getDao().insert(this);

		for (int i=0;i<this.purchaseitemofpurchases.getRelationData().size();i++)
		{
			ECS_PurchaseItem purchaseitem=(ECS_PurchaseItem) this.purchaseitemofpurchases.getRelationData().get(i);
			purchaseitem.setPid(this.pid);
			this.getDao().insert(purchaseitem);
		}

		String sql="select * from ecs_purchase where pid="+this.pid;
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	
	public void update() throws Exception
	{

		this.getJSONData();

		this.updatetime=new Date(System.currentTimeMillis());
		this.updator=this.getUserInfo().getLogin();	

		this.getDao().update(this);

		
		String sql="delete from ecs_purchaseitem where pid="+this.pid;
		this.getDao().execute(sql);
		
		for (int i=0;i<this.purchaseitemofpurchases.getRelationData().size();i++)
		{
			ECS_PurchaseItem purchaseitem=(ECS_PurchaseItem) this.purchaseitemofpurchases.getRelationData().get(i);			
			purchaseitem.setPid(this.pid);			
			this.getDao().insert(purchaseitem);
		}
	
		sql="select * from ecs_purchase where pid="+this.pid;
		
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	
	public void deleteBill() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String pid=prop.getProperty("pid");
		
		this.pid=Integer.valueOf(pid).intValue();
		
		this.flag=95;
		
		this.getDao().update(this, "flag");
	}
	
	public void checkBill() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String pid=prop.getProperty("pid");
		
		String sql="select * from ecs_purchase where pid="+pid;
		this.getMapData(this.getDao().oneRowSelect(sql));
		
		this.flag=1;
		this.getDao().update(this);
		
		
		IntfHelper.setInterfaceSheetList(this.getDao(), this.orgid, this.pid, 2301);
		
		sql="select * from ecs_purchase where pid="+this.pid;
		
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getDelivedate() {
		return delivedate;
	}

	public void setDelivedate(Date delivedate) {
		this.delivedate = delivedate;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public DataRelation getPurchaseitemofpurchases() {
		return purchaseitemofpurchases;
	}

	public void setPurchaseitemofpurchases(DataRelation purchaseitemofpurchases) {
		this.purchaseitemofpurchases = purchaseitemofpurchases;
	}

	public int getOrgid() {
		return orgid;
	}

	public void setOrgid(int orgid) {
		this.orgid = orgid;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getPurchasecode() {
		return purchasecode;
	}

	public void setPurchasecode(String purchasecode) {
		this.purchasecode = purchasecode;
	}

	public String getRefcode() {
		return refcode;
	}

	public void setRefcode(String refcode) {
		this.refcode = refcode;
	}

	public int getSupplierid() {
		return supplierid;
	}

	public void setSupplierid(int supplierid) {
		this.supplierid = supplierid;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getUpdator() {
		return updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}
	

	

}

