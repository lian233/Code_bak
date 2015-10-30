package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class PurchaseOrderSku {
	
	/**
	* 售卖站点
	*/
	
	private String sell_site;
	
	/**
	* 送货仓库
	*/
	
	private String warehouse;
	
	/**
	* 商品条码
	*/
	
	private String barcode;
	
	/**
	* 商品售卖数量
	*/
	
	private Integer amount;
	
	/**
	* 订单类别（ single：单品 multiple：多品）
	*/
	
	private String order_cate;
	
	/**
	* 订单状态
	*/
	
	private String order_status;
	
	/**
	* 下单时间
	*/
	
	private String create_time;
	
	/**
	* 订单审核时间
	*/
	
	private String audit_time;
	
	public String getSell_site(){
		return this.sell_site;
	}
	
	public void setSell_site(String value){
		this.sell_site = value;
	}
	public String getWarehouse(){
		return this.warehouse;
	}
	
	public void setWarehouse(String value){
		this.warehouse = value;
	}
	public String getBarcode(){
		return this.barcode;
	}
	
	public void setBarcode(String value){
		this.barcode = value;
	}
	public Integer getAmount(){
		return this.amount;
	}
	
	public void setAmount(Integer value){
		this.amount = value;
	}
	public String getOrder_cate(){
		return this.order_cate;
	}
	
	public void setOrder_cate(String value){
		this.order_cate = value;
	}
	public String getOrder_status(){
		return this.order_status;
	}
	
	public void setOrder_status(String value){
		this.order_status = value;
	}
	public String getCreate_time(){
		return this.create_time;
	}
	
	public void setCreate_time(String value){
		this.create_time = value;
	}
	public String getAudit_time(){
		return this.audit_time;
	}
	
	public void setAudit_time(String value){
		this.audit_time = value;
	}
	
}