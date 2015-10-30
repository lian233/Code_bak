package com.wofu.ecommerce.ordermanager;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class ECS_OrderPreModify extends BusinessObject {
	
	private String ordercode;
	private String outshopid;
	private String linkman;
	private String linktele;
	private String address;
	private String delivery;
	private String refnote;
	private int invoiceflag;
	private String invoicetitle;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public String getDelivery() {
		return delivery;
	}
	public void setDelivery(String delivery) {
		this.delivery = delivery;
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
	public String getOrdercode() {
		return ordercode;
	}
	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}
	public String getOutshopid() {
		return outshopid;
	}
	public void setOutshopid(String outshopid) {
		this.outshopid = outshopid;
	}
	public String getRefnote() {
		return refnote;
	}
	public void setRefnote(String refnote) {
		this.refnote = refnote;
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
