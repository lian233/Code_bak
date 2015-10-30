package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class PackageProduct {
	
	/**
	* 产品条形码
	* @sampleValue barcode 
	*/
	
	private String barcode;
	
	/**
	* 产品数量
	* @sampleValue amount 
	*/
	
	private int amount;
	
	public String getBarcode(){
		return this.barcode;
	}
	
	public void setBarcode(String value){
		this.barcode = value;
	}
	public int getAmount(){
		return this.amount;
	}
	
	public void setAmount(int value){
		this.amount = value;
	}
	
}