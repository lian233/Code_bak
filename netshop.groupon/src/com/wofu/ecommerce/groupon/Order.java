package com.wofu.ecommerce.groupon;

public class Order {

	private String orderid;
	private String money;
	private String creattime;
	private String paymenttime;
	private String postage;
	private String buynum;
	private String status;
	private String receivername;
	private String buyernick;
	private String email;
	private String mobilephone;
	private String province;
	private String city;
	private String address;
	private String zipcode;
	private String remarks;
	private String sku;
	
	public Order()
	{
		
	}
	public void setbuyerNick(String buyernick)
	{
		this.buyernick=buyernick;
	}
	public String getbuyerNick()
	{
		return this.buyernick;
	}
	
	public void setZipCode(String zipcode)
	{
		this.zipcode=zipcode;
	}
	public String getZipCode()
	{
		return this.zipcode;
	}
	
	public void setOrderId(String orderid)
	{
		this.orderid=orderid;
	}
	public String getOrderId()
	{
		return orderid;
	}
	
	public void setMoney(String money)
	{
		this.money=money;
	}
	public String getMoney()
	{
		return money;
	}	
	
	public void setCreatTime(String creattime)
	{
		this.creattime=creattime;
	}
	public String getCreatTime()
	{
		return creattime;
	}	
	public void setPaymentTime(String paymenttime)
	{
		this.paymenttime=paymenttime;
	}
	public String getPaymentTime()
	{
		return paymenttime;
	}	
	public void setPostage(String postage)
	{
		this.postage=postage;
	}
	public String getPostage()
	{
		return postage;
	}	
	public void setBuyNum(String buynum)
	{
		this.buynum=buynum;
	}
	public String getBuyNum()
	{
		return buynum;
	}		
	public void setStatus(String status)
	{
		this.status=status;
	}
	public String getStatus()
	{
		return status;
	}
	public void setReceiverName(String receivername)
	{
		this.receivername=receivername;
	}
	public String getReceiverName()
	{
		return receivername;
	}	
	public void setEmail(String email)
	{
		this.email=email;
	}
	public String getEmail()
	{
		return email;
	}		
	public void setMobilePhone(String mobilephone)
	{
		this.mobilephone=mobilephone;
	}
	public String getMobilePhone()
	{
		return mobilephone;
	}	
	public void setProvince(String province)
	{
		this.province=province;
	}
	public String getProvince()
	{
		return province;
	}	
	public void setCity(String city)
	{
		this.city=city;
	}
	public String getCity()
	{
		return city;
	}		
	public void setAddress(String address)
	{
		this.address=address;
	}
	public String getAddress()
	{
		return address;
	}	
	public void setRemarks(String remarks)
	{
		this.remarks=remarks;
	}
	public String getRemarks()
	{
		return remarks;
	}	
	public void setSKU(String sku)
	{
		this.sku=sku;
	}
	public String getSKU()
	{
		return sku;
	}			
}
