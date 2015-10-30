package com.wofu.retail.baseinformation;

import com.wofu.base.util.BusinessObject;

public class ECS_ItemSku extends BusinessObject {
	private int itemid;
	private int skuid;
	private String customid;
	private int colorid;
	private int sizeid;
	private int cupid;
	private String custombc;
	private double price;
	private int flag;
	private int merchantid;
	
	public ECS_ItemSku()
	{
		this.uniqueFields1="custombc";
		this.uniqueFields1="customid";
	}

	public int getMerchantid() {
		return merchantid;
	}
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}
	public int getItemid() {
		return itemid;	
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}


	public int getSkuid() {
		return skuid;
	}
	public void setSkuid(int skuid) {
		this.skuid = skuid;
	}
	public String getCustomid() {
		return customid;
	}
	public void setCustomid(String customid) {
		this.customid = customid;
	}
	public int getColorid() {
		return colorid;	
	}
	public void setColorid(int colorid) {
		this.colorid = colorid;
	}
	public int getSizeid() {
		return sizeid;	
	}
	public void setSizeid(int sizeid) {
		this.sizeid = sizeid;
	}
	public int getCupid() {
		return cupid;	
	}
	public void setCupid(int cupid) {
		this.cupid = cupid;
	}
	public String getCustombc() {
		return custombc;	
	}
	public void setCustombc(String custombc) {
		this.custombc = custombc;
	}
	public double getPrice() {
		return price;	
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}

}
