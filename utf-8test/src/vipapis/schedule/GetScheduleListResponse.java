package vipapis.schedule;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class GetScheduleListResponse {
	
	/**
	* 档期列表
	*/
	
	private List<vipapis.schedule.Schedule> schedules;
	
	/**
	* 总记录条数
	*/
	
	private int total;
	
	public List<vipapis.schedule.Schedule> getSchedules(){
		return this.schedules;
	}
	
	public void setSchedules(List<vipapis.schedule.Schedule> value){
		this.schedules = value;
	}
	public int getTotal(){
		return this.total;
	}
	
	public void setTotal(int value){
		this.total = value;
	}
	
}