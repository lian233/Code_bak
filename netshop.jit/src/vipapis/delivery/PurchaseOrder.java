package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;

import com.wofu.base.util.BusinessObject;



public  class PurchaseOrder extends BusinessObject{
	
	/**
	* 
	*/
	
	private String po_no;
	
	/**
	* 
	*/
	
	private String co_mode;
	
	/**
	* 
	* @sampleValue sell_st_time 2014-07-01 10:00:00
	*/
	
	private String sell_st_time;
	
	/**
	* 
	* @sampleValue sell_et_time 2014-08-15 23:59:59
	*/
	
	private String sell_et_time;
	
	/**
	* 
	*/
	
	private String stock;
	
	/**
	* 
	*/
	
	private String sales_volume;
	
	/**
	* 
	*/
	
	private String not_pick;
	
	public String getPo_no(){
		return this.po_no;
	}
	
	public void setPo_no(String value){
		this.po_no = value;
	}
	public String getCo_mode(){
		return this.co_mode;
	}
	
	public void setCo_mode(String value){
		this.co_mode = value;
	}
	public String getSell_st_time(){
		return this.sell_st_time;
	}
	
	public void setSell_st_time(String value){
		this.sell_st_time = value;
	}
	public String getSell_et_time(){
		return this.sell_et_time;
	}
	
	public void setSell_et_time(String value){
		this.sell_et_time = value;
	}
	public String getStock(){
		return this.stock;
	}
	
	public void setStock(String value){
		this.stock = value;
	}
	public String getSales_volume(){
		return this.sales_volume;
	}
	
	public void setSales_volume(String value){
		this.sales_volume = value;
	}
	public String getNot_pick(){
		return this.not_pick;
	}
	
	public void setNot_pick(String value){
		this.not_pick = value;
	}
	
}