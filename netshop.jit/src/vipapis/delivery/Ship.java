package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class Ship {
	
	/**
	* 订单号码
	* @sampleValue order_id 
	*/
	
	private String order_id;
	
	/**
	* 承运商编码
	* @sampleValue carrier_code 
	*/
	
	private String carrier_code;
	
	/**
	* 承运商名称
	* @sampleValue carrier_name 
	*/
	
	private String carrier_name;
	
	/**
	* 包裹类型
	* @sampleValue package_type 
	*/
	
	private String package_type;
	
	/**
	* 包裹信息
	* @sampleValue packages 
	*/
	
	private List<vipapis.delivery.Package> packages;
	
	public String getOrder_id(){
		return this.order_id;
	}
	
	public void setOrder_id(String value){
		this.order_id = value;
	}
	public String getCarrier_code(){
		return this.carrier_code;
	}
	
	public void setCarrier_code(String value){
		this.carrier_code = value;
	}
	public String getCarrier_name(){
		return this.carrier_name;
	}
	
	public void setCarrier_name(String value){
		this.carrier_name = value;
	}
	public String getPackage_type(){
		return this.package_type;
	}
	
	public void setPackage_type(String value){
		this.package_type = value;
	}
	public List<vipapis.delivery.Package> getPackages(){
		return this.packages;
	}
	
	public void setPackages(List<vipapis.delivery.Package> value){
		this.packages = value;
	}
	
}