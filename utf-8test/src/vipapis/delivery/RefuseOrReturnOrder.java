package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class RefuseOrReturnOrder {
	
	/**
	* 订单编号
	*/
	
	private String order_id;
	
	/**
	* 承运商名称
	*/
	
	private String carrier_shortname;
	
	/**
	* 运单号
	*/
	
	private String transport_no;
	
	/**
	* 拒收商品列表
	*/
	
	private List<vipapis.delivery.RefuseOrReturnProduct> refuse_or_return_product_list;
	
	public String getOrder_id(){
		return this.order_id;
	}
	
	public void setOrder_id(String value){
		this.order_id = value;
	}
	public String getCarrier_shortname(){
		return this.carrier_shortname;
	}
	
	public void setCarrier_shortname(String value){
		this.carrier_shortname = value;
	}
	public String getTransport_no(){
		return this.transport_no;
	}
	
	public void setTransport_no(String value){
		this.transport_no = value;
	}
	public List<vipapis.delivery.RefuseOrReturnProduct> getRefuse_or_return_product_list(){
		return this.refuse_or_return_product_list;
	}
	
	public void setRefuse_or_return_product_list(List<vipapis.delivery.RefuseOrReturnProduct> value){
		this.refuse_or_return_product_list = value;
	}
	
}