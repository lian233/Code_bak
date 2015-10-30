package vipapis.product;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class GetProductStockResponse {
	
	/**
	* 商品库存信息列表
	*/
	
	private List<vipapis.product.ProductStock> products;
	
	/**
	* 总记录条数
	*/
	
	private int total;
	
	/**
	* 下一页记录的游标
	*/
	
	private String nextCursorMark;
	
	public List<vipapis.product.ProductStock> getProducts(){
		return this.products;
	}
	
	public void setProducts(List<vipapis.product.ProductStock> value){
		this.products = value;
	}
	public int getTotal(){
		return this.total;
	}
	
	public void setTotal(int value){
		this.total = value;
	}
	public String getNextCursorMark(){
		return this.nextCursorMark;
	}
	
	public void setNextCursorMark(String value){
		this.nextCursorMark = value;
	}
	
}