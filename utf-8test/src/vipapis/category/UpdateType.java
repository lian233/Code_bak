package vipapis.category;



public enum UpdateType {
	
	
	/**
	* 添加
	*/
	Insert(0),
	
	/**
	* 更新
	*/
	Update(1),
	
	/**
	* 删除
	*/
	Delete(2);
	
	private final int value;
	private UpdateType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static UpdateType findByValue(int value){
		
		switch(value){
			
			case 0: return Insert; 
			case 1: return Update; 
			case 2: return Delete; 
			
			default: return null; 
			
		}
		
	}
	
	
}