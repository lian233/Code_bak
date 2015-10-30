package com.wofu.ecommerce.aftersale;

import java.util.Date;
import java.util.Properties;

import com.wofu.base.interfacemanager.IntfHelper;
import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.ecommerce.ordermanager.ECS_OrderItem;

public class ECS_Refund extends PageBusinessObject {
	private int refundid;
	private int orgid;
	private String ordercode;
	private int customid;
	private String address;
	private String linktele;
	private String linkman;
	private String customernick;
	private int isreturnstore;
	private int inorgid;
	private Date refunddate;
	private int flag;
	private double refundfee;
	private double refundpostfee;
	private String buyeralipayno;
	private int refundtype;
	private int deliveryflag;
	private String delivery;
	private String deliverysheetid;
	private String reason;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private String notes;
	private int merchantid;
	
	private DataRelation refunditemofrefunds =new DataRelation("refunditemofrefund","com.wofu.ecommerce.aftersale.ECS_RefundItem");
	
	public ECS_Refund()
	{			
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="";
		this.exportQuerySQL="";
	}
	
	public void select() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String refundid=prop.getProperty("refundid");

		
		String sql="select * from ecs_refund with(nolock) where refundid="+refundid;
		this.getMapData(this.getDao().oneRowSelect(sql));
		

		sql="select * from ecs_refunditem with(nolock) where refundid="+refundid;
		this.refunditemofrefunds.setRelationData(this.getDao().multiRowSelect(sql));
	

		String s=this.toJSONObject();


		
		this.OutputStr(s);
	}
		
	public void insert() throws Exception
	{
		this.getJSONData();
		this.refundid=this.getDao().IDGenerator(this, "refundid");
		this.creator=this.getUserInfo().getLogin();
		this.createtime=new Date(System.currentTimeMillis());
		this.updatetime=new Date(System.currentTimeMillis());
		this.updator=this.getUserInfo().getLogin();
		this.refunddate=new Date(System.currentTimeMillis());
		this.flag=0;
		
		this.merchantid=this.getUserInfo().getMerchantid();

		this.ordercode=this.getDao().BusiCodeGenerator(this.getUserInfo().getMerchantid(), this, "ordercode");
		
		this.getDao().insert(this);

		for (int i=0;i<this.refunditemofrefunds.getRelationData().size();i++)
		{
			ECS_RefundItem refunditem=(ECS_RefundItem) this.refunditemofrefunds.getRelationData().get(i);
			refunditem.setRefundid(this.refundid);
			this.getDao().insert(refunditem);
		}

		String sql="select * from ecs_refund where orderid="+this.refundid;
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	
	public void update() throws Exception
	{

		this.getJSONData();

		this.updatetime=new Date(System.currentTimeMillis());
		this.updator=this.getUserInfo().getLogin();	

		this.getDao().update(this);

		
		String sql="delete from ecs_refunditem where refundid="+this.refundid;
		this.getDao().execute(sql);
		
		for (int i=0;i<this.refunditemofrefunds.getRelationData().size();i++)
		{
			ECS_RefundItem refunditem=(ECS_RefundItem) this.refunditemofrefunds.getRelationData().get(i);			
			refunditem.setRefundid(this.refundid);
			this.getDao().insert(refunditem);
		}
	
		sql="select * from ecs_refund where orderid="+this.refundid;
		
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	
	public void deleteBill() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String refundid=prop.getProperty("refundid");
		
		this.refundid=Integer.valueOf(refundid).intValue();
		
		this.flag=95;
		
		this.getDao().update(this, "flag");
	}
	
	public void checkBill() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String refundid=prop.getProperty("refundid");
		
		String sql="select * from ecs_refund where refundid="+refundid;
		this.getMapData(this.getDao().oneRowSelect(sql));
		
		if (this.flag==0)
			this.flag=10;
		else
			this.flag=100;
		
		this.getDao().update(this);
		
		
		sql="select * from ecs_refund where refundid="+this.refundid;
		
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	public int getRefundid() {
		return refundid;	
	}
	public void setRefundid(int refundid) {
		this.refundid = refundid;
	}
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
	public int getCustomid() {
		return customid;	
	}
	public void setCustomid(int customid) {
		this.customid = customid;
	}
	public String getAddress() {
		return address;	
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLinktele() {
		return linktele;	
	}
	public void setLinktele(String linktele) {
		this.linktele = linktele;
	}
	public String getLinkman() {
		return linkman;	
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	public String getCustomernick() {
		return customernick;	
	}
	public void setCustomernick(String customernick) {
		this.customernick = customernick;
	}
	public int getIsreturnstore() {
		return isreturnstore;	
	}
	public void setIsreturnstore(int isreturnstore) {
		this.isreturnstore = isreturnstore;
	}
	public int getInorgid() {
		return inorgid;	
	}
	public void setInorgid(int inorgid) {
		this.inorgid = inorgid;
	}
	public Date getRefunddate() {
		return refunddate;	
	}
	public void setRefunddate(Date refunddate) {
		this.refunddate = refunddate;
	}
	public int getFlag() {
		return flag;	
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public double getRefundfee() {
		return refundfee;	
	}
	public void setRefundfee(double refundfee) {
		this.refundfee = refundfee;
	}
	public double getRefundpostfee() {
		return refundpostfee;	
	}
	public void setRefundpostfee(double refundpostfee) {
		this.refundpostfee = refundpostfee;
	}
	public String getBuyeralipayno() {
		return buyeralipayno;	
	}
	public void setBuyeralipayno(String buyeralipayno) {
		this.buyeralipayno = buyeralipayno;
	}
	public int getRefundtype() {
		return refundtype;	
	}
	public void setRefundtype(int refundtype) {
		this.refundtype = refundtype;
	}
	public int getDeliveryflag() {
		return deliveryflag;	
	}
	public void setDeliveryflag(int deliveryflag) {
		this.deliveryflag = deliveryflag;
	}
	public String getDelivery() {
		return delivery;	
	}
	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}
	public String getDeliverysheetid() {
		return deliverysheetid;	
	}
	public void setDeliverysheetid(String deliverysheetid) {
		this.deliverysheetid = deliverysheetid;
	}
	public String getReason() {
		return reason;	
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getCreator() {
		return creator;	
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreatetime() {
		return createtime;	
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getUpdator() {
		return updator;	
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}
	public Date getUpdatetime() {
		return updatetime;	
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getNotes() {
		return notes;	
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}

	public DataRelation getRefunditemofrefunds() {
		return refunditemofrefunds;
	}

	public void setRefunditemofrefunds(DataRelation refunditemofrefunds) {
		this.refunditemofrefunds = refunditemofrefunds;
	}
}
