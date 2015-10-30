package com.wofu.ecommerce.taobao.fenxiao;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class Sku extends BusinessObject {
	
	private long sku_id;
	private long num_iid;
	private long quantity;
	private long with_hold_quantity;
	private String properties;
	private String properties_name;
	private Date created;
	private Date modified;
	private String status;
	private String price;
	public long getWith_hold_quantity() {
		return with_hold_quantity;
	}
	public void setWith_hold_quantity(long with_hold_quantity) {
		this.with_hold_quantity = with_hold_quantity;
	}
	public String getProperties_name() {
		return properties_name;
	}
	public void setProperties_name(String properties_name) {
		this.properties_name = properties_name;
	}
	private String outer_id;
	public long getSku_id() {
		return sku_id;
	}
	public void setSku_id(long sku_id) {
		this.sku_id = sku_id;
	}
	public long getNum_iid() {
		return num_iid;
	}
	public void setNum_iid(long num_iid) {
		this.num_iid = num_iid;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getOuter_id() {
		return outer_id;
	}
	public void setOuter_id(String outer_id) {
		this.outer_id = outer_id;
	}
	
	

}
