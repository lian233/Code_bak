package vipapis.category;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class Option {
	
	/**
	* 属性ID
	*/
	
	private int attributeId;
	
	/**
	* 选项ID
	*/
	
	private int optionId;
	
	/**
	* 选项名称
	*/
	
	private String name;
	
	/**
	* 英文名称
	*/
	
	private String englishname;
	
	/**
	* 选项描述
	*/
	
	private String description;
	
	/**
	* 选项分组
	*/
	
	private String hierarchyGroup;
	
	/**
	* 排序因子
	*/
	
	private Integer sort;
	
	/**
	* 父选项ID，为0表示都独立选项，否则为依赖选项
	*/
	
	private Integer parentOptionId;
	
	/**
	* 是否为虚拟选项，虚拟选型是多个其它选型的聚合
	*/
	
	private Boolean isVirtual;
	
	/**
	* 如果是虚拟选项，这里是其它需要聚合的多个选项
	*/
	
	private List<Integer> realOptions;
	
	/**
	* 外国名称，格式为JSON
	*/
	
	private String foreignname;
	
	/**
	* 选项外部信息，格式为JSON
	*/
	
	private String externaldata;
	
	public int getAttributeId(){
		return this.attributeId;
	}
	
	public void setAttributeId(int value){
		this.attributeId = value;
	}
	public int getOptionId(){
		return this.optionId;
	}
	
	public void setOptionId(int value){
		this.optionId = value;
	}
	public String getName(){
		return this.name;
	}
	
	public void setName(String value){
		this.name = value;
	}
	public String getEnglishname(){
		return this.englishname;
	}
	
	public void setEnglishname(String value){
		this.englishname = value;
	}
	public String getDescription(){
		return this.description;
	}
	
	public void setDescription(String value){
		this.description = value;
	}
	public String getHierarchyGroup(){
		return this.hierarchyGroup;
	}
	
	public void setHierarchyGroup(String value){
		this.hierarchyGroup = value;
	}
	public Integer getSort(){
		return this.sort;
	}
	
	public void setSort(Integer value){
		this.sort = value;
	}
	public Integer getParentOptionId(){
		return this.parentOptionId;
	}
	
	public void setParentOptionId(Integer value){
		this.parentOptionId = value;
	}
	public Boolean getIsVirtual(){
		return this.isVirtual;
	}
	
	public void setIsVirtual(Boolean value){
		this.isVirtual = value;
	}
	public List<Integer> getRealOptions(){
		return this.realOptions;
	}
	
	public void setRealOptions(List<Integer> value){
		this.realOptions = value;
	}
	public String getForeignname(){
		return this.foreignname;
	}
	
	public void setForeignname(String value){
		this.foreignname = value;
	}
	public String getExternaldata(){
		return this.externaldata;
	}
	
	public void setExternaldata(String value){
		this.externaldata = value;
	}
	
}