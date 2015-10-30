package com.wofu.ecommerce.jumei;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Order extends BusinessObject{

	private String order_id;
	private String shipping_system_id;
	private String payment_method;
	private double total_products_price;
	private double delivery_fee;
	private double balance_paid_amount;
	private double price_discount_amount;
	private double promo_card_discount_price;
	private double order_discount_price;
	private double red_envelope_discount_price_real;
	private double payment_amount;
	private int status;
	private String prefer_delivery_time_note;
	private long creation_time;
	private long timestamp;
	private long delivery_time;
	private long completed_time;
	private ReceiverInfo receiver_info;
	
	private DataRelation product_infos =new DataRelation("product_infos","com.wofu.ecommerce.jumei.OrderItem");

	public Order()
	{
		receiver_info=new ReceiverInfo();
	}

	public String getOrder_id() {
		return order_id;
	}


	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}


	public String getShipping_system_id() {
		return shipping_system_id;
	}


	public void setShipping_system_id(String shipping_system_id) {
		this.shipping_system_id = shipping_system_id;
	}


	public String getPayment_method() {
		return payment_method;
	}


	public void setPayment_method(String payment_method) {
		this.payment_method = payment_method;
	}


	public double getTotal_products_price() {
		return total_products_price;
	}


	public void setTotal_products_price(double total_products_price) {
		this.total_products_price = total_products_price;
	}


	public double getDelivery_fee() {
		return delivery_fee;
	}


	public void setDelivery_fee(double delivery_fee) {
		this.delivery_fee = delivery_fee;
	}


	public double getBalance_paid_amount() {
		return balance_paid_amount;
	}


	public void setBalance_paid_amount(double balance_paid_amount) {
		this.balance_paid_amount = balance_paid_amount;
	}


	public double getPrice_discount_amount() {
		return price_discount_amount;
	}


	public void setPrice_discount_amount(double price_discount_amount) {
		this.price_discount_amount = price_discount_amount;
	}


	public double getPromo_card_discount_price() {
		return promo_card_discount_price;
	}


	public void setPromo_card_discount_price(double promo_card_discount_price) {
		this.promo_card_discount_price = promo_card_discount_price;
	}


	public double getOrder_discount_price() {
		return order_discount_price;
	}


	public void setOrder_discount_price(double order_discount_price) {
		this.order_discount_price = order_discount_price;
	}


	public double getRed_envelope_discount_price_real() {
		return red_envelope_discount_price_real;
	}


	public void setRed_envelope_discount_price_real(
			double red_envelope_discount_price_real) {
		this.red_envelope_discount_price_real = red_envelope_discount_price_real;
	}


	public double getPayment_amount() {
		return payment_amount;
	}


	public void setPayment_amount(double payment_amount) {
		this.payment_amount = payment_amount;
	}


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public String getPrefer_delivery_time_note() {
		return prefer_delivery_time_note;
	}


	public void setPrefer_delivery_time_note(String prefer_delivery_time_note) {
		this.prefer_delivery_time_note = prefer_delivery_time_note;
	}


	public long getCreation_time() {
		return creation_time;
	}


	public void setCreation_time(long creation_time) {
		this.creation_time = creation_time;
	}


	public long getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}


	public long getDelivery_time() {
		return delivery_time;
	}


	public void setDelivery_time(long delivery_time) {
		this.delivery_time = delivery_time;
	}


	public long getCompleted_time() {
		return completed_time;
	}


	public void setCompleted_time(long completed_time) {
		this.completed_time = completed_time;
	}


	public DataRelation getProduct_infos() {
		return product_infos;
	}


	public void setProduct_infos(DataRelation product_infos) {
		this.product_infos = product_infos;
	}


	public ReceiverInfo getReceiver_info() {
		return receiver_info;
	}


	public void setReceiver_info(ReceiverInfo receiver_info) {
		this.receiver_info = receiver_info;
	}





	
	
	
}
