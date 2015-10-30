package com.wofu.ecommerce.yz;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
public class OrderItem extends BusinessObject {
	private int num;//商品数量
	private String num_iid;//商品数字编号
	private String sku_id;//Sku的ID
	private String outer_sku_id;//商家编码
	private String outer_item_id;//商品货号
	private String title;//商品标题
	private String seller_nick;//卖家昵称
	private float price;//商品价格
	private float total_fee;//应付金额
	private float discount_fee;//商品标题
	private float payment;//商品标题
	private String sku_properties_name;//商品标题
	private DataRelation buyer_messages = new DataRelation("DataRelation","com.wofu.ecommerce.yz.Messages");
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getNum_iid() {
		return num_iid;
	}
	public void setNum_iid(String num_iid) {
		this.num_iid = num_iid;
	}
	public String getSku_id() {
		return sku_id;
	}
	public void setSku_id(String sku_id) {
		this.sku_id = sku_id;
	}
	public String getOuter_sku_id() {
		return outer_sku_id;
	}
	public void setOuter_sku_id(String outer_sku_id) {
		this.outer_sku_id = outer_sku_id;
	}
	public String getOuter_item_id() {
		return outer_item_id;
	}
	public void setOuter_item_id(String outer_item_id) {
		this.outer_item_id = outer_item_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSeller_nick() {
		return seller_nick;
	}
	public void setSeller_nick(String seller_nick) {
		this.seller_nick = seller_nick;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(float total_fee) {
		this.total_fee = total_fee;
	}
	public float getDiscount_fee() {
		return discount_fee;
	}
	public void setDiscount_fee(float discount_fee) {
		this.discount_fee = discount_fee;
	}
	public float getPayment() {
		return payment;
	}
	public void setPayment(float payment) {
		this.payment = payment;
	}
	public String getSku_properties_name() {
		return sku_properties_name;
	}
	public void setSku_properties_name(String sku_properties_name) {
		this.sku_properties_name = sku_properties_name;
	}
	public DataRelation getBuyer_messages() {
		return buyer_messages;
	}
	public void setBuyer_messages(DataRelation buyer_messages) {
		this.buyer_messages = buyer_messages;
	}
	
	
	
	
	
	
}
