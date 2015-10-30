package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class GetPickListResponse {
	
	/**
	* 拣货单信息列表
	*/
	
	private List<vipapis.delivery.Pick> picks;
	
	/**
	* 记录总条数
	*/
	
	private Integer total;
	
	public List<vipapis.delivery.Pick> getPicks(){
		return this.picks;
	}
	
	public void setPicks(List<vipapis.delivery.Pick> value){
		this.picks = value;
	}
	public Integer getTotal(){
		return this.total;
	}
	
	public void setTotal(Integer value){
		this.total = value;
	}
	
}