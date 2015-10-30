package com.wofu.ecommerce.taobao;

/**
 * 经销订单明细
 */
import com.wofu.base.util.BusinessObject;

public class Dealer_order_detail extends BusinessObject{
	private long dealer_detail_id;
	private long dealer_order_id;
	private String final_price;
	private boolean is_deleted;
	private String original_price;
	private String price_count;
	private long product_id;
	private String product_title;
	private int quantity;
	private long sku_id;
	private String sku_number;
	private String sku_spec;
	private String snapshot_url;
	public long getDealer_detail_id() {
		return dealer_detail_id;
	}
	public void setDealer_detail_id(long dealer_detail_id) {
		this.dealer_detail_id = dealer_detail_id;
	}
	public long getDealer_order_id() {
		return dealer_order_id;
	}
	public void setDealer_order_id(long dealer_order_id) {
		this.dealer_order_id = dealer_order_id;
	}
	public String getFinal_price() {
		return final_price;
	}
	public void setFinal_price(String final_price) {
		this.final_price = final_price;
	}
	public boolean getIs_deleted() {
		return is_deleted;
	}
	public void setIs_deleted(boolean is_deleted) {
		this.is_deleted = is_deleted;
	}
	public String getOriginal_price() {
		return original_price;
	}
	public void setOriginal_price(String original_price) {
		this.original_price = original_price;
	}
	public String getPrice_count() {
		return price_count;
	}
	public void setPrice_count(String price_count) {
		this.price_count = price_count;
	}
	public long getProduct_id() {
		return product_id;
	}
	public void setProduct_id(long product_id) {
		this.product_id = product_id;
	}
	public String getProduct_title() {
		return product_title;
	}
	public void setProduct_title(String product_title) {
		this.product_title = product_title;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public long getSku_id() {
		return sku_id;
	}
	public void setSku_id(long sku_id) {
		this.sku_id = sku_id;
	}
	public String getSku_number() {
		return sku_number;
	}
	public void setSku_number(String sku_number) {
		this.sku_number = sku_number;
	}
	public String getSku_spec() {
		return sku_spec;
	}
	public void setSku_spec(String sku_spec) {
		this.sku_spec = sku_spec;
	}
	public String getSnapshot_url() {
		return snapshot_url;
	}
	public void setSnapshot_url(String snapshot_url) {
		this.snapshot_url = snapshot_url;
	}

}
