package com.wofu.ecommerce.weipinhui;

import com.wofu.base.util.BusinessObject;

public class ReturnOrderItem extends BusinessObject {
//	private String itemID = "" ;//��Ʒ��ʶ
//	private String itemName = "" ;//��Ʒ����
//	private String itemSubhead = "" ;//��Ʒ����(�̱���)
//	private float unitPrice = 0.0f ; //�ɽ���
//	private int orderCount = 0 ; //��������
//	private String outerItemID = "" ;//sku

	private String product_name = "";	//��Ʒ����
	private String order_id = "";	//�������
	private String po_no = "";	//PO����
	private String barcode = "";	//������
	private Integer amount = 0;	//�˻���Ʒ����
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
