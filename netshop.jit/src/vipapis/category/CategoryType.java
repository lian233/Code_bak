package vipapis.category;



public enum CategoryType {
	
	
	/**
	* 顶层类目节点
	*/
	TopCategory(0),
	
	/**
	* 中间层类目节点
	*/
	SubCategory(1),
	
	/**
	* 最低层类目节点，商品只能挂载到这个节点上
	*/
	LeafCategory(2);
	
	private final int value;
	private CategoryType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static CategoryType findByValue(int value){
		
		switch(value){
			
			case 0: return TopCategory; 
			case 1: return SubCategory; 
			case 2: return LeafCategory; 
			
			default: return null; 
			
		}
		
	}
	
	
}