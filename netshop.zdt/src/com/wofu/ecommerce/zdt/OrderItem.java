package com.wofu.ecommerce.zdt;

import java.util.Date;

import com.wofu.base.util.BusinessObject;

public class OrderItem extends BusinessObject {
	private int num;//��Ʒ����
	private String title;//��Ʒ����
	private int order_item_id;//�Ӷ������
	private float discount_fee; //�Żݽ��
	private float price; //��Ʒ�۸�'
	private float payment;//ʵ��֧����� 
	private String status;//״̬
	private float sku_properties_name;//sku����
	private String outer_sku_id;//skuid
	private float order_no;//������
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getOrder_item_id() {
		return order_item_id;
	}
	public void setOrder_item_id(int order_item_id) {
		this.order_item_id = order_item_id;
	}
	public float getDiscount_fee() {
		return discount_fee;
	}
	public void setDiscount_fee(float discount_fee) {
		this.discount_fee = discount_fee;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getPayment() {
		return payment;
	}
	public void setPayment(float payment) {
		this.payment = payment;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public float getSku_properties_name() {
		return sku_properties_name;
	}
	public void setSku_properties_name(float sku_properties_name) {
		this.sku_properties_name = sku_properties_name;
	}
	public String getOuter_sku_id() {
		return outer_sku_id;
	}
	public void setOuter_sku_id(String outer_sku_id) {
		this.outer_sku_id = outer_sku_id;
	}
	public float getOrder_no() {
		return order_no;
	}
	public void setOrder_no(float order_no) {
		this.order_no = order_no;
	}
	
	
	
	
}
