package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class GetPrintDeliveryResponse {
	
	/**
	* 打印页面信息
	*/
	
	private String template;
	
	/**
	* 记录总条数
	*/
	
	private Integer total;
	
	public String getTemplate(){
		return this.template;
	}
	
	public void setTemplate(String value){
		this.template = value;
	}
	public Integer getTotal(){
		return this.total;
	}
	
	public void setTotal(Integer value){
		this.total = value;
	}
	
}