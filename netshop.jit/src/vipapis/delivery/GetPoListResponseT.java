package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;



public  class GetPoListResponseT extends BusinessObject{
	
	/**
	* 
	*/
	private DataRelation purchase_order_list = new DataRelation("purchase_order_list","vipapis.delivery.PurchaseOrder");
	
	/**
	* 
	*/
	
	private Integer total;
	

	public DataRelation getPurchase_order_list() {
		return purchase_order_list;
	}

	public void setPurchase_order_list(DataRelation purchase_order_list) {
		this.purchase_order_list = purchase_order_list;
	}

	public Integer getTotal(){
		return this.total;
	}
	
	public void setTotal(Integer value){
		this.total = value;
	}
	
}