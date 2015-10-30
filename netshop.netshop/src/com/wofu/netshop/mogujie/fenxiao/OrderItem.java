package com.wofu.netshop.mogujie.fenxiao;
import com.wofu.base.util.BusinessObject;
public class OrderItem extends BusinessObject {
	private long oid;
	private long iid;
	private String iurl;
	private String title;
	private String image;
	private long sku_id;
	private String sku_bn;//sku编码
	private int items_num;
	private float total_order_fee;//订单总金额
	private float discount_fee;//优惠金额
	private float sale_price;//子订单销售金额
	private String 	sku_properties;
	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}
	public long getIid() {
		return iid;
	}
	public void setIid(long iid) {
		this.iid = iid;
	}
	public String getIurl() {
		return iurl;
	}
	public void setIurl(String iurl) {
		this.iurl = iurl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public long getSku_id() {
		return sku_id;
	}
	public void setSku_id(long sku_id) {
		this.sku_id = sku_id;
	}
	public String getSku_bn() {
		return sku_bn;
	}
	public void setSku_bn(String sku_bn) {
		this.sku_bn = sku_bn;
	}
	public int getItems_num() {
		return items_num;
	}
	public void setItems_num(int items_num) {
		this.items_num = items_num;
	}
	public float getTotal_order_fee() {
		return total_order_fee;
	}
	public void setTotal_order_fee(float total_order_fee) {
		this.total_order_fee = total_order_fee;
	}
	public float getDiscount_fee() {
		return discount_fee;
	}
	public void setDiscount_fee(float discount_fee) {
		this.discount_fee = discount_fee;
	}
	public float getSale_price() {
		return sale_price;
	}
	public void setSale_price(float sale_price) {
		this.sale_price = sale_price;
	}
	public String getSku_properties() {
		return sku_properties;
	}
	public void setSku_properties(String sku_properties) {
		this.sku_properties = sku_properties;
	}
	
	
	
	
	
}
