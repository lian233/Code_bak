package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class DvdOrderStatus {
	
	/**
	* ID
	*/
	
	private String id;
	
	/**
	* 订单编码
	*/
	
	private String order_id;
	
	/**
	* 旧订单编码
	*/
	
	private String old_order_id;
	
	/**
	* 订单状态编码
	*/
	
	private vipapis.common.OrderStatus state;
	
	/**
	* 仓库名称
	*/
	
	private String warehouse_name;
	
	/**
	* EBS分仓代码
	*/
	
	private String ebs_warehouse_code;
	
	/**
	* B2C分仓代码
	*/
	
	private String b2c_warehouse_code;
	
	/**
	* 客户类型
	*/
	
	private Integer user_type;
	
	/**
	* 客户名称
	*/
	
	private String user_name;
	
	public String getId(){
		return this.id;
	}
	
	public void setId(String value){
		this.id = value;
	}
	public String getOrder_id(){
		return this.order_id;
	}
	
	public void setOrder_id(String value){
		this.order_id = value;
	}
	public String getOld_order_id(){
		return this.old_order_id;
	}
	
	public void setOld_order_id(String value){
		this.old_order_id = value;
	}
	public vipapis.common.OrderStatus getState(){
		return this.state;
	}
	
	public void setState(vipapis.common.OrderStatus value){
		this.state = value;
	}
	public String getWarehouse_name(){
		return this.warehouse_name;
	}
	
	public void setWarehouse_name(String value){
		this.warehouse_name = value;
	}
	public String getEbs_warehouse_code(){
		return this.ebs_warehouse_code;
	}
	
	public void setEbs_warehouse_code(String value){
		this.ebs_warehouse_code = value;
	}
	public String getB2c_warehouse_code(){
		return this.b2c_warehouse_code;
	}
	
	public void setB2c_warehouse_code(String value){
		this.b2c_warehouse_code = value;
	}
	public Integer getUser_type(){
		return this.user_type;
	}
	
	public void setUser_type(Integer value){
		this.user_type = value;
	}
	public String getUser_name(){
		return this.user_name;
	}
	
	public void setUser_name(String value){
		this.user_name = value;
	}
	
}