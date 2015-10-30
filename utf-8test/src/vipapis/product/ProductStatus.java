package vipapis.product;



public enum ProductStatus {
	
	
	/**
	* 主档待提交
	*/
	MASTER_DRAFT(11),
	
	/**
	* 主档待审核
	*/
	MASTER_PENDING(12),
	
	/**
	* 主档审核通过
	*/
	MASTER_PASS(13),
	
	/**
	* 主档审不通过
	*/
	MASTER_REJECT(14),
	
	/**
	* 可售待提交
	*/
	SALE_DRAFT(21),
	
	/**
	* 可售待审核
	*/
	SALE_PENDING(22),
	
	/**
	* 可售审核通过
	*/
	SALE_PASS(23),
	
	/**
	* 可售审核不通过
	*/
	SALE_REJECT(24),
	
	/**
	* 禁售
	*/
	OUTSALE(3),
	
	/**
	* 删除
	*/
	DELETED(-1);
	
	private final int value;
	private ProductStatus(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static ProductStatus findByValue(int value){
		
		switch(value){
			
			case 11: return MASTER_DRAFT; 
			case 12: return MASTER_PENDING; 
			case 13: return MASTER_PASS; 
			case 14: return MASTER_REJECT; 
			case 21: return SALE_DRAFT; 
			case 22: return SALE_PENDING; 
			case 23: return SALE_PASS; 
			case 24: return SALE_REJECT; 
			case 3: return OUTSALE; 
			case -1: return DELETED; 
			
			default: return null; 
			
		}
		
	}
	
	
}