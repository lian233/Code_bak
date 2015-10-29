package com.wofu.ecommerce.alibaba;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
import com.wofu.common.json.JSONObject;

public class Goods extends BusinessObject {
	private boolean isOfferSupportOnlineTrade;
	private boolean isPicAuthOffer;
	private boolean isPriceAuthOffer;
	private boolean isSkuOffer;
	private boolean isSupportMix;
	private boolean isPrivateOffer;
	private boolean isSkuTradeSupported;
	private int freightTemplateId; //物流版块
	private String freightType;		//运费模板
	private long offerId;			 //商品ID
	private String detailsUrl;       //商品详情地址
	private String type;             //商品类型。Sale：供应信息，Buy：求购信息
	private String tradeType;       //贸易类型。1：产品，2：加工，3：代理，4：合作，5：商务服务
	private int postCategryId;   //所属叶子类目ID
	private String offerStatus;      //状态。auditing：审核中；online：已上网；FailAudited：审核未通过；outdated：已过期；member delete(d)：用户删除；delete：审核删除
	private String memberId;		 //卖家会员ID
	private String subject;			 //商品标题
	private String details;          //详情说明
	private String priceRanges;
	
	private int amount;
	private int amountOnSale;
	private int saledCount;
	private String retailPrice;
	private String unit;			//商品单位
	private String priceUnit;
	private double unitPrice;
	private int termOfferProcess;
	private int sendGoodsId;
	private String productUnitWeight;
	private int qualityLevel;
	
	private String gmtCreate;
	private String gmtModified;
	private String gmtApproved;
	private String gmtExpire;
	private String gmtLastRepost;
	
