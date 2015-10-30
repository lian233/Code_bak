package com.wofu.ecommerce.qqbuy;

public class SkuInfo {
	private String stockhouseId = "" ;
	private String stockLocalcode = "" ;
	private String stockLocalBarcode  = "" ;//�̼�������
	private float primeCost = 0.0f ;
	private float price = 0.0f ;
	private int stockCount = 0 ;//���������,����δ�۳����Ѹ��δ����ġ�stockCount-stockPayedNum����ƽ̨ʣ��Ŀ������۵�����
	private int stockPayedNum = 0 ;//����Ѹ��������
	private String restrictedAreas = "" ;//�����ͳ���,�ֵ����sku�����˷�Χ��(��Ӣ��,�ָ��һ�������б�)
	private String stockSaleState = "" ;//STOCK_STATE_SELLING STOCK_STATE_INSTOCK
	private String stockPromotDesc = "" ;
	private String vatInvoice = "" ;//1�������ƷΪ���ṩ��ֵ˰��Ʊ��0�������Ʒ�����ṩ��ֵ˰��Ʊ
	private String b2b2cCoupon = "" ;
	private String coopertorCoupon = "" ;
	private String plainInvoice = "" ;//ҵ���߼���0�����ṩ��ͨ��Ʊ��1������ṩ��ͨ��Ʊ
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
