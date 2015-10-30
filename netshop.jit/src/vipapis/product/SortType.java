package vipapis.product;



public enum SortType {
	
	
	/**
	* 默认
	*/
	SORT_DEFAULT(0),
	
	/**
	* 折扣降序
	*/
	DISCOUNT_DOWN(1),
	
	/**
	* 折扣升序
	*/
	DISCOUNT_UP(2),
	
	/**
	* 价格升序
	*/
	PRICE_UP(3),
	
	/**
	* 价格降序
	*/
	PRICE_DOWN(4),
	
	/**
	* 销量降序
	*/
	SALECOUNT_DOWN(5),
	
	/**
	* 销量升序
	*/
	SALECOUNT_UP(6);
	
	private final int value;
	private SortType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static SortType findByValue(int value){
		
		switch(value){
			
			case 0: return SORT_DEFAULT; 
			case 1: return DISCOUNT_DOWN; 
			case 2: return DISCOUNT_UP; 
			case 3: return PRICE_UP; 
			case 4: return PRICE_DOWN; 
			case 5: return SALECOUNT_DOWN; 
			case 6: return SALECOUNT_UP; 
			
			default: return null; 
			
		}
		
	}
	
	
}