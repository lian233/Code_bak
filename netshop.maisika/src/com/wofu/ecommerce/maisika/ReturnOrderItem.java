package com.wofu.ecommerce.maisika;
import com.wofu.base.util.BusinessObject;

public class ReturnOrderItem extends BusinessObject {
	private String goods_serial = "" ;//��Ʒ���� sku
	private String goods_id = "" ;//��Ʒ�����ڲ�����  ���� 
	private String rec_id = "" ;//�˻�ID
	private String goods_name = "" ;//��Ʒ����
	private int goods_num;//����
	private float refund_amount;//�˿�۸�
	private float goods_price;
	private String goods_spec = "" ;//����	
			
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
