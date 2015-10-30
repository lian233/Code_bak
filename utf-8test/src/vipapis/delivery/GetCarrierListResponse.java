package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class GetCarrierListResponse {
	
	/**
	* 承运商信息
	*/
	
	private List<vipapis.delivery.Carrier> carriers;
	
	/**
	* 记录总条数
	*/
	
	private Integer total;
	
	public List<vipapis.delivery.Carrier> getCarriers(){
		return this.carriers;
	}
	
	public void setCarriers(List<vipapis.delivery.Carrier> value){
		this.carriers = value;
	}
	public Integer getTotal(){
		return this.total;
	}
	
	public void setTotal(Integer value){
		this.total = value;
	}
	
}