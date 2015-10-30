package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class Package {
	
	/**
	* 包裹产品信息列表
	* @sampleValue package_product_list 
	*/
	
	private List<vipapis.delivery.PackageProduct> package_product_list;
	
	/**
	* 订单号码
	* @sampleValue transport_no 
	*/
	
	private String transport_no;
	
	public List<vipapis.delivery.PackageProduct> getPackage_product_list(){
		return this.package_product_list;
	}
	
	public void setPackage_product_list(List<vipapis.delivery.PackageProduct> value){
		this.package_product_list = value;
	}
	public String getTransport_no(){
		return this.transport_no;
	}
	
	public void setTransport_no(String value){
		this.transport_no = value;
	}
	
}