package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class CreateDeliveryResponse {
	
	/**
	* 出库单Id
	*/
	
	private String delivery_id;
	
	/**
	* 入库编号
	*/
	
	private String storage_no;
	
	public String getDelivery_id(){
		return this.delivery_id;
	}
	
	public void setDelivery_id(String value){
		this.delivery_id = value;
	}
	public String getStorage_no(){
		return this.storage_no;
	}
	
	public void setStorage_no(String value){
		this.storage_no = value;
	}
	
}