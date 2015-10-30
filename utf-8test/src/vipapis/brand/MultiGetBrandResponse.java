package vipapis.brand;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class MultiGetBrandResponse {
	
	/**
	* 品牌信息列表
	*/
	
	private List<vipapis.brand.BrandInfo> brands;
	
	/**
	* 总记录条数
	*/
	
	private int total;
	
	public List<vipapis.brand.BrandInfo> getBrands(){
		return this.brands;
	}
	
	public void setBrands(List<vipapis.brand.BrandInfo> value){
		this.brands = value;
	}
	public int getTotal(){
		return this.total;
	}
	
	public void setTotal(int value){
		this.total = value;
	}
	
}