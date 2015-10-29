package com.wofu.fire.deliveryservice;

public class HscodeInfo {
	private String order_id;//订单号
	private String customs_barcode;//海关条码 
	private String delivery;//快递公司
	private String delivery_id;//快递单号
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getCustoms_barcode() {
		return customs_barcode;
	}
	public void setCustoms_barcode(String customs_barcode) {
		this.customs_barcode = customs_barcode;
	}
	public String getDelivery() {
		return delivery;
	}
	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}
	public String getDelivery_id() {
		return delivery_id;
	}
	public void setDelivery_id(String delivery_id) {
		this.delivery_id = delivery_id;
	}
	
	
}
