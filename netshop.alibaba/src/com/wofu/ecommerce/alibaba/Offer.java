package com.wofu.ecommerce.alibaba;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Offer extends BusinessObject{
	private int bizType;
	private boolean supportOnlineTrade;
	private boolean pictureAuthOffer;
	private boolean priceAuthOffer;
	private boolean skuTradeSupport;
	
	private boolean mixWholeSale;
	private long offerId;
	private String priceRanges;
	private int amountOnSale;
	private String productFeatures;
	private int categoryID;
	private int periodOfValidity;
	
	private String skuPics;
	private String offerDetail;
	private String subject;
	private	String imageUriList;
	private	String freightType;
	private int sendGoodsAddressId;
	private int freightTemplateId;
	private String offerWeight;
	private String skuList;
	
	public Offer(){
		this.bizType=0;
		this.supportOnlineTrade=false;
		this.pictureAuthOffer=false;
		this.priceAuthOffer=false;
		this.skuTradeSupport=false;
		this.mixWholeSale=false;
		this.offerId=1;
		this.priceRanges="";
		this.amountOnSale=0;
		this.productFeatures="";
		this.categoryID=0;
		this.periodOfValidity=0;
		this.offerDetail="";
		this.subject="";
		this.imageUriList="";
		this.freightType="";
		this.sendGoodsAddressId=1;
		this.freightTemplateId=1;
		this.offerWeight="";
		this.skuPics="";
		//this.skuList="";
		
	}
	
	public int getBizType() {
		return bizType;
	}
	public void setBizType(int bizType) {
		this.bizType = bizType;
	}
	public boolean getSupportOnlineTrade() {
		return supportOnlineTrade;
	}
	public void setSupportOnlineTrade(boolean supportOnlineTrade) {
		this.supportOnlineTrade = supportOnlineTrade;
	}
	public boolean getPictureAuthOffer() {
		return pictureAuthOffer;
	}
	public void setPictureAuthOffer(boolean pictureAuthOffer) {
		this.pictureAuthOffer = pictureAuthOffer;
	}
	public boolean getPriceAuthOffer() {
		return priceAuthOffer;
	}
	public void setPriceAuthOffer(boolean priceAuthOffer) {
		this.priceAuthOffer = priceAuthOffer;
	}
	public boolean getSkuTradeSupport() {
		return skuTradeSupport;
	}
	public void setSkuTradeSupport(boolean skuTradeSupport) {
		this.skuTradeSupport = skuTradeSupport;
	}
	public boolean getMixWholeSale() {
		return mixWholeSale;
	}
	public void setMixWholeSale(boolean mixWholeSale) {
		this.mixWholeSale = mixWholeSale;
	}
	public long getOfferId() {
		return offerId;
	}
	public void setOfferId(long offerId) {
		this.offerId = offerId;
	}
	public String getPriceRanges() {
		return priceRanges;
	}
	public void setPriceRanges(String priceRanges) {
		this.priceRanges = priceRanges;
	}
	public int getAmountOnSale() {
		return amountOnSale;
	}
	public void setAmountOnSale(int amountOnSale) {
		this.amountOnSale = amountOnSale;
	}
	public String getProductFeatures() {
		return productFeatures;
	}
	public void setProductFeatures(String productFeatures) {
		this.productFeatures = productFeatures;
	}
	public int getCategoryID() {
		return categoryID;
	}
	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}
	public int getPeriodOfValidity() {
		return periodOfValidity;
	}
	public void setPeriodOfValidity(int periodOfValidity) {
		this.periodOfValidity = periodOfValidity;
	}
	public String getOfferDetail() {
		return offerDetail;
	}
	public void setOfferDetail(String offerDetail) {
		this.offerDetail = offerDetail;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getImageUriList() {
		return imageUriList;
	}
	public void setImageUriList(String imageUriList) {
		this.imageUriList = imageUriList;
	}
	public String getFreightType() {
		return freightType;
	}
	public void setFreightType(String freightType) {
		this.freightType = freightType;
	}
	public int getSendGoodsAddressId() {
		return sendGoodsAddressId;
	}
	public void setSendGoodsAddressId(int sendGoodsAddressId) {
		this.sendGoodsAddressId = sendGoodsAddressId;
	}
	public int getFreightTemplateId() {
		return freightTemplateId;
	}
	public void setFreightTemplateId(int freightTemplateId) {
		this.freightTemplateId = freightTemplateId;
	}
	public String getOfferWeight() {
		return offerWeight;
	}
	public void setOfferWeight(String offerWeight) {
		this.offerWeight = offerWeight;
	}

	public String getSkuList() {
		return skuList;
	}

	public void setSkuList(String skuList) {
		this.skuList = skuList;
	}

	public String getSkuPics() {
		return skuPics;
	}

	public void setSkuPics(String skuPics) {
		this.skuPics = skuPics;
	}

	
}
