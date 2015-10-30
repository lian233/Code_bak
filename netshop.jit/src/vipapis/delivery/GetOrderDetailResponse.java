package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class GetOrderDetailResponse {
	
	/**
	* 订单明细信息列表
	*/
	
	private List<vipapis.delivery.DvdOrderDetail> orderDetails;
	
	/**
	* 记录总条数
	*/
	
	private Integer total;
	
	public List<vipapis.delivery.DvdOrderDetail> getOrderDetails(){
		return this.orderDetails;
	}
	
	public void setOrderDetails(List<vipapis.delivery.DvdOrderDetail> value){
		this.orderDetails = value;
	}
	public Integer getTotal(){
		return this.total;
	}
	
	public void setTotal(Integer value){
		this.total = value;
	}
	
}