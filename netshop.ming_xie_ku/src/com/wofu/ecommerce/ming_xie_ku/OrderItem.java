package com.wofu.ecommerce.ming_xie_ku;

import com.wofu.base.util.BusinessObject;

public class OrderItem extends BusinessObject  //把orderDets里面的变量放进这里
{
	private int Qty;
	private String VendorOrderDetNo;
	private String VendorSkuId;
	private float UnitPrice;
	public int getQty() {
		return Qty;
	}
	public void setQty(int qty) {
		Qty = qty;
	}
	public String getVendorOrderDetNo() {
		return VendorOrderDetNo;
	}
	public void setVendorOrderDetNo(String vendorOrderDetNo) {
		VendorOrderDetNo = vendorOrderDetNo;
	}
	public String getVendorSkuId() {
		return VendorSkuId;
	}
	public void setVendorSkuId(String vendorSkuId) {
		VendorSkuId = vendorSkuId;
	}
	public float getUnitPrice() {
		return UnitPrice;
	}
	public void setUnitPrice(float unitPrice) {
		UnitPrice = unitPrice;
	}
	
	
	
	
	
}
