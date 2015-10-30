package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class DvdOrderDetail {
	
	/**
	* 品牌名称
	*/
	
	private String brand_name;
	
	/**
	* 商品名称
	*/
	
	private String product_name;
	
	/**
	* 尺码
	*/
	
	private String size;
	
	/**
	* 货号
	*/
	
	private String product_no;
	
	/**
	* 条形码
	*/
	
	private String barcode;
	
	/**
	* 商品数量
	*/
	
	private Integer amount;
	
	/**
	* 单价
	*/
	
	private Double price;
	
	/**
	* 订单号
	*/
	
	private String order_id;
	
	public String getBrand_name(){
		return this.brand_name;
	}
	
	public void setBrand_name(String value){
		this.brand_name = value;
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
	public String getProduct_no(){
		return this.product_no;
	}
	
	public void setProduct_no(String value){
		this.product_no = value;
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
	public Double getPrice(){
		return this.price;
	}
	
	public void setPrice(Double value){
		this.price = value;
	}
	public String getOrder_id(){
		return this.order_id;
	}
	
	public void setOrder_id(String value){
		this.order_id = value;
	}
	
}