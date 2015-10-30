package com.wofu.ecommerce.s;

import com.wofu.base.util.BusinessObject;

public class OrderItem extends BusinessObject  //把orderDets里面的变量放进这里
{
	private int Qty;
	private String SalePrice;
	private String VendorOrderDetNo;
	private String VendorSkuId;
	private String UnitPrice;
	
	public void setQty(int Qty)
	{
		this.Qty=Qty;
	}
	
	public int getQty()
	{
		return Qty;
	}
	
	public void setSalePrice(String SalePrice)
	{
		this.SalePrice=SalePrice;
	}
	
	public String getSalePrice()
	{
		return SalePrice;
	}
	
	public void setVendorOrderDetNo(String VendorOrderDetNo)
	{
		this.VendorOrderDetNo=VendorOrderDetNo;
	}
	
	public String getVendorOrderDetNo()
	{
		return VendorOrderDetNo;
	}
	
	public void setVendorSkuId(String VendorSkuId)
	{
		this.VendorSkuId=VendorSkuId;
	}
	
	public String getVendorSkuId()
	{
		return VendorSkuId;
	}
	
	public void setUnitPrice(String UnitPrice)
	{
		this.UnitPrice=UnitPrice;
	}
	
	public String getUnitPrice()
	{
		return UnitPrice;
	}
}
