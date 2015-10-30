package com.wofu.ecommerce.qqbuy;

import java.util.ArrayList;
import java.util.Hashtable;

public class OrderItem {
	private String tradeId = "" ;
	private String splitPackFlag = "" ;
	private String tradePayType = "" ;
	private String tradePayTypeDesc = "" ;
	private int tradeScore = 0 ;
	private float totalFee = 0.0f ;
	private String tradeGenTime  ;
	private String tradeEndTime ;
	private String tradeCheckTime ;
	private String tradePayTime ;
	private String tradeConsignTime ;
	private String goodsLastUpdateTime = "" ;
	private ArrayList  tradeProperty = new ArrayList() ;
	private int itemCount = 0 ;
	private int buyNum = 0 ;
	private String skuId = "" ;
	private String itemId = "" ;
	private String skuLocalCode = "" ;
	private String attr = "" ;
	private String attrCode = "" ;
	private String stockhouseId = "" ;
	private String itemPhisicStorage = "" ;
	private String itemStoreId = "" ;
	private float itemOriginPrice =  0.0f ;
	private float itemSoldPrice = 0.0f ;
	private float itemPrice = 0.0f ;
	private float tradePromotionFee = 0.0f ;
	private float tradeCouponFee = 0.0f ;
	private float tradeComboFavorFee = 0.0f ;
	private float tradeAdjustFee = 0.0f ;
	private String itemPic = "" ;
	private String itemTitle = "" ;
	private String itemType = "" ;
	private float refund = 0.0f ;
	private float haltRefund = 0.0f ;
	private String refundDealId = "" ;
	private String closeReasonType = "" ;
	private String closeReason = "" ;
	private String consignItemNum = "" ;
	private int noStockNum = 0 ;
	private int noStockRefund = 0 ;
	private int refuseNum = 0 ;
	private float refuseRefund = 0 ;
	private ArrayList<Hashtable<String, String>> activeList  = new ArrayList<Hashtable<String, String>>();
	private ArrayList refundList  = new ArrayList();
	public ArrayList getActiveList() {
		return activeList;
	}
	public void setActiveList(ArrayList<Hashtable<String, String>> activeList) {
		this.activeList = activeList;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public int getBuyNum() {
		return buyNum;
	}
	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}
	public String getCloseReason() {
		return closeReason;
	}
	public void setCloseReason(String closeReason) {
		this.closeReason = closeReason;
	}
	public String getCloseReasonType() {
		return closeReasonType;
	}
	public void setCloseReasonType(String closeReasonType) {
		this.closeReasonType = closeReasonType;
	}
	public float getHaltRefund() {
		return haltRefund;
	}
	public void setHaltRefund(float haltRefund) {
		this.haltRefund = haltRefund;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	public float getItemOriginPrice() {
		return itemOriginPrice;
	}
	public void setItemOriginPrice(float itemOriginPrice) {
		this.itemOriginPrice = itemOriginPrice;
	}
	public String getItemPhisicStorage() {
		return itemPhisicStorage;
	}
	public void setItemPhisicStorage(String itemPhisicStorage) {
		this.itemPhisicStorage = itemPhisicStorage;
	}
	public String getItemPic() {
		return itemPic;
	}
	public void setItemPic(String itemPic) {
		this.itemPic = itemPic;
	}
	public float getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(float itemPrice) {
		this.itemPrice = itemPrice;
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public float getItemSoldPrice() {
		return itemSoldPrice;
	}
	public void setItemSoldPrice(float itemSoldPrice) {
		this.itemSoldPrice = itemSoldPrice;
	}
	public String getItemStoreId() {
		return itemStoreId;
	}
	public void setItemStoreId(String itemStoreId) {
		this.itemStoreId = itemStoreId;
	}
	public String getItemTitle() {
		return itemTitle;
	}
	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public int getNoStockNum() {
		return noStockNum;
	}
	public void setNoStockNum(int noStockNum) {
		this.noStockNum = noStockNum;
	}
	public int getNoStockRefund() {
		return noStockRefund;
	}
	public void setNoStockRefund(int noStockRefund) {
		this.noStockRefund = noStockRefund;
	}
	public String getRefundDealId() {
		return refundDealId;
	}
	public void setRefundDealId(String refundDealId) {
		this.refundDealId = refundDealId;
	}
	public ArrayList getRefundList() {
		return refundList;
	}
	public void setRefundList(ArrayList refundList) {
		this.refundList = refundList;
	}
	public int getRefuseNum() {
		return refuseNum;
	}
	public void setRefuseNum(int refuseNum) {
		this.refuseNum = refuseNum;
	}
	public float getRefuseRefund() {
		return refuseRefund;
	}
	public void setRefuseRefund(float refuseRefund) {
		this.refuseRefund = refuseRefund;
	}
	public String getSkuLocalCode() {
		return skuLocalCode;
	}
	public void setSkuLocalCode(String skuLocalCode) {
		this.skuLocalCode = skuLocalCode;
	}
	public String getStockhouseId() {
		return stockhouseId;
	}
	public void setStockhouseId(String stockhouseId) {
		this.stockhouseId = stockhouseId;
	}
	public float getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(float totalFee) {
		this.totalFee = totalFee;
	}
	public float getTradeAdjustFee() {
		return tradeAdjustFee;
	}
	public void setTradeAdjustFee(float tradeAdjustFee) {
		this.tradeAdjustFee = tradeAdjustFee;
	}
	public String getTradeCheckTime() {
		return tradeCheckTime;
	}
	public void setTradeCheckTime(String tradeCheckTime) {
		this.tradeCheckTime = tradeCheckTime;
	}
	public float getTradeComboFavorFee() {
		return tradeComboFavorFee;
	}
	public void setTradeComboFavorFee(float tradeComboFavorFee) {
		this.tradeComboFavorFee = tradeComboFavorFee;
	}
	public String getTradeConsignTime() {
		return tradeConsignTime;
	}
	public void setTradeConsignTime(String tradeConsignTime) {
		this.tradeConsignTime = tradeConsignTime;
	}
	public float getTradeCouponFee() {
		return tradeCouponFee;
	}
	public void setTradeCouponFee(float tradeCouponFee) {
		this.tradeCouponFee = tradeCouponFee;
	}
	public String getTradeEndTime() {
		return tradeEndTime;
	}
	public void setTradeEndTime(String tradeEndTime) {
		this.tradeEndTime = tradeEndTime;
	}
	public String getTradeGenTime() {
		return tradeGenTime;
	}
	public void setTradeGenTime(String tradeGenTime) {
		this.tradeGenTime = tradeGenTime;
	}
	public String getTradeId() {
		return tradeId;
	}
	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}
	public String getTradePayTime() {
		return tradePayTime;
	}
	public void setTradePayTime(String tradePayTime) {
		this.tradePayTime = tradePayTime;
	}
	public float getTradePromotionFee() {
		return tradePromotionFee;
	}
	public void setTradePromotionFee(float tradePromotionFee) {
		this.tradePromotionFee = tradePromotionFee;
	}
	public ArrayList getTradeProperty() {
		return tradeProperty;
	}
	public void setTradeProperty(ArrayList tradeProperty) {
		this.tradeProperty = tradeProperty;
	}
	public int getTradeScore() {
		return tradeScore;
	}
	public void setTradeScore(int tradeScore) {
		this.tradeScore = tradeScore;
	}
	public String getSplitPackFlag() {
		return splitPackFlag;
	}
	public void setSplitPackFlag(String splitPackFlag) {
		this.splitPackFlag = splitPackFlag;
	}
	public String getTradePayType() {
		return tradePayType;
	}
	public void setTradePayType(String tradePayType) {
		this.tradePayType = tradePayType;
	}
	public String getTradePayTypeDesc() {
		return tradePayTypeDesc;
	}
	public void setTradePayTypeDesc(String tradePayTypeDesc) {
		this.tradePayTypeDesc = tradePayTypeDesc;
	}
	public String getGoodsLastUpdateTime() {
		return goodsLastUpdateTime;
	}
	public void setGoodsLastUpdateTime(String goodsLastUpdateTime) {
		this.goodsLastUpdateTime = goodsLastUpdateTime;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getAttrCode() {
		return attrCode;
	}
	public void setAttrCode(String attrCode) {
		this.attrCode = attrCode;
	}
	public float getRefund() {
		return refund;
	}
	public void setRefund(float refund) {
		this.refund = refund;
	}
	public String getConsignItemNum() {
		return consignItemNum;
	}
	public void setConsignItemNum(String consignItemNum) {
		this.consignItemNum = consignItemNum;
	}

}
