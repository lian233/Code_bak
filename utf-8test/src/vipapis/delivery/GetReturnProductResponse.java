package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class GetReturnProductResponse {
	
	/**
	* 退货商品信息列表
	*/
	
	private List<vipapis.delivery.DvdReturnProduct> dvd_return_product_list;
	
	/**
	* 记录总条数
	*/
	
	private Integer total;
	
	public List<vipapis.delivery.DvdReturnProduct> getDvd_return_product_list(){
		return this.dvd_return_product_list;
	}
	
	public void setDvd_return_product_list(List<vipapis.delivery.DvdReturnProduct> value){
		this.dvd_return_product_list = value;
	}
	public Integer getTotal(){
		return this.total;
	}
	
	public void setTotal(Integer value){
		this.total = value;
	}
	
}