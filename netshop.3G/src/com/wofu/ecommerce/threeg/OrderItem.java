package com.wofu.ecommerce.threeg;

public class OrderItem {
	
	private String ProductName;
	private String ExternalId;
	private String Count;
	private String ProductPrice;
	
	public String getCount() {
		return Count;
	}
	public void setCount(String count) {
		Count = count;
	}
	public String getExternalId() {
		return ExternalId;
	}
	public void setExternalId(String externalId) {
		ExternalId = externalId;
	}
	public String getProductName() {
		return ProductName;
	}
	public void setProductName(String productName) {
		ProductName = productName;
	}
	public String getProductPrice() {
		return ProductPrice;
	}
	public void setProductPrice(String productPrice) {
		ProductPrice = productPrice;
	}

}
