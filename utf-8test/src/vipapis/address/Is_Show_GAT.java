package vipapis.address;



public enum Is_Show_GAT {
	
	
	/**
	* 只显示大陆地址
	*/
	SHOW_MAINLAND(0),
	
	/**
	* 只显示港澳台
	*/
	SHOW_GAT(1),
	
	/**
	* 显示全部
	*/
	SHOW_ALL(-1);
	
	private final int value;
	private Is_Show_GAT(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static Is_Show_GAT findByValue(int value){
		
		switch(value){
			
			case 0: return SHOW_MAINLAND; 
			case 1: return SHOW_GAT; 
			case -1: return SHOW_ALL; 
			
			default: return null; 
			
		}
		
	}
	
	
}