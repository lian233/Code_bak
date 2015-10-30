package com.wofu.ecommerce.papago8;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * 订单中的商品
 *
 */
public class OrderItem extends BusinessObject{
	private String oid="";//商品标识--自增
	private String title="";//商品名称
	private String prono="";//货号
	private String outer_sku_id="";        //商家sku  通过v_barcodeall表查出来
	private float price=0.0f;//市场价 
	private int num=0;//网购订货数量
	private String pic_path="";//商品链接
	private String procolor="";
	private String prosize="";
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getProno() {
		return prono;
	}
	public void setProno(String prono) {
		this.prono = prono;
	}
	public String getOuter_sku_id() {
		return outer_sku_id;
	}
	public void setOuter_sku_id(String outer_sku_id) {
		this.outer_sku_id = outer_sku_id;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getPic_path() {
		return pic_path;
	}
	public void setPic_path(String pic_path) {
		this.pic_path = pic_path;
	}
	public String getProcolor() {
		return procolor;
	}
	public void setProcolor(String procolor) {
		this.procolor = procolor;
	}
	public String getProsize() {
		return prosize;
	}
	public void setProsize(String prosize) {
		this.prosize = prosize;
	}
	
	
}
