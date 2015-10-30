package vipapis.category;



public enum AttributeType {
	
	
	/**
	* 自然属性
	*/
	Normal(0),
	
	/**
	* 标签属性
	*/
	Tag(1);
	
	private final int value;
	private AttributeType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static AttributeType findByValue(int value){
		
		switch(value){
			
			case 0: return Normal; 
			case 1: return Tag; 
			
			default: return null; 
			
		}
		
	}
	
	
}