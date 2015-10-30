package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class GetOrderListResponse {
	
	/**
	* 订单信息列表
	*/
	
	private List<vipapis.delivery.DvdOrder> dvd_order_list;
	
	/**
	* 记录总条数
	*/
	
	private Integer total;
	
	public List<vipapis.delivery.DvdOrder> getDvd_order_list(){
		return this.dvd_order_list;
	}
	
	public void setDvd_order_list(List<vipapis.delivery.DvdOrder> value){
		this.dvd_order_list = value;
	}
	public Integer getTotal(){
		return this.total;
	}
	
	public void setTotal(Integer value){
		this.total = value;
	}
	
}