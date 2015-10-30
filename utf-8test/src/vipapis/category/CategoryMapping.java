package vipapis.category;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class CategoryMapping {
	
	/**
	* 源分类
	*/
	
	private vipapis.category.Category sourcecategory;
	
	/**
	* 属性条件, 格式：属性ID1:选项ID1,选项ID2|属性ID2:选项ID1,选项ID2
	*/
	
	private String filter;
	
	public vipapis.category.Category getSourcecategory(){
		return this.sourcecategory;
	}
	
	public void setSourcecategory(vipapis.category.Category value){
		this.sourcecategory = value;
	}
	public String getFilter(){
		return this.filter;
	}
	
	public void setFilter(String value){
		this.filter = value;
	}
	
}