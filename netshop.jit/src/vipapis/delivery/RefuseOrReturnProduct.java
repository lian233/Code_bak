package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class RefuseOrReturnProduct {
	
	/**
	* 条形码
	*/
	
	private String barcode;
	
	/**
	* 商品数量
	*/
	
	private Integer amount;
	
	public String getBarcode(){
		return this.barcode;
	}
	
	public void setBarcode(String value){
		this.barcode = value;
	}
	public Integer getAmount(){
		return this.amount;
	}
	
	public void setAmount(Integer value){
		this.amount = value;
	}
	
}