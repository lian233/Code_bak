package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class ExportOrderByIdResponse {
	
	/**
	* 成功数量
	* @sampleValue success_num 
	*/
	
	private Integer success_num;
	
	/**
	* 成功的数据
	* @sampleValue success_data 
	*/
	
	private List<vipapis.delivery.ExportOrderInfo> success_data;
	
	/**
	* 失败数量
	* @sampleValue fail_num 
	*/
	
	private Integer fail_num;
	
	/**
	* 失败的数据
	* @sampleValue fail_data 
	*/
	
	private List<vipapis.delivery.ExportOrderInfo> fail_data;
	
	public Integer getSuccess_num(){
		return this.success_num;
	}
	
	public void setSuccess_num(Integer value){
		this.success_num = value;
	}
	public List<vipapis.delivery.ExportOrderInfo> getSuccess_data(){
		return this.success_data;
	}
	
	public void setSuccess_data(List<vipapis.delivery.ExportOrderInfo> value){
		this.success_data = value;
	}
	public Integer getFail_num(){
		return this.fail_num;
	}
	
	public void setFail_num(Integer value){
		this.fail_num = value;
	}
	public List<vipapis.delivery.ExportOrderInfo> getFail_data(){
		return this.fail_data;
	}
	
	public void setFail_data(List<vipapis.delivery.ExportOrderInfo> value){
		this.fail_data = value;
	}
	
}