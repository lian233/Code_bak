package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class SimplePick {
	
	/**
	* 拣货单编号
	*/
	
	private String pick_no;
	
	/**
	* 拣货单类别
	*/
	
	private String pick_type;
	
	/**
	* 送货仓库
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