package com.wofu.ecommerce.vjia;

public class ProductInfo {
	private String barcode="";//商品条码 内部sku
	private String sku="";//V+商品编码
	private String developid="";//款号
	private String productname="";//商品名称
	private String color="";//颜色
	private String size="";//尺寸
	private int fororder=0;//当前预售库存
	private String onsale="";//在架状态：true上架，false下架
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getDevelopid() {
		return developid;
	}
	public void setDevelopid(String developid) {
		this.developid = developid;
	}
	public int getFororder() {
		return fororder;
	}
	public void setFororder(int fororder) {
		this.fororder = fororder;
	}
	public String getOnsale() {
		return onsale;
	}
	public void setOnsale(String onsale) {
		this.onsale = onsale;
	}
	public String getProductname() {
		return productname;
	}
	public void setProductname(String productname) {
		this.productname = productname;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}

}
