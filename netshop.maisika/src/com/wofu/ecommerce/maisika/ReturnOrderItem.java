package com.wofu.ecommerce.maisika;
import com.wofu.base.util.BusinessObject;

public class ReturnOrderItem extends BusinessObject {
	private String goods_serial = "" ;//商品编码 sku
	private String goods_id = "" ;//商品网店内部编码  货号 
	private String rec_id = "" ;//退货ID
	private String goods_name = "" ;//商品标题
	private int goods_num;//数量
	private float refund_amount;//退款价格
	private float goods_price;
	private String goods_spec = "" ;//属性	
			
	public float getGoods_price() {
		return goods_price;
	}
	public void setGoods_price(float goods_price) {
		this.goods_price = goods_price;
	}
	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}
	
	public String getRec_id() {
		return rec_id;
	}
	public void setRec_id(String rec_id) {
		this.rec_id = rec_id;
	}
	
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	
	public int getGoods_num() {
		return goods_num;
	}
	public void setGoods_num(int goods_num) {
		this.goods_num = goods_num;
	}
	
	public float getRefund_amount() {
		return refund_amount;
	}
	public void setRefund_amount(float refund_amount) {
		this.refund_amount = refund_amount;
	}
	public void setGoods_spec(String goods_spec) {
		this.goods_spec = goods_spec;
	}
	public String getGoods_spec() {
		return goods_spec;
	}
	public void setGoods_serial(String goods_serial) {
		this.goods_serial = goods_serial;
	}
	public String getGoods_serial() {
		return goods_serial;
	}
	
	
}
