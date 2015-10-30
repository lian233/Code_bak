package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class PurchaseOrder {
	
	/**
	* PO编号
	*/
	
	private String po_no;
	
	/**
	* 合作模式编码
	*/
	
	private String co_mode;
	
	/**
	* 档期开始销售时间(格式yyyy-MM-dd HH:mm:ss)
	* @sampleValue sell_st_time 2014-07-01 10:00:00
	*/
	
	private String sell_st_time;
	
	/**
	* 档期结束销售时间(格式yyyy-MM-dd HH:mm:ss)
	* @sampleValue sell_et_time 2014-08-15 23:59:59
	*/
	
	private String sell_et_time;
	
	/**
	* 虚拟总库存
	*/
	
	private int stock;
	
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
	public int getStock(){
		return this.stock;
	}
	
	public void setStock(int value){
		this.stock = value;
	}
	
}