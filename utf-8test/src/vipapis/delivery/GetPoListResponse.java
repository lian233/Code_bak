package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class GetPoListResponse {
	
	/**
	* 商品清单列表
	*/
	
	private List<vipapis.delivery.PurchaseOrder> purchase_order_list;
	
	/**
	* 记录总条数
	*/
	
	private Integer total;
	
	public List<vipapis.delivery.PurchaseOrder> getPurchase_order_list(){
		return this.purchase_order_list;
	}
	
	public void setPurchase_order_list(List<vipapis.delivery.PurchaseOrder> value){
		this.purchase_order_list = value;
	}
	public Integer getTotal(){
		return this.total;
	}
	
	public void setTotal(Integer value){
		this.total = value;
	}
	
}