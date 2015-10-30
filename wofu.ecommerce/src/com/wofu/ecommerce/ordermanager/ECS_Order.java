package com.wofu.ecommerce.ordermanager;
import java.util.Date;
import java.util.Properties;
import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.base.interfacemanager.IntfHelper;
public class ECS_Order extends PageBusinessObject {
	
	private int orderid;
	private String ordercode;
	private String refordercode;
	private int outorgid;
	private int customid;
	private int outplaceid;
	private int inorgid;
	private Date purdate;
	private Date created;
	private Date paydate;
	private Date delivedate;
	private int flag;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private String notes;
	private String refnote;
	private String address;
	private String linktele;
	private String linkman;
	private int stockflag;
	private String delivery;
	private String deliverysheetid;
	private double postfee;
	private String tradenote;
	private String zipcode;
	private String customernick;
	private String promotiondetails;
	private String tradefrom;
	private String message;
	private double payfee;
	private int paymode;
	private int invoiceflag;
	private String invoicetitle;
	private int oversaleflag;
	private int refundflag;
	private int distorid;
	private String disttid;
	private String distshopname;
	private int merchantid;
	
	private DataRelation orderitemoforders =new DataRelation("orderitemoforder","com.wofu.ecommerce.ordermanager.ECS_OrderItem");
	
	public ECS_Order()
	{			
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="refordercode";
		this.exportQuerySQL="";
	}
	
	public void select() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String orderid=prop.getProperty("orderid");

		
		String sql="select * from ecs_order with(nolock) where orderid="+orderid;
		this.getMapData(this.getDao().oneRowSelect(sql));
		

		sql="select * from ecs_orderitem with(nolock) where orderid="+orderid;
		this.orderitemoforders.setRelationData(this.getDao().multiRowSelect(sql));
	

