package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class DvdReturn {
	
	/**
	* 供应商ID
	* @sampleValue vendor_id 
	*/
	
	private Integer vendor_id;
	
	/**
	* 订单编号
	* @sampleValue order_id 
	*/
	
	private String order_id;
	
	/**
	* 退货申请单状态
	* @sampleValue state 
	*/
	
	private String state;
	
	/**
	* 退货原因
	* @sampleValue return_reason 
	*/
	
	private String return_reason;
	
	/**
	* 从b2c拉取客退订单状态时间
	* @sampleValue create_time 
	*/
	
	private String create_time;
	
	/**
	* 客退申请单号
	* @sampleValue back_sn 
	*/
	
	private String back_sn;
	
	public Integer getVendor_id(){
		return this.vendor_id;
	}
	
	public void setVendor_id(Integer value){
		this.vendor_id = value;
	}
	public String getOrder_id(){
		return this.order_id;
	}
	
	public void setOrder_id(String value){
		this.order_id = value;
	}
	public String getState(){
		return this.state;
	}
	
	public void setState(String value){
		this.state = value;
	}
	public String getReturn_reason(){
		return this.return_reason;
	}
	
	public void setReturn_reason(String value){
		this.return_reason = value;
	}
	public String getCreate_time(){
		return this.create_time;
	}
	
	public void setCreate_time(String value){
		this.create_time = value;
	}
	public String getBack_sn(){
		return this.back_sn;
	}
	
	public void setBack_sn(String value){
		this.back_sn = value;
	}
	
}