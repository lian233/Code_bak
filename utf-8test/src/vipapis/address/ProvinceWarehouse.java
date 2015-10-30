package vipapis.address;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class ProvinceWarehouse {
	
	/**
	* 所属编码
	* @sampleValue warehouse VIP_BJ
	*/
	
	private String warehouse;
	
	/**
	* 省/市/区
	*/
	
	private List<vipapis.address.City> children;
	
	public String getWarehouse(){
		return this.warehouse;
	}
	
	public void setWarehouse(String value){
		this.warehouse = value;
	}
	public List<vipapis.address.City> getChildren(){
		return this.children;
	}
	
	public void setChildren(List<vipapis.address.City> value){
		this.children = value;
	}
	
}