package vipapis.product;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class ProductStock {
	
	/**
	* 商品ID
	*/
	
	private int product_id;
	
	/**
	* 商品名称
	*/
	
	private String product_name;
	
	/**
	* 商品销售价格
	*/
	
	private double sell_price;
	
	/**
	* 库存状况，true:有库存；false:没有库存
	*/
	
	private boolean hasStock;
	
	public int getProduct_id(){
		return this.product_id;
	}
	
	public void setProduct_id(int value){
		this.product_id = value;
	}
	public String getProduct_name(){
		return this.product_name;
	}
	
	public void setProduct_name(String value){
		this.product_name = value;
	}
	public double getSell_price(){
		return this.sell_price;
	}
	
	public void setSell_price(double value){
		this.sell_price = value;
	}
	public boolean getHasStock(){
		return this.hasStock;
	}
	
	public void setHasStock(boolean value){
		this.hasStock = value;
	}
	
}