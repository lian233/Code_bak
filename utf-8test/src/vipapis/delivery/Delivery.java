package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class Delivery {
	
	/**
	* 供应商类型： COMMON：普通 3pl：3PL
	*/
	
	private vipapis.common.VendorType vendor_type;
	
	/**
	* 条形码
	*/
	
	private String barcode;
	
	/**
	* 供应商箱号
	*/
	
	private String box_no;
	
	/**
	* 拣货单号
	*/
	
	private String pick_no;
	
	/**
	* 商品数量
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