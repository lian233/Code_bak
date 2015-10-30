package com.wofu.ecommerce.qqbuy;

public class SkuInfo {
	private String stockhouseId = "" ;
	private String stockLocalcode = "" ;
	private String stockLocalBarcode  = "" ;//商家条形码
	private float primeCost = 0.0f ;
	private float price = 0.0f ;
	private int stockCount = 0 ;//库存总数量,包括未售出和已付款但未出库的。stockCount-stockPayedNum就是平台剩余的可以销售的数量
	private int stockPayedNum = 0 ;//库存已付款的数量
	private String restrictedAreas = "" ;//不配送城市,仓的这个sku，限运范围，(用英文,分割的一串数字列表)
	private String stockSaleState = "" ;//STOCK_STATE_SELLING STOCK_STATE_INSTOCK
	private String stockPromotDesc = "" ;
	private String vatInvoice = "" ;//1代表该商品为可提供增值税发票，0代表该商品不可提供增值税发票
	private String b2b2cCoupon = "" ;
	private String coopertorCoupon = "" ;
	private String plainInvoice = "" ;//业务逻辑：0代表不提供普通发票；1代表可提供普通发票
	private String plainInvoiceFlag = "" ;
	private String vatInvoiceFlag = "" ;
	public String getB2b2cCoupon() {
		return b2b2cCoupon;
	}
	public void setB2b2cCoupon(String coupon) {
		b2b2cCoupon = coupon;
	}
	public String getCoopertorCoupon() {
		return coopertorCoupon;
	}
	public void setCoopertorCoupon(String coopertorCoupon) {
		this.coopertorCoupon = coopertorCoupon;
	}
	public String getPlainInvoice() {
		return plainInvoice;
	}
	public void setPlainInvoice(String plainInvoice) {
		this.plainInvoice = plainInvoice;
	}
	public String getPlainInvoiceFlag() {
		return plainInvoiceFlag;
	}
	public void setPlainInvoiceFlag(String plainInvoiceFlag) {
		this.plainInvoiceFlag = plainInvoiceFlag;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getPrimeCost() {
		return primeCost;
	}
	public void setPrimeCost(float primeCost) {
		this.primeCost = primeCost;
	}
	public String getRestrictedAreas() {
		return restrictedAreas;
	}
	public void setRestrictedAreas(String restrictedAreas) {
		this.restrictedAreas = restrictedAreas;
	}
	public int getStockCount() {
		return stockCount;
	}
	public void setStockCount(int stockCount) {
		this.stockCount = stockCount;
	}
	public String getStockhouseId() {
		return stockhouseId;
	}
	public void setStockhouseId(String stockhouseId) {
		this.stockhouseId = stockhouseId;
	}
	public String getStockLocalBarcode() {
		return stockLocalBarcode;
	}
	public void setStockLocalBarcode(String stockLocalBarcode) {
		this.stockLocalBarcode = stockLocalBarcode;
	}
	public String getStockLocalcode() {
		return stockLocalcode;
	}
	public void setStockLocalcode(String stockLocalcode) {
		this.stockLocalcode = stockLocalcode;
	}
	public int getStockPayedNum() {
		return stockPayedNum;
	}
	public void setStockPayedNum(int stockPayedNum) {
		this.stockPayedNum = stockPayedNum;
	}
	public String getStockPromotDesc() {
		return stockPromotDesc;
	}
	public void setStockPromotDesc(String stockPromotDesc) {
		this.stockPromotDesc = stockPromotDesc;
	}
	public String getStockSaleState() {
		return stockSaleState;
	}
	public void setStockSaleState(String stockSaleState) {
		this.stockSaleState = stockSaleState;
	}
	public String getVatInvoice() {
		return vatInvoice;
	}
	public void setVatInvoice(String vatInvoice) {
		this.vatInvoice = vatInvoice;
	}
	public String getVatInvoiceFlag() {
		return vatInvoiceFlag;
	}
	public void setVatInvoiceFlag(String vatInvoiceFlag) {
		this.vatInvoiceFlag = vatInvoiceFlag;
	}


}
