package com.wofu.ecommerce.vjia;

public class CopyOfProductInfo {
	private String barcode="";//��Ʒ���� �ڲ�sku
	private String sku="";//V+��Ʒ����
	private String developid="";//���
	private String productname="";//��Ʒ����
	private String color="";//��ɫ
	private String size="";//�ߴ�
	private int fororder=0;//��ǰԤ�ۿ��
	private String onsale="";//�ڼ�״̬��true�ϼܣ�false�¼�
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
