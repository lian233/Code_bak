package vipapis.category;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class Category {
	
	/**
	* 分类ID
	*/
	
	private int category_id;
	
	/**
	* 分类名称
	*/
	
	private String category_name;
	
	/**
	* 英文名称
	*/
	
	private String english_name;
	
	/**
	* 分类描述
	*/
	
	private String description;
	
	/**
	* 分类类型
	*/
	
	private vipapis.category.CategoryType category_type;
	
	/**
	* 关键字
	*/
	
	private String keywords;
	
	/**
	* 标记位，用于后续扩展属性
	*/
	
	private Long flags;
	
	/**
	* 展示导航ID
	*/
	
	private Integer hierarchy_id;
	
	/**
	* 最后变更时间, 获取变更的节点接口使用
	*/
	
	private Long last_updatetime;
	
	/**
	* 相关分类
	*/
	
	private List<Integer> related_categories;
	
	/**
	* 子分类
	*/
	
	private List<vipapis.category.Category> children;
	
	/**
	* 分类映射
	*/
	
	private List<vipapis.category.CategoryMapping> mapping;
	
	/**
	* 主父分类
	*/
	
	private Integer major_parent_category_id;
	
	/**
	* 非主父分类
	*/
	
	private List<Integer> salve_parent_category_ids;
	
	/**
	* 分类属性
	*/
	
	private List<vipapis.category.Attribute> attributes;
	
	public int getCategory_id(){
		return this.category_id;
	}
	
	public void setCategory_id(int value){
		this.category_id = value;
	}
	public String getCategory_name(){
		return this.category_name;
	}
	
	public void setCategory_name(String value){
		this.category_name = value;
	}
	public String getEnglish_name(){
		return this.english_name;
	}
	
	public void setEnglish_name(String value){
		this.english_name = value;
	}
	public String getDescription(){
		return this.description;
	}
	
	public void setDescription(String value){
		this.description = value;
	}
	public vipapis.category.CategoryType getCategory_type(){
		return this.category_type;
	}
	
	public void setCategory_type(vipapis.category.CategoryType value){
		this.category_type = value;
	}
	public String getKeywords(){
		return this.keywords;
	}
	
	public void setKeywords(String value){
		this.keywords = value;
	}
	public Long getFlags(){
		return this.flags;
	}
	
	public void setFlags(Long value){
		this.flags = value;
	}
	public Integer getHierarchy_id(){
		return this.hierarchy_id;
	}
	
	public void setHierarchy_id(Integer value){
		this.hierarchy_id = value;
	}
	public Long getLast_updatetime(){
		return this.last_updatetime;
	}
	
	public void setLast_updatetime(Long value){
		this.last_updatetime = value;
	}
	public List<Integer> getRelated_categories(){
		return this.related_categories;
	}
	
	public void setRelated_categories(List<Integer> value){
		this.related_categories = value;
	}
	public List<vipapis.category.Category> getChildren(){
		return this.children;
	}
	
	public void setChildren(List<vipapis.category.Category> value){
		this.children = value;
	}
	public List<vipapis.category.CategoryMapping> getMapping(){
		return this.mapping;
	}
	
	public void setMapping(List<vipapis.category.CategoryMapping> value){
		this.mapping = value;
	}
	public Integer getMajor_parent_category_id(){
		return this.major_parent_category_id;
	}
	
	public void setMajor_parent_category_id(Integer value){
		this.major_parent_category_id = value;
	}
	public List<Integer> getSalve_parent_category_ids(){
		return this.salve_parent_category_ids;
	}
	
	public void setSalve_parent_category_ids(List<Integer> value){
		this.salve_parent_category_ids = value;
	}
	public List<vipapis.category.Attribute> getAttributes(){
		return this.attributes;
	}
	
	public void setAttributes(List<vipapis.category.Attribute> value){
		this.attributes = value;
	}
	
}