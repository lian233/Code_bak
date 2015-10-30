package com.wofu.ecommerce.meilishuo;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * 订单中的商品
 *
 */
public class OrderItem extends BusinessObject{
	private String mid="";//商品标识--自增
	private String goods_id="";//商品id
	private String goods_title="";//商品名称
	private String goods_no="";//货号
	private String sku="";        //商家sku  通过v_barcodeall表查出来
	private float price=0.0f;//市场价 
	private int amount=0;//网购订货数量
	private String goods_img="";//商品链接
	private String refund_status_text="";          //退货状态
	private String deliver_status = "";//发货状态
	private String goods_code="";
	private DataRelation prop = new DataRelation("props","com.wofu.ecommerce.meilishuo.Prop");
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
