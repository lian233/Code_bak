package vipapis.brand;



public enum BrandSearchType {
	
	
	/**
	* 品牌名称
	*/
	brand_name(0),
	
	/**
	* 品牌英文名
	*/
	brand_name_eng(1);
	
	private final int value;
	private BrandSearchType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static BrandSearchType findByValue(int value){
		
		switch(value){
			
			case 0: return brand_name; 
			case 1: return brand_name_eng; 
			
			default: return null; 
			
		}
		
	}
	
	
}