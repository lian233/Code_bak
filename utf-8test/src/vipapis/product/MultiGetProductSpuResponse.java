package vipapis.product;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class MultiGetProductSpuResponse {
	
	/**
	* 商品列表信息
	*/
	
	private List<vipapis.product.ProductSpuInfo> products;
	
	/**
	* 总记录条数
	*/
	
	private int total;
	
	public List<vipapis.product.ProductSpuInfo> getProducts(){
		return this.products;
	}
	
	public void setProducts(List<vipapis.product.ProductSpuInfo> value){
		this.products = value;
	}
	public int getTotal(){
		return this.total;
	}
	
	public void setTotal(int value){
		this.total = value;
	}
	
}