package vipapis.common;



public enum OrderStatus {
	
	
	/**
	* 未支付订单
	*/
	STATUS_0(0),
	
	/**
	* 待审核订单（已支付/未处理）
	*/
	STATUS_1(1),
	
	/**
	* 订单已审核（已处理）
	*/
	STATUS_10(10),
	
	/**
	* 未处理
	*/
	STATUS_11(11),
	
	/**
	* 商品调拨中
	*/
	STATUS_12(12),
	
	/**
	* 缺货
	*/
	STATUS_13(13),
	
	/**
	* 订单发货失败
	*/
	STATUS_14(14),
	
	/**
	* 拣货中
	*/
	STATUS_20(20),
	
	/**
	* 已打包
	*/
	STATUS_21(21),
	
	/**
	* 已发货
	*/
	STATUS_22(22),
	
	/**
	* 售后处理
	*/
	STATUS_23(23),
	
	/**
	* 未处理
	*/
	STATUS_24(24),
	
	/**
	* 已签收
	*/
	STATUS_25(25),
	
	/**
	* 订单重发
	*/
	STATUS_28(28),
	
	/**
	* 未处理
	*/
	STATUS_30(30),
	
	/**
	* 未处理
	*/
	STATUS_31(31),
	
	/**
	* 货品回寄中
	*/
	STATUS_40(40),
	
	/**
	* 退换货服务不受理
	*/
	STATUS_41(41),
	
	/**
	* 无效换货
	*/
	STATUS_42(42),
	
	/**
	* 已发货
	*/
	STATUS_44(44),
	
	/**
	* 退款处理中
	*/
	STATUS_45(45),
	
	/**
	* 退换货未处理
	*/
	STATUS_46(46),
	
	/**
	* 修改退款资料
	*/
	STATUS_47(47),
	
	/**
	* 无效退货
	*/
	STATUS_48(48),
	
	/**
	* 已退款
	*/
	STATUS_49(49),
	
	/**
	* 退货异常处理中
	*/
	STATUS_51(51),
	
	/**
	* 退款异常处理中
	*/
	STATUS_52(52),
	
	/**
	* 退货未审核
	*/
	STATUS_53(53),
	
	/**
	* 退货已审核
	*/
	STATUS_54(54),
	
	/**
	* 拒收回访
	*/
	STATUS_55(55),
	
	/**
	* 售后异常
	*/
	STATUS_56(56),
	
	/**
	* 上门取件
	*/
	STATUS_57(57),
	
	/**
	* 退货已返仓
	*/
	STATUS_58(58),
	
	/**
	* 已退货
	*/
	STATUS_59(59),
	
	/**
	* 已完成
	*/
	STATUS_60(60),
	
	/**
	* 已换货
	*/
	STATUS_61(61),
	
	/**
	* 用户已拒收
	*/
	STATUS_70(70),
	
	/**
	* 超区返仓中
	*/
	STATUS_71(71),
	
	/**
	* 拒收返仓中
	*/
	STATUS_72(72),
	
	/**
	* 订单已修改
	*/
	STATUS_96(96),
	
	/**
	* 订单已取消
	*/
	STATUS_97(97),
	
	/**
	* 已合并
	*/
	STATUS_98(98),
	
	/**
	* 已删除
	*/
	STATUS_99(99),
	
	/**
	* 退货失败
	*/
	STATUS_100(100);
	
	private final int value;
	private OrderStatus(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	
	public static OrderStatus findByValue(int value){
		
		switch(value){
			
			case 0: return STATUS_0; 
			case 1: return STATUS_1; 
			case 10: return STATUS_10; 
			case 11: return STATUS_11; 
			case 12: return STATUS_12; 
			case 13: return STATUS_13; 
			case 14: return STATUS_14; 
			case 20: return STATUS_20; 
			case 21: return STATUS_21; 
			case 22: return STATUS_22; 
			case 23: return STATUS_23; 
			case 24: return STATUS_24; 
			case 25: return STATUS_25; 
			case 28: return STATUS_28; 
			case 30: return STATUS_30; 
			case 31: return STATUS_31; 
			case 40: return STATUS_40; 
			case 41: return STATUS_41; 
			case 42: return STATUS_42; 
			case 44: return STATUS_44; 
			case 45: return STATUS_45; 
			case 46: return STATUS_46; 
			case 47: return STATUS_47; 
			case 48: return STATUS_48; 
			case 49: return STATUS_49; 
			case 51: return STATUS_51; 
			case 52: return STATUS_52; 
			case 53: return STATUS_53; 
			case 54: return STATUS_54; 
			case 55: return STATUS_55; 
			case 56: return STATUS_56; 
			case 57: return STATUS_57; 
			case 58: return STATUS_58; 
			case 59: return STATUS_59; 
			case 60: return STATUS_60; 
			case 61: return STATUS_61; 
			case 70: return STATUS_70; 
			case 71: return STATUS_71; 
			case 72: return STATUS_72; 
			case 96: return STATUS_96; 
			case 97: return STATUS_97; 
			case 98: return STATUS_98; 
			case 99: return STATUS_99; 
			case 100: return STATUS_100; 
			
			default: return null; 
			
		}
		
	}
	
	
}