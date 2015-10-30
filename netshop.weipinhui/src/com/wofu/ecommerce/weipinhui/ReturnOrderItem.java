package com.wofu.ecommerce.weipinhui;

import com.wofu.base.util.BusinessObject;

public class ReturnOrderItem extends BusinessObject {
//	private String itemID = "" ;//商品标识
//	private String itemName = "" ;//商品名称
//	private String itemSubhead = "" ;//商品名称(短标题)
//	private float unitPrice = 0.0f ; //成交价
//	private int orderCount = 0 ; //购买数量
//	private String outerItemID = "" ;//sku

	private String product_name = "";	//商品名称
	private String order_id = "";	//订单编号
	private String po_no = "";	//PO单号
	private String barcode = "";	//条形码
	private Integer amount = 0;	//退货商品数量
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
	/**
	 * @return the po_no
	 */
	public String getPo_no() {
		return po_no;
	}
	/**
	 * @param po_no the po_no to set
	 */
	public void setPo_no(String po_no) {
		this.po_no = po_no;
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
	
	
}
