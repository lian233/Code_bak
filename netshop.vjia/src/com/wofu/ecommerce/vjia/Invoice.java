package com.wofu.ecommerce.vjia;

public class Invoice
{
	private String orderID ; //订单号
	private String invoicetitle ;//<!--发票抬头-->
	private String name ;//<!--名称-->
	private String unit ;// <!--单位-->
	private int qty ;//<!--数量-->
	private float unitprice;//<!--单价-->
	private float price ;//<!--金额-->
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public float getPrice()
	{
		return price;
	}
	public void setPrice(float price)
	{
		this.price = price;
	}
	public int getQty()
	{
		return qty;
	}
	public void setQty(int qty)
	{
		this.qty = qty;
	}
	public String getUnit()
	{
		return unit;
	}
	public void setUnit(String unit)
	{
		this.unit = unit;
	}
	public float getUnitprice()
	{
		return unitprice;
	}
	public void setUnitprice(float unitprice)
	{
		this.unitprice = unitprice;
	}
	public String getInvoicetitle() {
		return invoicetitle;
	}
	public void setInvoicetitle(String invoicetitle) {
		this.invoicetitle = invoicetitle;
	}
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

}
