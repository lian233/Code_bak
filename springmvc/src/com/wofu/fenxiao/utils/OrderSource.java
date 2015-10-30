package com.wofu.fenxiao.utils;
/**
 * 订单来源枚举
 * @author Administrator
 *
 */
public enum OrderSource {
	TM(1),//天猫
	JD(2),//京东
	MGJ(3),//蘑菇街
	MLS(4),
	DD(5),
	OTHERS(100);//其它
	private final int value;
	
	private OrderSource(int value){
		this.value=value;
	}
	
	public int getValue(){return value;}
	
	public static OrderSource findByValue(int value){
		switch(value){
		case 1: return TM;
		case 2: return JD;
		case 3: return MGJ;
		case 4: return MLS;
		case 5: return DD;
		default: return OTHERS;
			
		}
	}
	
}
