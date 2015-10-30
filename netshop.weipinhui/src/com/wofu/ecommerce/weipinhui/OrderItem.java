package com.wofu.ecommerce.weipinhui;

import com.wofu.base.util.BusinessObject;
/**
 * 
 * 订单中的商品
 *
 */
public class OrderItem extends BusinessObject{
//	private String good_sn="";//条形码
//	private String good_name="";//商品名称
//	private String good_no="";//货号
//	private String size="";//	尺码
//	private String brand_name="";//	品牌名称
//	private float price=0.0f;//市场价 
//	private int amount=0;//网购订货数量
	
	private String brand_name;	//品牌中文名称
	private String product_name;	//商品名称
	private String size;	//尺码
	private String art_no;	//货号
	private String barcode;	//条形码
	private Integer amount;	//商品数量
	private float sell_price;	//商品售卖价格
	private String order_id;	//订单号
	/**
	 * @return the brand_name
	 */
	public String getBrand_name() {
		return brand_name;
	}
	/**
	 * @param brand_name the brand_name to set
	 */
	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}
	/**
	 * @return the product_name
	 */
	public String getProduct_name() {
		return product_name;
	}
	/**
	 * @param product_name the product_name to set
	 */
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}
	/**
	 * @return the art_no
	 */
	public String getArt_no() {
		return art_no;
	}
	/**
	 * @param art_no the art_no to set
	 */
	public void setArt_no(String art_no) {
		this.art_no = art_no;
	}
	/**
	 * @return the barcode
	 */
	public String getBarcode() {
		return barcode;
	}
	/**
	 * @param barcode the barcode to set
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	/**
	 * @return the amount
	 */
	public Integer getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	/**
	 * @return the sell_price
	 */
	public float getSell_price() {
		return sell_price;
	}
	/**
	 * @param sell_price the sell_price to set
	 */
	public void setSell_price(float sell_price) {
		this.sell_price = sell_price;
	}
	/**
	 * @return the order_id
	 */
	public String getOrder_id() {
		return order_id;
	}
	/**
	 * @param order_id the order_id to set
	 */
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}


	
}
