package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class GetPoSkuListResponse {
	
	/**
	* PO单SKU信息
	*/
	
	private List<vipapis.delivery.PurchaseOrderSku> purchase_order_sku_list;
	
	/**
	* 记录总条数
	*/
	
	private Integer total;
	
	public List<vipapis.delivery.PurchaseOrderSku> getPurchase_order_sku_list(){
		return this.purchase_order_sku_list;
	}
	
	public void setPurchase_order_sku_list(List<vipapis.delivery.PurchaseOrderSku> value){
		this.purchase_order_sku_list = value;
	}
	public Integer getTotal(){
		return this.total;
	}
	
	public void setTotal(Integer value){
		this.total = value;
	}
	
}