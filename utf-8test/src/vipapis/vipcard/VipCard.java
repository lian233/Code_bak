package vipapis.vipcard;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class VipCard {
	
	/**
	* 唯品卡卡号
	*/
	
	private String card_code;
	
	/**
	* 金额
	*/
	
	private double money;
	
	/**
	* 状态,0:正常；1：已售出；
	*/
	
	private int state;
	
	public String getCard_code(){
		return this.card_code;
	}
	
	public void setCard_code(String value){
		this.card_code = value;
	}
	public double getMoney(){
		return this.money;
	}
	
	public void setMoney(double value){
		this.money = value;
	}
	public int getState(){
		return this.state;
	}
	
	public void setState(int value){
		this.state = value;
	}
	
}