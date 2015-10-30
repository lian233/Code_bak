package com.wofu.ecommerce.s;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Order extends BusinessObject
{
	private String GoodsPrice;
	private int OrderStatus;
	private String RcvAddrDetail;
	private String RcvAddrId;
	private String RcvName;
	private String RcvTel;
	private String SellerId;
	private String SellerMemo;
	private String SellerOrderNo;
	private String SubmitDate;
	private String UpdateDate;
	private String VendorId;
	private String VendorOrderNo;
	private String SuggestExpress;
	private int TotalResults;
	private String ErrCode;
	private String ErrMsg;
	private boolean IsError;
	private int Qty;
	private String SalePrice;
	private String VendorOrderDetNo;
	private String VendorSkuId;
	private String UnitPrice;
	
	
	private DataRelation orderItemList=new DataRelation("orderItemList","com.wofu.ecommerce.s.OrderItem");	
	public DataRelation getOrderItemList()
	{
		return orderItemList;
	}
	public void setOrderItemList(DataRelation orderItemList) 
	{
		this.orderItemList = orderItemList;
	}
	public void setGoodsPrice(String GoodsPrice)
	{
		this.GoodsPrice=GoodsPrice;
	}
	public String getGoodsPrice()
	{
		return GoodsPrice;
	}
	public void setOrderStatus(int OrderStatus)
	{
		this.OrderStatus=OrderStatus;
	}
	public int getOrderStatus()
	{
		return OrderStatus;
	}
	public void setRcvAddrDetail(String RcvAddrDetail)
	{
		this.RcvAddrDetail=RcvAddrDetail;
	}
	public String getRcvAddrDetail()
	{
		return RcvAddrDetail;
	}
	public void setRcvAddrId(String RcvAddrId)
	{
		this.RcvAddrId=RcvAddrId;
	}
	public String getRcvAddrId()
	{
		return RcvAddrId;
	}	
	public void setRcvName(String RcvName)
	{
		this.RcvName=RcvName;
	}	
	public String getRcvName()
	{
		return RcvName;
	}
	public void setRcvTel(String RcvTel)
	{
		this.RcvTel=RcvTel;
	}
	public String getRcvTel()
	{
		return RcvTel;
	}
	public void setSellerId(String SellerId)
	{
		this.SellerId=SellerId;
	}
	public String getSellerId()
	{
		return SellerId;
	}
	public void setSellerMemo(String SellerMemo)
	{
		this.SellerMemo=SellerMemo;
	}
	public String getSellerMemo()
	{
		return SellerMemo;
	}
	public void setSellerOrderNo(String SellerOrderNo)
	{
		this.SellerOrderNo=SellerOrderNo;
	}
	public String getSellerOrderNo()
	{
		return SellerOrderNo;
	}
	public void setSubmitDate(String SubmitDate)
	{
		this.SubmitDate=SubmitDate;
	}
	public String getSubmitDate()
	{
		return SubmitDate;
	}
	public void setUpdateDate(String UpdateDate)
	{
		this.UpdateDate=UpdateDate;
	}
	public String getUpdateDate()
	{
		return UpdateDate;
	}
	public void setVendorId(String VendorId)
	{
		this.VendorId=VendorId;
	}
	public String getVendorId()
	{
		return VendorId;
	}
	public void setVendorOrderNo(String VendorOrderNo)
	{
		this.VendorOrderNo=VendorOrderNo;
	}
	public String getVendorOrderNo()
	{
		return VendorOrderNo;
	}
	public void setSuggestExpress(String SuggestExpress)
	{
		this.SuggestExpress=SuggestExpress;
	}
	public String getSuggestExpress()
	{
		return SuggestExpress;
	}
	public void setTotalResults(int TotalResults)
	{
		this.TotalResults=TotalResults;
	}
	public int getTotalResults()
	{
		return TotalResults;
	}
	public void setErrCode(String ErrCode)
	{
		this.ErrCode=ErrCode;
	}
	public String getErrCode()
	{
		return ErrCode;
	}
	public void setErrMsg(String ErrMsg)
	{
		this.ErrMsg=ErrMsg;
	}
	public String getErrMsg()
	{
		return ErrMsg;
	}
	public void setIsError(boolean IsError)
	{
		this.IsError=IsError;
	}
	public boolean getIsError()
	{
		return IsError;
	}
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
