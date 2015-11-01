package com.wofu.ecommerce.huasheng;

import com.wofu.base.util.BusinessObject;
/**
 * 
 * 订单中的商品
 *
 */
public class OrderItem extends BusinessObject{
	private String sku;			//商品编码
	private String mid;			//商品网店内部编码
	private String title;		//商品标题
	private Integer num;		//数量
	private double price;		//成交价格
	private double goodsprice;	//产品价格
	private String prop;		//属性
	/**
	 * @return the sku
	 */
	public String getSku() {
		return sku;
	}
	/**
	 * @param sku the sku to set
	 */
	public void setSku(String sku) {
		this.sku = sku;
	}
	/**
	 * @return the mid
	 */
	public String getMid() {
		return mid;
	}
	/**
	 * @param mid the mid to set
	 */
	public void setMid(String mid) {
		this.mid = mid;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the num
	 */
	public Integer getNum() {
		return num;
	}
	/**
	 * @param num the num to set
	 */
	public void setNum(Integer num) {
		this.num = num;
	}
	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	/**
	 * @return the goodsprice
	 */
	public double getGoodsprice() {
		return goodsprice;
	}
	/**
	 * @param goodsprice the goodsprice to set
	 */
	public void setGoodsprice(double goodsprice) {
		this.goodsprice = goodsprice;
	}
	/**
	 * @return the prop
	 */
	public String getProp() {
		return prop;
	}
	/**
	 * @param prop the prop to set
	 */
	public void setProp(String prop) {
		this.prop = prop;
	}
}
