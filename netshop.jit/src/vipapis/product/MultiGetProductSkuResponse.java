package vipapis.product;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class MultiGetProductSkuResponse {
	
	/**
	* 商品详细信息
	*/
	
	private List<vipapis.product.ProductSkuInfo> products;
	
	/**
	* 总记录条数
	*/
	
	private int total;
	
	public List<vipapis.product.ProductSkuInfo> getProducts(){
		return this.products;
	}
	
	public void setProducts(List<vipapis.product.ProductSkuInfo> value){
		this.products = value;
	}
	public int getTotal(){
		return this.total;
	}
	
	public void setTotal(int value){
		this.total = value;
	}
	
}