package vipapis.product;



public enum ProductType {
	
	
	/**
	* 普通商品
	*/
	NORMALGOOD(0),
	
	/**
	* 赠品
	*/
	GIVINGGOOD(1),
	
	/**
	* 自由赠
	*/
	FREEGIFT(2),
	
	/**
	* 换购商品
	*/
	EXCHANGEGOOD(3),
	
	/**
	* 捆绑销售商品
	*/
	BUNDINGGOOD(4),
	
	/**
	* 礼品
	*/
	GIFT(5);
	
	private final int value;
	private ProductType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static ProductType findByValue(int value){
		
		switch(value){
			
			case 0: return NORMALGOOD; 
			case 1: return GIVINGGOOD; 
			case 2: return FREEGIFT; 
			case 3: return EXCHANGEGOOD; 
			case 4: return BUNDINGGOOD; 
			case 5: return GIFT; 
			
			default: return null; 
			
		}
		
	}
	
	
}