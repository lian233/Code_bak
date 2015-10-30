package vipapis.product;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class VendorProductFailItem {
	
	/**
	* 条形码
	* @sampleValue barcode 113113302011245
	*/
	
	private String barcode;
	
	/**
	* 失败信息
	* @sampleValue msg 供应商不存在
	*/
	
	private String msg;
	
	public String getBarcode(){
		return this.barcode;
	}
	
	public void setBarcode(String value){
		this.barcode = value;
	}
	public String getMsg(){
		return this.msg;
	}
	
	public void setMsg(String value){
		this.msg = value;
	}
	
}