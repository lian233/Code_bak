package vipapis.category;



public enum DataType {
	
	
	/**
	* 文本数据类型（不包含英文标点符号）
	*/
	Text(0),
	
	/**
	* 数值数据类型，有单位Unit字段
	*/
	Numeric(1),
	
	/**
	* 选项数据类型，有选项Option数据记录
	*/
	option(2);
	
	private final int value;
	private DataType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static DataType findByValue(int value){
		
		switch(value){
			
			case 0: return Text; 
			case 1: return Numeric; 
			case 2: return option; 
			
			default: return null; 
			
		}
		
	}
	
	
}