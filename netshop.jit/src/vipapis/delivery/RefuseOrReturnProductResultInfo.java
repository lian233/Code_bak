package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class RefuseOrReturnProductResultInfo {
	
	/**
	* 订单编号
	*/
	
	private String order_id;
	
	/**
	* 供应商Id
	*/
	
	private String vendor_id;
	
	/**
	* 运单号
	*/
	
	private String transport_no;
	
	/**
	* 承运商名称
	*/
	
	private String carrier_shortname;
	
	/**
	* 商品列表
	*/
	
	private List<vipapis.delivery.RefuseOrReturnProduct> product_list;
	
	/**
	* 错误信息
	*/
	
	private String error_msg;
	
	public String getOrder_id(){
		return this.order_id;
	}
	
	public void setOrder_id(String value){
		this.order_id = value;
	}
	public String getVendor_id(){
		return this.vendor_id;
	}
	
	public void setVendor_id(String value){
		this.vendor_id = value;
	}
	public String getTransport_no(){
		return this.transport_no;
	}
	
	public void setTransport_no(String value){
		this.transport_no = value;
	}
	public String getCarrier_shortname(){
		return this.carrier_shortname;
	}
	
	public void setCarrier_shortname(String value){
		this.carrier_shortname = value;
	}
	public List<vipapis.delivery.RefuseOrReturnProduct> getProduct_list(){
		return this.product_list;
	}
	
	public void setProduct_list(List<vipapis.delivery.RefuseOrReturnProduct> value){
		this.product_list = value;
	}
	public String getError_msg(){
		return this.error_msg;
	}
	
	public void setError_msg(String value){
		this.error_msg = value;
	}
	
}