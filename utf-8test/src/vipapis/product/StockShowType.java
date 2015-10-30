package vipapis.product;



public enum StockShowType {
	
	
	/**
	* 全部显示
	*/
	SHOW_ALL(0),
	
	/**
	* 只显示有库存的
	*/
	SHOW_STOCK(1);
	
	private final int value;
	private StockShowType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static StockShowType findByValue(int value){
		
		switch(value){
			
			case 0: return SHOW_ALL; 
			case 1: return SHOW_STOCK; 
			
			default: return null; 
			
		}
		
	}
	
	
}