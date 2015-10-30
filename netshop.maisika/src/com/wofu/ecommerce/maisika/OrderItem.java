package com.wofu.ecommerce.maisika;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * 订单中的商品
 *
 */
public class OrderItem extends BusinessObject{
	private String mid="";//商品标识--自增
	private String sku="";        //商家sku  通过v_barcodeall表查出来
	private float price=0.0f;//市场价 
	private String  title; //标题
	private int num=0;//商品数量
	

	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
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
	
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}



	public int getNum() {
		// TODO Auto-generated method stub
		return num;
	}
	
	public void setNum(int num) {
		// TODO Auto-generated method stub
		this.num = num;
	}

	
	
	
	
	
}
