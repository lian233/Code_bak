package vipapis.category;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class CategoryUpdate {
	
	/**
	* 更新类型
	*/
	
	private vipapis.category.UpdateType updateType;
	
	/**
	* 变更分类节点
	*/
	
	private vipapis.category.Category category;
	
	public vipapis.category.UpdateType getUpdateType(){
		return this.updateType;
	}
	
	public void setUpdateType(vipapis.category.UpdateType value){
		this.updateType = value;
	}
	public vipapis.category.Category getCategory(){
		return this.category;
	}
	
	public void setCategory(vipapis.category.Category value){
		this.category = value;
	}
	
}