		String s=this.toJSONObject();


		
		this.OutputStr(s);
	}
		
	public void insert() throws Exception
	{
		this.getJSONData();
		this.orderid=this.getDao().IDGenerator(this, "orderid");
		this.creator=this.getUserInfo().getLogin();
		this.createtime=new Date(System.currentTimeMillis());
		this.updatetime=new Date(System.currentTimeMillis());
		this.purdate=new Date(System.currentTimeMillis());
		this.delivedate=new Date(System.currentTimeMillis());
		this.purdate=new Date(System.currentTimeMillis());
		this.paydate=new Date(System.currentTimeMillis());
		this.created=new Date(System.currentTimeMillis());
		this.updator=this.getUserInfo().getLogin();
		this.flag=0;
		this.stockflag=0;
		this.oversaleflag=0;
		this.refundflag=0;
		
		this.merchantid=this.getUserInfo().getMerchantid();

		this.ordercode=this.getDao().BusiCodeGenerator(this.getUserInfo().getMerchantid(), this, "ordercode");
		
		this.getDao().insert(this);

		for (int i=0;i<this.orderitemoforders.getRelationData().size();i++)
		{
			ECS_OrderItem orderitem=(ECS_OrderItem) this.orderitemoforders.getRelationData().get(i);
			orderitem.setOrderid(this.orderid);
			orderitem.setOrderqty(orderitem.getPurqty());	
			this.getDao().insert(orderitem);
		}

		String sql="select * from ecs_order where orderid="+this.orderid;
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	
	public void update() throws Exception
	{

		this.getJSONData();

		this.updatetime=new Date(System.currentTimeMillis());
		this.updator=this.getUserInfo().getLogin();	

		this.getDao().update(this);

		
		String sql="delete from ecs_orderitem where orderid="+this.orderid;
		this.getDao().execute(sql);
		
		for (int i=0;i<this.orderitemoforders.getRelationData().size();i++)
		{
			ECS_OrderItem orderitem=(ECS_OrderItem) this.orderitemoforders.getRelationData().get(i);			
			orderitem.setOrderid(this.orderid);			
			this.getDao().insert(orderitem);
		}
	
		sql="select * from ecs_order where orderid="+this.orderid;
		
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	
	public void deleteBill() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String orderid=prop.getProperty("orderid");
		
		this.orderid=Integer.valueOf(orderid).intValue();
		
		this.flag=95;
		
		this.getDao().update(this, "flag");
	}
	
	public void checkBill() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String orderid=prop.getProperty("orderid");
		
		String sql="select * from ecs_order where orderid="+orderid;
		this.getMapData(this.getDao().oneRowSelect(sql));
		
		this.flag=100;
		this.stockflag=1;
		this.getDao().update(this);
		
		
		IntfHelper.setInterfaceSheetList(this.getDao(), this.outorgid, this.orderid, 2209);
		
		sql="select * from ecs_order where orderid="+this.orderid;
		
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
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

	public int getCustomid() {
		return customid;
	}
	public void setCustomid(int customid) {
		this.customid = customid;
	}
	public Date getDelivedate() {
		return delivedate;
	}
	public void setDelivedate(Date delivedate) {
		this.delivedate = delivedate;
	}
	public String getDelivery() {
		return delivery;
	}
	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}

	public String getCustomernick() {
		return customernick;
	}

	public void setCustomernick(String customernick) {
		this.customernick = customernick;
	}

	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getInshopid() {
		return inorgid;
	}
	public void setInorgid(int inorgid) {
		this.inorgid = inorgid;
	}
	public String getLinkman() {
		return linkman;
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	public String getLinktele() {
		return linktele;
	}
	public void setLinktele(String linktele) {
		this.linktele = linktele;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getOrdercode() {
		return ordercode;
	}
	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}
	public int getOrderid() {
		return orderid;
	}
	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}
	public DataRelation getOrderitemoforders() {
		return orderitemoforders;
	}
	public void setOrderitemoforders(DataRelation orderitemoforders) {
		this.orderitemoforders = orderitemoforders;
	}

	public int getOutplaceid() {
		return outplaceid;
	}
	public void setOutplaceid(int outplaceid) {
		this.outplaceid = outplaceid;
	}
	public int getOutorgid() {
		return this.outorgid;
	}
	public void setOutorgid(int outorgid) {
		this.outorgid = outorgid;
	}
	public Date getPaydate() {
		return paydate;
	}
	public void setPaydate(Date paydate) {
		this.paydate = paydate;
	}
	
	public int getPaymode() {
		return paymode;
	}
	public void setPaymode(int paymode) {
		this.paymode = paymode;
	}

	public double getPostfee() {
		return postfee;
	}

	public void setPostfee(double postfee) {
		this.postfee = postfee;
	}

	public String getPromotiondetails() {
		return promotiondetails;
	}
	public void setPromotiondetails(String promotiondetails) {
		this.promotiondetails = promotiondetails;
	}
	public Date getPurdate() {
		return purdate;
	}
	public void setPurdate(Date purdate) {
		this.purdate = purdate;
	}
	public String getRefnote() {
		return refnote;
	}
	public void setRefnote(String refnote) {
		this.refnote = refnote;
	}
	public String getRefordercode() {
		return refordercode;
	}
	public void setRefordercode(String refordercode) {
		this.refordercode = refordercode;
	}

	public int getStockflag() {
		return stockflag;
	}
	public void setStockflag(int stockflag) {
		this.stockflag = stockflag;
	}

	public String getTradefrom() {
		return tradefrom;
	}
	public void setTradefrom(String tradefrom) {
		this.tradefrom = tradefrom;
	}
	public String getTradenote() {
		return tradenote;
	}
	public void setTradenote(String tradenote) {
		this.tradenote = tradenote;
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
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public int getInorgid() {
		return inorgid;
	}
	public int getDistorid() {
		return distorid;
	}
	public void setDistorid(int distorid) {
		this.distorid = distorid;
	}
	public String getDistshopname() {
		return distshopname;
	}
	public void setDistshopname(String distshopname) {
		this.distshopname = distshopname;
	}
	public String getDisttid() {
		return disttid;
	}
	public void setDisttid(String disttid) {
		this.disttid = disttid;
	}
	public int getInvoiceflag() {
		return invoiceflag;
	}
	public void setInvoiceflag(int invoiceflag) {
		this.invoiceflag = invoiceflag;
	}
	public String getInvoicetitle() {
		return invoicetitle;
	}
	public void setInvoicetitle(String invoicetitle) {
		this.invoicetitle = invoicetitle;
	}
	public int getOversaleflag() {
		return oversaleflag;
	}
	public void setOversaleflag(int oversaleflag) {
		this.oversaleflag = oversaleflag;
	}
	public int getRefundflag() {
		return refundflag;
	}
	public void setRefundflag(int refundflag) {
		this.refundflag = refundflag;
	}
	public int getMerchantid() {
		return merchantid;
	}
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}

	public double getPayfee() {
		return payfee;
	}

	public void setPayfee(double payfee) {
		this.payfee = payfee;
	}

	public String getDeliverysheetid() {
		return deliverysheetid;
	}

	public void setDeliverysheetid(String deliverysheetid) {
		this.deliverysheetid = deliverysheetid;
	}
	

}

