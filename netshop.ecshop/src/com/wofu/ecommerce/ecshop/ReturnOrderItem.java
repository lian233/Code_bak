package com.wofu.ecommerce.ecshop;

import com.wofu.base.util.BusinessObject;

/**
 * 
 * �����е���Ʒ
 *
 */
public class ReturnOrderItem extends BusinessObject{
	private String itemCode="";//��Ʒ��
	private String sellerItemCode="";//sellerItemCode
	private String itemTitle="";//��Ʒ����
	private String orderQty="";//��������
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getSellerItemCode() {
		return sellerItemCode;
	}
	public void setSellerItemCode(String sellerItemCode) {
		this.sellerItemCode = sellerItemCode;
	}
	public String getItemTitle() {
		return itemTitle;
	}
	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}
	public String getOrderQty() {
		return orderQty;
	}
	public void setOrderQty(String orderQty) {
		this.orderQty = orderQty;
	}

}
