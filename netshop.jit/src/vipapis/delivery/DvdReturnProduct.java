package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class DvdReturnProduct {
	
	/**
	* 商品名称
	* @sampleValue product_name 
	*/
	
	private String product_name;
	
	/**
	* 订单编号
	* @sampleValue order_id 
	*/
	
	private String order_id;
	
	/**
	* PO单号
	* @sampleValue po_no 
	*/
	
	private String po_no;
	
	/**
	* 条形码
	* @sampleValue barcode 
	*/
	
	private String barcode;
	
	/**
	* 退货商品数量
	* @sampleValue amount 
	*/
	
	private Integer amount;
	
	public String getProduct_name(){
		return this.product_name;
	}
	
	public void setProduct_name(String value){
		this.product_name = value;
	}
	public String getOrder_id(){
		return this.order_id;
	}
	
	public void setOrder_id(String value){
		this.order_id = value;
	}
	public String getPo_no(){
		return this.po_no;
	}
	
	public void setPo_no(String value){
		this.po_no = value;
	}
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