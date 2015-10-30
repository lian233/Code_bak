package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;

import com.wofu.base.util.BusinessObject;



public  class SimplePick extends BusinessObject{
	
	/**
	* 
	*/
	
	private String pick_no;
	
	/**
	* 
	*/
	
	private String pick_type;
	
	/**
	* 
	*/
	
	private String warehouse;
	
	public String getPick_no(){
		return this.pick_no;
	}
	
	public void setPick_no(String value){
		this.pick_no = value;
	}
	public String getPick_type(){
		return this.pick_type;
	}
	
	public void setPick_type(String value){
		this.pick_type = value;
	}
	public String getWarehouse(){
		return this.warehouse;
	}
	
	public void setWarehouse(String value){
		this.warehouse = value;
	}
	
}