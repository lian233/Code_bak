package com.wofu.ecommerce.meilishuo2;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * �����е���Ʒ
 *
 */
public class OrderItem extends BusinessObject{
	private String mid="";//��Ʒ��ʶ--����
	private String goods_id="";//��Ʒid
	private String goods_title="";//��Ʒ����
	private String goods_no="";//����
	private String sku="";        //�̼�sku  ͨ��v_barcodeall������
	private float price=0.0f;//�г��� 
	private int amount=0;//������������
	private String goods_img="";//��Ʒ����
	private String refund_status_text="";          //�˻�״̬
	private String deliver_status = "";//����״̬
	private String goods_code="";
	private DataRelation prop = new DataRelation("props","com.wofu.ecommerce.meilishuo2.Prop");
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}
	public String getGoods_title() {
		return goods_title;
	}
	public void setGoods_title(String goods_title) {
		this.goods_title = goods_title;
	}
	public String getGoods_no() {
		return goods_no;
	}
	public void setGoods_no(String goods_no) {
		this.goods_no = goods_no;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getGoods_img() {
		return goods_img;
	}
	public void setGoods_img(String goods_img) {
		this.goods_img = goods_img;
	}
	public String getRefund_status_text() {
		return refund_status_text;
	}
	public void setRefund_status_text(String refund_status_text) {
		this.refund_status_text = refund_status_text;
	}
	public String getDeliver_status() {
		return deliver_status;
	}
	public void setDeliver_status(String deliver_status) {
		this.deliver_status = deliver_status;
	}
	public String getGoods_code() {
		return goods_code;
	}
	public void setGoods_code(String goods_code) {
		this.goods_code = goods_code;
	}
	public DataRelation getProp() {
		return prop;
	}
	public void setProp(DataRelation props) {
		this.prop = props;
	}
	
	

	
	
	
	
	
}
