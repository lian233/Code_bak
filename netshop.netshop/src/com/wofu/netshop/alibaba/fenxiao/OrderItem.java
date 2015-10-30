package com.wofu.netshop.alibaba.fenxiao;

import com.wofu.base.util.BusinessObject;

public class OrderItem extends BusinessObject {
	private long id;
	private long sourceId;   //��Ʒ��Ϣ����-��ƷID(��������ҹ������Ʒ��Ϣ��������ƷID��ͼƬURL�����ơ����ۡ�������������ͬ)
	private String productPic;   //��Ʒ��Ϣ����-��Ʒ����ͼƬ��URL��ַ
	private String productName;   //��Ʒ��Ϣ����-��Ʒ����
	private double price;          //��Ʒ��Ϣ����-��Ʒ���ۣ���λ����
	private double quantity;       //��Ʒ��Ϣ����-�����и���Ʒ�Ĺ�������
	
	private String specId;
	private String sku;
	private String specInfo;           //������Ϣ
	private String specName;             //��������
	private String specValue;            //����ֵ
	private String specUnit;   		     //���Ե�λ
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
