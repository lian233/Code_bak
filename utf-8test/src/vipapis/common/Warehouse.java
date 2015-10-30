package vipapis.common;



public enum Warehouse {
	
	
	/**
	* 华南：南海仓
	*/
	VIP_NH(1),
	
	/**
	* 华东：上海仓
	*/
	VIP_SH(2),
	
	/**
	* 西北：成都仓
	*/
	VIP_CD(3),
	
	/**
	* 北京仓
	*/
	VIP_BJ(4),
	
	/**
	* 华中：鄂州仓
	*/
	VIP_HZ(5);
	
	private final int value;
	private Warehouse(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static Warehouse findByValue(int value){
		
		switch(value){
			
			case 1: return VIP_NH; 
			case 2: return VIP_SH; 
			case 3: return VIP_CD; 
			case 4: return VIP_BJ; 
			case 5: return VIP_HZ; 
			
			default: return null; 
			
		}
		
	}
	
	
}