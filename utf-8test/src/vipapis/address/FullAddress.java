package vipapis.address;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class FullAddress {
	
	/**
	* 省/市/区列表
	*/
	
	private List<vipapis.address.City> cities;
	
	/**
	* 地址信息
	*/
	
	private vipapis.address.Address address;
	
	public List<vipapis.address.City> getCities(){
		return this.cities;
	}
	
	public void setCities(List<vipapis.address.City> value){
		this.cities = value;
	}
	public vipapis.address.Address getAddress(){
		return this.address;
	}
	
	public void setAddress(vipapis.address.Address value){
		this.address = value;
	}
	
}