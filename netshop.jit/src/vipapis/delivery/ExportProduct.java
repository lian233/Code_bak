package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class ExportProduct {
	
	/**
	* 订单编码
	*/
	
	private String order_id;
	
	/**
	* PO采购单号
	*/
	
	private String po;
	
	/**
	* 商品条形码
	*/
	
	private String barcode;
	
	/**
	* 商品名称
	*/
	
	private String product_name;
	
	/**
	* 货号
	*/
	
	private String product_no;
	
	/**
	* 商品尺寸
	*/
	
	private String size;
	
	/**
	* 品牌名称
	*/
	
	private String brand_name;
	
	/**
	* 数量
	*/
	
	private Integer amount;
	
	/**
	* 商品价格
	*/
	
	private String price;
	
	/**
	* 是否礼品,G:礼品 NULL:非礼品
	*/
	
	private String is_gift;
	
	/**
	* 单位
	*/
	
	private String unit;
	
	/**
	* 专场类型
	*/
	
	private Integer is_vip;
	
	/**
	* 商品图片
	*/
	
	private String product_pic;
	
	/**
	* 创建时间
	*/
	
	private String create_time;
	
	public String getOrder_id(){
		return this.order_id;
	}
	
	public void setOrder_id(String value){
		this.order_id = value;
	}
	public String getPo(){
		return this.po;
	}
	
	public void setPo(String value){
		this.po = value;
	}
	public String getBarcode(){
		return this.barcode;
	}
	
	public void setBarcode(String value){
		this.barcode = value;
	}
	public String getProduct_name(){
		return this.product_name;
	}
	
	public void setProduct_name(String value){
		this.product_name = value;
	}
	public String getProduct_no(){
		return this.product_no;
	}
	
	public void setProduct_no(String value){
		this.product_no = value;
	}
	public String getSize(){
		return this.size;
	}
	
	public void setSize(String value){
		this.size = value;
	}
	public String getBrand_name(){
		return this.brand_name;
	}
	
	public void setBrand_name(String value){
		this.brand_name = value;
	}
	public Integer getAmount(){
		return this.amount;
	}
	
	public void setAmount(Integer value){
		this.amount = value;
	}
	public String getPrice(){
		return this.price;
	}
	
	public void setPrice(String value){
		this.price = value;
	}
	public String getIs_gift(){
		return this.is_gift;
	}
	
	public void setIs_gift(String value){
		this.is_gift = value;
	}
	public String getUnit(){
		return this.unit;
	}
	
	public void setUnit(String value){
		this.unit = value;
	}
	public Integer getIs_vip(){
		return this.is_vip;
	}
	
	public void setIs_vip(Integer value){
		this.is_vip = value;
	}
	public String getProduct_pic(){
		return this.product_pic;
	}
	
	public void setProduct_pic(String value){
		this.product_pic = value;
	}
	public String getCreate_time(){
		return this.create_time;
	}
	
	public void setCreate_time(String value){
		this.create_time = value;
	}
	
}