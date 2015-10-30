package com.wofu.netshop.alibaba.fenxiao;

import com.wofu.base.util.BusinessObject;

public class OrderItem extends BusinessObject {
	private long id;
	private long sourceId;   //商品信息数组-商品ID(订单中买家购买的商品信息，包括商品ID，图片URL、名称、单价、购买数量，下同)
	private String productPic;   //商品信息数组-商品所有图片的URL地址
	private String productName;   //商品信息数组-商品名称
	private double price;          //商品信息数组-商品单价，单位：分
	private double quantity;       //商品信息数组-订单中该商品的购买数量
	
	private String specId;
	private String sku;
	private String specInfo;           //属性信息
	private String specName;             //属性名称
	private String specValue;            //属性值
	private String specUnit;   		     //属性单位
	private String entryStatus;
	private String entryCodStatus;
	private double entryDiscount;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getSourceId() {
		return sourceId;
	}
	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
	public String getProductPic() {
		return productPic;
	}
	public void setProductPic(String productPic) {
		this.productPic = productPic;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public String getSpecId() {
		return specId;
	}
	public void setSpecId(String specId) {
		this.specId = specId;
	}
	public String getSpecInfo() {
		return specInfo;
	}
	public void setSpecInfo(String specInfo) {
		this.specInfo = specInfo;
	}
	public String getSpecName() {
		return specName;
	}
	public void setSpecName(String specName) {
		this.specName = specName;
	}
	public String getSpecValue() {
		return specValue;
	}
	public void setSpecValue(String specValue) {
		this.specValue = specValue;
	}
	public String getSpecUnit() {
		return specUnit;
	}
	public void setSpecUnit(String specUnit) {
		this.specUnit = specUnit;
	}
	
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getEntryStatus() {
		return entryStatus;
	}
	public void setEntryStatus(String entryStatus) {
		this.entryStatus = entryStatus;
	}
	public String getEntryCodStatus() {
		return entryCodStatus;
	}
	public void setEntryCodStatus(String entryCodStatus) {
		this.entryCodStatus = entryCodStatus;
	}
	public double getEntryDiscount() {
		return entryDiscount;
	}
	public void setEntryDiscount(double entryDiscount) {
		this.entryDiscount = entryDiscount;
	}
	
	
	
	
	
}