	private DataRelation skuArray = new DataRelation("skuArray","com.wofu.ecommerce.alibaba.GoodsSKU");
	private String productFeatureList;
	
	
	public Goods() {
		this.isPrivateOffer=false;
		this.isSkuTradeSupported=false;
		this.isOfferSupportOnlineTrade = false;
		this.isPicAuthOffer = false;
		this.isPriceAuthOffer = false;
		this.isSkuOffer = false;
		this.isSupportMix = false;
		this.freightTemplateId = 1;
		this.freightType = "";
		this.offerId = 1;
		this.detailsUrl = "";
		this.type = "";
		this.tradeType = "";
		this.postCategryId = 1;
		this.offerStatus = "";
		this.memberId = "";
		this.subject = "";
		this.details = "";
		this.amount = 1;
		this.amountOnSale = 1;
		this.saledCount = 1;
		this.retailPrice = "";
		this.unit="";
		this.priceUnit="";
		this.unitPrice = 1;
		this.termOfferProcess = 1;
		this.sendGoodsId = 1;
		this.productUnitWeight = "";
		this.qualityLevel=1;
		this.gmtCreate = "";
		this.gmtModified = "";
		this.gmtApproved="";
		this.gmtExpire="";
		this.gmtLastRepost="";
		//this.skuArray = null;
		
		//this.productFeatureList = null;
	}
	public DataRelation getSkuArray() {
		return skuArray;
	}
	public void setSkuArray(DataRelation skuArray) {
		this.skuArray = skuArray;
	}
	public long getOfferId() {
		return offerId;
	}
	public void setOfferId(long offerId) {
		this.offerId = offerId;
	}
	public String getDetailsUrl() {
		return detailsUrl;
	}
	public void setDetailsUrl(String detailsUrl) {
		this.detailsUrl = detailsUrl;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public int getPostCategryId() {
		return postCategryId;
	}
	public void setPostCategryId(int postCategryId) {
		this.postCategryId = postCategryId;
	}
	public String getOfferStatus() {
		return offerStatus;
	}
	public void setOfferStatus(String offerStatus) {
		this.offerStatus = offerStatus;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getAmountOnSale() {
		return amountOnSale;
	}
	public void setAmountOnSale(int amountOnSale) {
		this.amountOnSale = amountOnSale;
	}
	public int getSaledCount() {
		return saledCount;
	}
	public void setSaledCount(int saledCount) {
		this.saledCount = saledCount;
	}
	public String getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(String retailPrice) {
		this.retailPrice = retailPrice;
	}
	public double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public int getTermOfferProcess() {
		return termOfferProcess;
	}
	public void setTermOfferProcess(int termOfferProcess) {
		this.termOfferProcess = termOfferProcess;
	}
	public int getSendGoodsId() {
		return sendGoodsId;
	}
	public void setSendGoodsId(int sendGoodsId) {
		this.sendGoodsId = sendGoodsId;
	}
	public String getProductUnitWeight() {
		return productUnitWeight;
	}
	public void setProductUnitWeight(String productUnitWeight) {
		this.productUnitWeight = productUnitWeight;
	}
	public String getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(String gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(String gmtModified) {
		this.gmtModified = gmtModified;
	}
	public String getProductFeatureList() {
		return productFeatureList;
	}
	public void setProductFeatureList(String productFeatureList) {
		this.productFeatureList = productFeatureList;
	}
	public boolean getIsOfferSupportOnlineTrade() {
		return isOfferSupportOnlineTrade;
	}
	public void setIsOfferSupportOnlineTrade(boolean isOfferSupportOnlineTrade) {
		this.isOfferSupportOnlineTrade = isOfferSupportOnlineTrade;
	}
	public boolean getIsPicAuthOffer() {
		return isPicAuthOffer;
	}
	public void setIsPicAuthOffer(boolean isPicAuthOffer) {
		this.isPicAuthOffer = isPicAuthOffer;
	}
	public boolean getIsPriceAuthOffer() {
		return isPriceAuthOffer;
	}
	public void setIsPriceAuthOffer(boolean isPriceAuthOffer) {
		this.isPriceAuthOffer = isPriceAuthOffer;
	}
	public boolean getIsSkuOffer() {
		return isSkuOffer;
	}
	public void setIsSkuOffer(boolean isSkuOffer) {
		this.isSkuOffer = isSkuOffer;
	}
	public boolean getIsSupportMix() {
		return isSupportMix;
	}
	public void setIsSupportMix(boolean isSupportMix) {
		this.isSupportMix = isSupportMix;
	}
	public int getFreightTemplateId() {
		return freightTemplateId;
	}
	public void setFreightTemplateId(int freightTemplateId) {
		this.freightTemplateId = freightTemplateId;
	}
	public String getFreightType() {
		return freightType;
	}
	public void setFreightType(String freightType) {
		this.freightType = freightType;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public boolean getIsPrivateOffer() {
		return isPrivateOffer;
	}
	public void setIsPrivateOffer(boolean isPrivateOffer) {
		this.isPrivateOffer = isPrivateOffer;
	}
	public String getPriceUnit() {
		return priceUnit;
	}
	public void setPriceUnit(String priceUnit) {
		this.priceUnit = priceUnit;
	}
	public int getQualityLevel() {
		return qualityLevel;
	}
	public void setQualityLevel(int qualityLevel) {
		this.qualityLevel = qualityLevel;
	}
	public String getGmtApproved() {
		return gmtApproved;
	}
	public void setGmtApproved(String gmtApproved) {
		this.gmtApproved = gmtApproved;
	}
	public String getGmtExpire() {
		return gmtExpire;
	}
	public void setGmtExpire(String gmtExpire) {
		this.gmtExpire = gmtExpire;
	}
	public String getGmtLastRepost() {
		return gmtLastRepost;
	}
	public void setGmtLastRepost(String gmtLastRepost) {
		this.gmtLastRepost = gmtLastRepost;
	}
	public boolean getIsSkuTradeSupported() {
		return isSkuTradeSupported;
	}
	public void setIsSkuTradeSupported(boolean isSkuTradeSupported) {
		this.isSkuTradeSupported = isSkuTradeSupported;
	}
	public String getPriceRanges() {
		return priceRanges;
	}
	public void setPriceRanges(String priceRanges) {
		this.priceRanges = priceRanges;
	}
	
	
}
