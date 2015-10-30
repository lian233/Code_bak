package vipapis.product;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class VendorProductSkuKey {
	
	/**
	* 供应商ID
	* @sampleValue vendor_id 525
	*/
	
	private int vendor_id;
	
	/**
	* 条形码
	* @sampleValue barcode 113113302011245
	*/
	
	private String barcode;
	
	public int getVendor_id(){
		return this.vendor_id;
	}
	
	public void setVendor_id(int value){
		this.vendor_id = value;
	}
	public String getBarcode(){
		return this.barcode;
	}
	
	public void setBarcode(String value){
		this.barcode = value;
	}
	
}