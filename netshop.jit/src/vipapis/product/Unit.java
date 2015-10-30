package vipapis.product;



public enum Unit {
	
	
	/**
	* 件/个 (piece/pieces) 
	*/
	PIECE(1000),
	
	/**
	* 双 (pair) 
	*/
	PAIR(1001),
	
	/**
	* 套 (set/sets) 
	*/
	SET(1002),
	
	/**
	* 箱 (carton) 
	*/
	CARTON(1003),
	
	/**
	* 打 (dozen) 
	*/
	DOZEN(1004),
	
	/**
	* 桶 (barrel/barrels) 
	*/
	BARREL(1005),
	
	/**
	* 包 (pack/packs) 
	*/
	PACK(1006),
	
	/**
	* 袋 (bag/bags) 
	*/
	BAG(1007),
	
	/**
	* 克 (gram) 
	*/
	GRAM(1008),
	
	/**
	* 千克 (kilogram) 
	*/
	KILOGRAM(1009),
	
	/**
	* 毫升 (milliliter) 
	*/
	MILLILITER(1010),
	
	/**
	* 盎司 (ounce) 
	*/
	OUNCE(1011),
	
	/**
	* 磅 (pound) 
	*/
	POUND(1012),
	
	/**
	* 美吨 (short ton) 
	*/
	SHORT_TON(1013),
	
	/**
	* 加仑 (gallon) 
	*/
	GALLON(1014),
	
	/**
	* 平方米 (square meter) 
	*/
	SQUARE_METER(1015),
	
	/**
	* 立方米 (cubic meter) 
	*/
	CUBIC_METER(1016),
	
	/**
	* 吨 (ton) 
	*/
	TON(1017),
	
	/**
	* 公吨 (metric ton) 
	*/
	METRIC_TON(1018),
	
	/**
	* 英吨 (long ton) 
	*/
	LONG_TON(1019),
	
	/**
	* 厘米 (centimeter) 
	*/
	CENTIMETER(1020),
	
	/**
	* 米 (meter) 
	*/
	METER(1021),
	
	/**
	* 千米 (kilometer) 
	*/
	KILOMETER(1022),
	
	/**
	* 英尺 (feet) 
	*/
	FEET(1023),
	
	/**
	* 英寸 (inch) 
	*/
	INCH(1024),
	
	/**
	* 千升 (kiloliter) 
	*/
	KILOLITER(1025),
	
	/**
	* 升 (liter/liters) 
	*/
	LITER(1026),
	
	/**
	* 毫克 (milligram) 
	*/
	MILLIGRAM(1027),
	
	/**
	* 毫米 (millimeter) 
	*/
	MILLIMETER(1028),
	
	/**
	* 平方英尺 (square feet) 
	*/
	SQUARE_FEET(1029),
	
	/**
	* 平方英寸 (square inch) 
	*/
	SQUARE_INCH(1030),
	
	/**
	* 平方码 (square yard) 
	*/
	SQUARE_YARD(1031),
	
	/**
	* 码 (yard/yards) 
	*/
	YARD(1032);
	
	private final int value;
	private Unit(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static Unit findByValue(int value){
		
		switch(value){
			
			case 1000: return PIECE; 
			case 1001: return PAIR; 
			case 1002: return SET; 
			case 1003: return CARTON; 
			case 1004: return DOZEN; 
			case 1005: return BARREL; 
			case 1006: return PACK; 
			case 1007: return BAG; 
			case 1008: return GRAM; 
			case 1009: return KILOGRAM; 
			case 1010: return MILLILITER; 
			case 1011: return OUNCE; 
			case 1012: return POUND; 
			case 1013: return SHORT_TON; 
			case 1014: return GALLON; 
			case 1015: return SQUARE_METER; 
			case 1016: return CUBIC_METER; 
			case 1017: return TON; 
			case 1018: return METRIC_TON; 
			case 1019: return LONG_TON; 
			case 1020: return CENTIMETER; 
			case 1021: return METER; 
			case 1022: return KILOMETER; 
			case 1023: return FEET; 
			case 1024: return INCH; 
			case 1025: return KILOLITER; 
			case 1026: return LITER; 
			case 1027: return MILLIGRAM; 
			case 1028: return MILLIMETER; 
			case 1029: return SQUARE_FEET; 
			case 1030: return SQUARE_INCH; 
			case 1031: return SQUARE_YARD; 
			case 1032: return YARD; 
			
			default: return null; 
			
		}
		
	}
	
	
}