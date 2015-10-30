package vipapis.address;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class City {
	
	/**
	* 省/市/区ID
	* @sampleValue city_id 104104
	*/
	
	private String city_id;
	
	/**
	* 省/市/区名称
	* @sampleValue city_name 广东省
	*/
	
	private String city_name;
	
	public String getCity_id(){
		return this.city_id;
	}
	
	public void setCity_id(String value){
		this.city_id = value;
	}
	public String getCity_name(){
		return this.city_name;
	}
	
	public void setCity_name(String value){
		this.city_name = value;
	}
	
}