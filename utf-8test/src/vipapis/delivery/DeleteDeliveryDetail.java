package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class DeleteDeliveryDetail {
	
	/**
	* 送货单编号
	*/
	
	private String delivery_no;
	
	/**
	* 条形码
	*/
	
	private String barcode;
	
	/**
	* 出仓明细Id
	*/
	
	private String delivery_detail_id;
	
	public String getDelivery_no(){
		return this.delivery_no;
	}
	
	public void setDelivery_no(String value){
		this.delivery_no = value;
	}
	public String getBarcode(){
		return this.barcode;
	}
	
	public void setBarcode(String value){
		this.barcode = value;
	}
	public String getDelivery_detail_id(){
		return this.delivery_detail_id;
	}
	
	public void setDelivery_detail_id(String value){
		this.delivery_detail_id = value;
	}
	
}