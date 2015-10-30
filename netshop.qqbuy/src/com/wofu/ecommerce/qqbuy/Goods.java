package com.wofu.ecommerce.qqbuy;

import java.util.ArrayList;
import java.util.Date;

public class Goods {
	private String skuId = "" ;
	private String skuTitle = "" ;
	private String skufrontTitle = "" ;
	private String skuSubtitle = "" ;
	private String attr = "" ;
	private String classId = "" ;
	private String buyLimit = "" ;
	private String sellerPayFreight = "" ;
	private String skuLocalCode = "" ;
	private String producerBarCode = "" ;
	private String skuBarcode = "" ;
	private float marketPrice = 0.0f ;
	private float size = 0.0f ;
	private float weight = 0.0f ;
	private float skuSearchfactor = 0.0f ;
	private float skuVatrate = 0.0f ;
	private String present = "" ;
	private String spuId = "" ;
	private String shopCategoryAttr = "" ;
	private Date lastUpdateTime ;
	private String cannotReturned = "" ;
	private String cannotChange = "" ;
	private String secondhand = "" ;
	private String noPostage = "" ;
	private String skuState = "" ;
	private ArrayList<SkuInfo> stockList = new ArrayList<SkuInfo>() ;
	
	public ArrayList<SkuInfo> getStockList() {
		return stockList;
	}
	public void setStockList(ArrayList<SkuInfo> stockList) {
		this.stockList = stockList;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getBuyLimit() {
		return buyLimit;
	}
	public void setBuyLimit(String buyLimit) {
		this.buyLimit = buyLimit;
	}
	public String getCannotChange() {
		return cannotChange;
	}
	public void setCannotChange(String cannotChange) {
		this.cannotChange = cannotChange;
	}
	public String getCannotReturned() {
		return cannotReturned;
	}
	public void setCannotReturned(String cannotReturned) {
		this.cannotReturned = cannotReturned;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public float getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(float marketPrice) {
		this.marketPrice = marketPrice;
	}
	public String getNoPostage() {
		return noPostage;
	}
	public void setNoPostage(String noPostage) {
		this.noPostage = noPostage;
	}
	public String getPresent() {
		return present;
	}
	public void setPresent(String present) {
		this.present = present;
	}
	public String getProducerBarCode() {
		return producerBarCode;
	}
	public void setProducerBarCode(String producerBarCode) {
		this.producerBarCode = producerBarCode;
	}
	public String getSecondhand() {
		return secondhand;
	}
	public void setSecondhand(String secondhand) {
		this.secondhand = secondhand;
	}
	public String getSellerPayFreight() {
		return sellerPayFreight;
	}
	public void setSellerPayFreight(String sellerPayFreight) {
		this.sellerPayFreight = sellerPayFreight;
	}
	public String getShopCategoryAttr() {
		return shopCategoryAttr;
	}
	public void setShopCategoryAttr(String shopCategoryAttr) {
		this.shopCategoryAttr = shopCategoryAttr;
	}
	public float getSize() {
		return size;
	}
	public void setSize(float size) {
		this.size = size;
	}
	public String getSkuBarcode() {
		return skuBarcode;
	}
	public void setSkuBarcode(String skuBarcode) {
		this.skuBarcode = skuBarcode;
	}
	public String getSkufrontTitle() {
		return skufrontTitle;
	}
	public void setSkufrontTitle(String skufrontTitle) {
		this.skufrontTitle = skufrontTitle;
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public String getSkuLocalCode() {
		return skuLocalCode;
	}
	public void setSkuLocalCode(String skuLocalCode) {
		this.skuLocalCode = skuLocalCode;
	}
	public float getSkuSearchfactor() {
		return skuSearchfactor;
	}
	public void setSkuSearchfactor(float skuSearchfactor) {
		this.skuSearchfactor = skuSearchfactor;
	}
	public String getSkuSubtitle() {
		return skuSubtitle;
	}
	public void setSkuSubtitle(String skuSubtitle) {
		this.skuSubtitle = skuSubtitle;
	}
	public String getSkuTitle() {
		return skuTitle;
	}
	public void setSkuTitle(String skuTitle) {
		this.skuTitle = skuTitle;
	}
	public float getSkuVatrate() {
		return skuVatrate;
	}
	public void setSkuVatrate(float skuVatrate) {
		this.skuVatrate = skuVatrate;
	}
	public String getSpuId() {
		return spuId;
	}
	public void setSpuId(String spuId) {
		this.spuId = spuId;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public String getSkuState() {
		return skuState;
	}
	public void setSkuState(String skuState) {
		this.skuState = skuState;
	}

	
	
}
