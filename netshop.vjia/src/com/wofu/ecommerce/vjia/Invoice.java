package com.wofu.ecommerce.vjia;

public class Invoice
{
	private String orderID ; //������
	private String invoicetitle ;//<!--��Ʊ̧ͷ-->
	private String name ;//<!--����-->
	private String unit ;// <!--��λ-->
	private int qty ;//<!--����-->
	private float unitprice;//<!--����-->
	private float price ;//<!--���-->
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
