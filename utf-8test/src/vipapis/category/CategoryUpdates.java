package vipapis.category;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class CategoryUpdates {
	
	/**
	* 变更开始时间
	*/
	
	private long sinceUpdateTime;
	
	/**
	* 变更结束时间
	*/
	
	private long lastUpdateTime;
	
	/**
	* 变更节点集合
	*/
	
	private List<vipapis.category.CategoryUpdate> categories;
	
	public long getSinceUpdateTime(){
		return this.sinceUpdateTime;
	}
	
	public void setSinceUpdateTime(long value){
		this.sinceUpdateTime = value;
	}
	public long getLastUpdateTime(){
		return this.lastUpdateTime;
	}
	
	public void setLastUpdateTime(long value){
		this.lastUpdateTime = value;
	}
	public List<vipapis.category.CategoryUpdate> getCategories(){
		return this.categories;
	}
	
	public void setCategories(List<vipapis.category.CategoryUpdate> value){
		this.categories = value;
	}
	
}