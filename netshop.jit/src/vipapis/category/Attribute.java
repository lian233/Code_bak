package vipapis.category;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class Attribute {
	
	/**
	* 属性Id
	*/
	
	private int attriute_id;
	
	/**
	* 属性名称
	*/
	
	private String attribute_name;
	
	/**
	* 英文名称
	*/
	
	private String english_name;
	
	/**
	* 属性说明
	*/
	
	private String description;
	
	/**
	* 属性类型：自然属性/Tag属性
	*/
	
	private vipapis.category.AttributeType attribute_type;
	
	/**
	* 数据类型：文本/数值/选项
	*/
	
	private vipapis.category.DataType data_type;
	
	/**
	* 数值单位
	*/
	
	private String unit;
	
	/**
	* 排序因子
	*/
	
	private Integer sort;
	
	/**
	* 属性标记位,条件属性
	*/
	
	private Long flags;
	
	/**
	* 父属性ID，为0表示为独立属性，否则为依赖属性
	*/
	
	private Integer parent_attribute_id;
	
	/**
	* 选项列表
	*/
	
	private List<vipapis.category.Option> options;
	
	public int getAttriute_id(){
		return this.attriute_id;
	}
	
	public void setAttriute_id(int value){
		this.attriute_id = value;
	}
	public String getAttribute_name(){
		return this.attribute_name;
	}
	
	public void setAttribute_name(String value){
		this.attribute_name = value;
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
	public vipapis.category.AttributeType getAttribute_type(){
		return this.attribute_type;
	}
	
	public void setAttribute_type(vipapis.category.AttributeType value){
		this.attribute_type = value;
	}
	public vipapis.category.DataType getData_type(){
		return this.data_type;
	}
	
	public void setData_type(vipapis.category.DataType value){
		this.data_type = value;
	}
	public String getUnit(){
		return this.unit;
	}
	
	public void setUnit(String value){
		this.unit = value;
	}
	public Integer getSort(){
		return this.sort;
	}
	
	public void setSort(Integer value){
		this.sort = value;
	}
	public Long getFlags(){
		return this.flags;
	}
	
	public void setFlags(Long value){
		this.flags = value;
	}
	public Integer getParent_attribute_id(){
		return this.parent_attribute_id;
	}
	
	public void setParent_attribute_id(Integer value){
		this.parent_attribute_id = value;
	}
	public List<vipapis.category.Option> getOptions(){
		return this.options;
	}
	
	public void setOptions(List<vipapis.category.Option> value){
		this.options = value;
	}
	
}