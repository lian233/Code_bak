package vipapis.common;



public enum VendorType {
	
	
	/**
	* common
	*/
	COMMON(1),
	
	/**
	* 3PL
	*/
	_3PL(2);
	
	private final int value;
	private VendorType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static VendorType findByValue(int value){
		
		switch(value){
			
			case 1: return COMMON; 
			case 2: return _3PL; 
			
			default: return null; 
			
		}
		
	}
	
	
}