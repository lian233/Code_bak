package com.wofu.retail.customer;


import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class ECS_Delivery extends BusinessObject {
	
	private int deliveryid;
	private String deliverycode;
	private int orderid;
	private String ordercode;
	private String refordercode;
	private int outplaceid;
	private int inplaceid;
	private int outorgid;
	private int customid;
	private int inorgid;
	private Date delivedate;
	private int flag;
	private int stockflag;
	private String creator;
	private Date createtime;
	private String operator;
	private String updator;
	private Date updatetime;
	private Date busidate;
	private String address;
	private String linktele;
	private String linkman;
	private String delivery;
	private String deliverysheetid;
	private String zipcode;
	private String detailid;
	private String message;
	private double payfee;
	private int paymode;
	private double postfee;
	private int invoiceflag;
	private String invoicetitle;
	private int distorid;
	private String disttid;
	private String distshopname;
	private String notes;
	
	public DataRelation deliveryitemofdeliverys =new DataRelation("deliveryitemofdelivery","com.wofu.retail.customer.ECS_DeliveryItem");

	public int getDeliveryid() {
		return deliveryid;	
	}
	public void setDeliveryid(int deliveryid) {
		this.deliveryid = deliveryid;
	}
	public String getDeliverycode() {
		return deliverycode;	
	}
	public void setDeliverycode(String deliverycode) {
		this.deliverycode = deliverycode;
	}
	public int getOrderid() {
		return orderid;	
	}
	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}
	public String getOrdercode() {
		return ordercode;	
	}
	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}
	public String getRefordercode() {
		return refordercode;	
	}
	public void setRefordercode(String refordercode) {
		this.refordercode = refordercode;
	}
	public int getOutplaceid() {
		return outplaceid;	
	}
	public void setOutplaceid(int outplaceid) {
		this.outplaceid = outplaceid;
	}
	public int getInplaceid() {
		return inplaceid;	
	}
	public void setInplaceid(int inplaceid) {
		this.inplaceid = inplaceid;
	}

	public int getCustomid() {
		return customid;	
	}
	public void setCustomid(int customid) {
		this.customid = customid;
	}

	public int getInorgid() {
		return inorgid;
	}
	public void setInorgid(int inorgid) {
		this.inorgid = inorgid;
	}
	public int getOutorgid() {
		return outorgid;
	}
	public void setOutorgid(int outorgid) {
		this.outorgid = outorgid;
	}

	public int getFlag() {
		return flag;	
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getStockflag() {
		return stockflag;	
	}
	public void setStockflag(int stockflag) {
		this.stockflag = stockflag;
	}
	public String getCreator() {
		return creator;	
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getOperator() {
		return operator;	
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getUpdator() {
		return updator;	
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}

	public Date getBusidate() {
		return busidate;
	}
	public void setBusidate(Date busidate) {
		this.busidate = busidate;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getDelivedate() {
		return delivedate;
	}
	public void setDelivedate(Date delivedate) {
		this.delivedate = delivedate;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
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
	public String getZipcode() {
		return zipcode;	
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getDetailid() {
		return detailid;	
	}
	public void setDetailid(String detailid) {
		this.detailid = detailid;
	}
	public String getMessage() {
		return message;	
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public int getPaymode() {
		return paymode;	
	}
	public void setPaymode(int paymode) {
		this.paymode = paymode;
	}
	public String getNotes() {
		return notes;	
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public DataRelation getDeliveryitemofdeliverys() {
		return deliveryitemofdeliverys;
	}
	public void setDeliveryitemofdeliverys(DataRelation deliveryitemofdeliverys) {
		this.deliveryitemofdeliverys = deliveryitemofdeliverys;
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
	public double getPayfee() {
		return payfee;
	}
	public void setPayfee(double payfee) {
		this.payfee = payfee;
	}
	public double getPostfee() {
		return postfee;
	}
	public void setPostfee(double postfee) {
		this.postfee = postfee;
	}



}
