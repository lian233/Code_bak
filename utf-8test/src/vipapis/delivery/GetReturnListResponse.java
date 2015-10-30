package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class GetReturnListResponse {
	
	/**
	* 退货信息列表
	*/
	
	private List<vipapis.delivery.DvdReturn> dvd_return_list;
	
	/**
	* 记录总条数
	*/
	
	private Integer total;
	
	public List<vipapis.delivery.DvdReturn> getDvd_return_list(){
		return this.dvd_return_list;
	}
	
	public void setDvd_return_list(List<vipapis.delivery.DvdReturn> value){
		this.dvd_return_list = value;
	}
	public Integer getTotal(){
		return this.total;
	}
	
	public void setTotal(Integer value){
		this.total = value;
	}
	
}