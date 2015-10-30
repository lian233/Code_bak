package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class PickProduct {
	
	/**
	* 虚拟总库存
	*/
	
	private Integer stock;
	
	/**
	* 商品条码
	*/
	
	private String barcode;
	
	/**
	* 货号
	*/
	
	private String art_no;
	
	/**
	* 商品名称
	*/
	
	private String product_name;
	
	/**
	* 尺码
	*/
	
	private String size;
	
	public Integer getStock(){
		return this.stock;
	}
	
	public void setStock(Integer value){
		this.stock = value;
	}
	public String getBarcode(){
		return this.barcode;
	}
	
	public void setBarcode(String value){
		this.barcode = value;
	}
	public String getArt_no(){
		return this.art_no;
	}
	
	public void setArt_no(String value){
		this.art_no = value;
	}
	public String getProduct_name(){
		return this.product_name;
	}
	
	public void setProduct_name(String value){
		this.product_name = value;
	}
	public String getSize(){
		return this.size;
	}
	
	public void setSize(String value){
		this.size = value;
	}
	
}