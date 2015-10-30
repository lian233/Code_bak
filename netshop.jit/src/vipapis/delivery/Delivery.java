package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;

import com.wofu.base.util.BusinessObject;



public  class Delivery extends BusinessObject{
	
	/**
	* 
	*/
	
	private vipapis.common.VendorType vendor_type;
	
	/**
	* 
	*/
	
	private String barcode;
	
	/**
	* 
	*/
	
	private String box_no;
	
	/**
	* 
	*/
	
	private String pick_no;
	
	/**
	* 
	*/
	
	private int amount;
	
	public vipapis.common.VendorType getVendor_type(){
		return this.vendor_type;
	}
	
	public void setVendor_type(vipapis.common.VendorType value){
		this.vendor_type = value;
	}
	public String getBarcode(){
		return this.barcode;
	}
	
	public void setBarcode(String value){
		this.barcode = value;
	}
	public String getBox_no(){
		return this.box_no;
	}
	
	public void setBox_no(String value){
		this.box_no = value;
	}
	public String getPick_no(){
		return this.pick_no;
	}
	
	public void setPick_no(String value){
		this.pick_no = value;
	}
	public int getAmount(){
		return this.amount;
	}
	
	public void setAmount(int value){
		this.amount = value;
	}
	
}