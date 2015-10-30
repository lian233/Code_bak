package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class Carrier {
	
	/**
	* tms端承运商ID
	* @sampleValue tms_carrier_id 
	*/
	
	private String tms_carrier_id;
	
	/**
	* 承运商全称
	* @sampleValue carrier_name 
	*/
	
	private String carrier_name;
	
	/**
	* 承运商简称
	* @sampleValue carrier_shortname 
	*/
	
	private String carrier_shortname;
	
	/**
	* 承运商编码
	* @sampleValue carrier_code 
	*/
	
	private String carrier_code;
	
	/**
	* 承运商状态 1启用， 0 关闭
	* @sampleValue carrier_isvalid 
	*/
	
	private Integer carrier_isvalid;
	
	public String getTms_carrier_id(){
		return this.tms_carrier_id;
	}
	
	public void setTms_carrier_id(String value){
		this.tms_carrier_id = value;
	}
	public String getCarrier_name(){
		return this.carrier_name;
	}
	
	public void setCarrier_name(String value){
		this.carrier_name = value;
	}
	public String getCarrier_shortname(){
		return this.carrier_shortname;
	}
	
	public void setCarrier_shortname(String value){
		this.carrier_shortname = value;
	}
	public String getCarrier_code(){
		return this.carrier_code;
	}
	
	public void setCarrier_code(String value){
		this.carrier_code = value;
	}
	public Integer getCarrier_isvalid(){
		return this.carrier_isvalid;
	}
	
	public void setCarrier_isvalid(Integer value){
		this.carrier_isvalid = value;
	}
	
}