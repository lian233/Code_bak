package com.wofu.fire.deliveryservice;

import com.wofu.base.util.BusinessObject;

/**
 * ¶©µ¥Ã÷Ï¸
 * @author Administrator
 *
 */
public class Detail{
	private String sku;
	private String title;
	private int num;
	private float price;
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	
}
