package com.wofu.ecommerce.weidian2;

import com.wofu.base.util.BusinessObject;

public class OrderItem extends BusinessObject
{
	private String sku;
	private String price;
	private String mid;
	private String title;
	private int num;
	private String prop;
	
	public void   setSku(String sku){this.sku=sku;}
	public String getSku(){return sku;}
	
	public void   setPrice(String price){this.price=price;}
	public String getPrice(){return price;}
	
	public void   setMid(String mid){this.mid=mid;}
	public String getMid(){return mid;}

	public void   setTitle(String title){this.title=title;}
	public String getTitle(){return title;}
	
	public void   setNum(int num){this.num=num;}
	public int getNum(){return num;}
	
	public void   setProp(String prop){this.prop=prop;}
	public String getProp(){return prop;}
}
