package com.wofu.ecommerce.alibaba;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Order extends BusinessObject{
	private String sellerRateStatus;   //卖家评价状态，4-已评价，5-未评价
	private String buyerRateStatus;    //买家评价状态，4-已评价，5-未评价
	private long id;              //订单ID
	private String alipayTradeId;   //支付宝交易号
	private String sellerAlipayId;   //卖家支付宝ID
	private String buyerMemberId;    //买家会员登录名，即会员id

	private Integer tradeType;    //1：担保交易，2：预付款交易，3：ETC境外收单交易，4：及时到账交易，5：保障金安全交易，6：统一交易流程，7：分阶段交易，8：货到付款交易，9：信用凭证支付交易
	private String status;        //担保交易共有5个状态： waitbuyerpay(等待买家付款), waitsellersend(等待卖家发货), waitbuyerreceive(等待买家收货), success(交易成功), cancel(交易取消，违约金等交割完毕); 即时到账交易共有4个状态： waitbuyerpay(等待买家付款), waitsellersend(等待卖家发货),(交易成功), cancel(交易取消，违约金等交割完毕) 分阶段交易包括：waitbuyerpay(等待买家付款), waitsellersend(等待卖家发货), waitbuyerreceive(等待买家收货), success(交易成功), cancel(交易取消，违约金等交割完毕),waitselleract(等待卖家操作),waitbuyerconfirmaction(等待买家确认操作),waitsellerpush(等待卖家推进)
	private String gmtCreate;     //买家下单时间，即订单创建时间
	
	private String gmtPayment;   //买家付款时间
	//private Integer sourceId;    //商品信息数组-商品ID
	
	private String[] productPic; //商品信息数组-商品所有图片的URL地址
	private Integer productName; //商品信息数组-商品名称
	private Integer quantity;    //商品信息数组-订单中该商品的购买数量
	
	private String[] specInfo;   //属性信息
	private String   specName;   //属性名称
	private String   specValue;  //属性值
	private String   specUnit;   //属性单位
	private double   carriage;   //运费，单位：分
	private double   discount;   //涨价或折扣，折扣为负数（单位:分）
	private double   sumPayment; //货品总价+运费=实付款（单位:分）
	private String   couponAmount; //抵价券实际消费金额（单位:分）
	private String   toArea;      //收货人所在地区
	private String   toPost;      //收货人邮编
	private String   toFullName;  //收货人姓名
	private String   toMobile;    //收货人手机
	private String   toPhone;     //收货人电话
	
	private String bankAndAccount;  //发票信息数组-银行及账户
	private String buyerAlipayId;  //买家支付宝ID
	private String buyerCompanyName;  //买家公司名
	private String buyerFeedback;  //买家留言
	private String buyerMobile;  //买家手机号
	private String buyerPhone;  //买家电话
	
	private String closeReason;  //关闭交易理由
	private String codActualFee;  //cod交易的实付款，单位：分
	private String codAudit;  //是否COD订单并且清算成功
	private String codBuyerFee;  //买家承担的服务费
	private String codBuyerInitFee;  //买家承担的服务费初始值，单位：分
	private String codFee;  //cod服务费，单位：分
	private String codFeeDividend;  //cod三家分润
	private String codGmtSign;  //买家签收时间
	private String codInitFee;  //cod服务费初始值，单位：分
	private String codSellerFee;  //卖家承担的服务费，单位：分
	private String codStatus;  //COD物流状态，取值范围：0(初始值),20(接单),-20(不接单),2(接单超时),30(揽收成功),-30(揽收失败),3(揽收超时),100(签收成功),-100(签收失败),10(订单等候发送给物流公司),-1(用户取消物流订单)
	private String entryCodStatus;  //订单明细货到付款状态，取值范围同订单codStatus
	private String entryDiscount;  //订单明细折扣
	private String entryStatus;  //订单明细状态，取值范围同订单status
	private String fromAddress;  //发货街道地址
	private String fromArea;  //发货区
	private String fromCity;  //发货市
	private String fromContact;  //发货联系人
	private String fromMobile;  //发货联系手机
	private String fromPhone;  //发货联系电话
	private String fromPost;  //发货地址邮编
	private String fromProvince;  //发货省
	private String gmtCompleted;  //交易完成时间
	private String gmtGoodsSend;  //卖家发货时间
	private String gmtModified;  //交易最后修改时间
	
	private DataRelation orderEntries = new DataRelation("orderEntries","com.wofu.ecommerce.alibaba.OrderItem");
	
	public String getSellerRateStatus() {
		return sellerRateStatus;
	}
	public void setSellerRateStatus(String sellerRateStatus) {
		this.sellerRateStatus = sellerRateStatus;
	}
	public String getBuyerRateStatus() {
		return buyerRateStatus;
	}
	public void setBuyerRateStatus(String buyerRateStatus) {
		this.buyerRateStatus = buyerRateStatus;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAlipayTradeId() {
		return alipayTradeId;
	}
	public void setAlipayTradeId(String alipayTradeId) {
		this.alipayTradeId = alipayTradeId;
	}
	public String getSellerAlipayId() {
		return sellerAlipayId;
	}
	public void setSellerAlipayId(String sellerAlipayId) {
		this.sellerAlipayId = sellerAlipayId;
	}
	public String getBuyerMemberId() {
		return buyerMemberId;
	}
	public void setBuyerMemberId(String buyerMemberId) {
		this.buyerMemberId = buyerMemberId;
	}
	public Integer getTradeType() {
		return tradeType;
	}
	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(String gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getGmtPayment() {
		return gmtPayment;
	}
	public void setGmtPayment(String gmtPayment) {
		this.gmtPayment = gmtPayment;
	}
	//public Integer getSourceId() {
	//	return sourceId;
	//}
	//public void setSourceId(Integer sourceId) {
	//	this.sourceId = sourceId;
	//}
	public String[] getProductPic() {
		return productPic;
	}
	public void setProductPic(String[] productPic) {
		this.productPic = productPic;
	}
	public Integer getProductName() {
		return productName;
	}
	public void setProductName(Integer productName) {
		this.productName = productName;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String[] getSpecInfo() {
		return specInfo;
	}
	public void setSpecInfo(String[] specInfo) {
		this.specInfo = specInfo;
	}
	public String getSpecName() {
		return specName;
	}
	public void setSpecName(String specName) {
		this.specName = specName;
	}
	public String getSpecValue() {
		return specValue;
	}
	public void setSpecValue(String specValue) {
		this.specValue = specValue;
	}
	public String getSpecUnit() {
		return specUnit;
	}
	public void setSpecUnit(String specUnit) {
		this.specUnit = specUnit;
	}
	public double getCarriage() {
		return carriage;
	}
	public void setCarriage(double carriage) {
		this.carriage = carriage;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public double getSumPayment() {
		return sumPayment;
	}
	public void setSumPayment(double sumPayment) {
		this.sumPayment = sumPayment;
	}
	public String getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(String couponAmount) {
		this.couponAmount = couponAmount;
	}
	public String getToArea() {
		return toArea;
	}
	public void setToArea(String toArea) {
		this.toArea = toArea;
	}
	public String getToPost() {
		return toPost;
	}
	public void setToPost(String toPost) {
		this.toPost = toPost;
	}
	public String getToFullName() {
		return toFullName;
	}
	public void setToFullName(String toFullName) {
		this.toFullName = toFullName;
	}
	public String getToMobile() {
		return toMobile;
	}
	public void setToMobile(String toMobile) {
		this.toMobile = toMobile;
	}
	public String getToPhone() {
		return toPhone;
	}
	public void setToPhone(String toPhone) {
		this.toPhone = toPhone;
	}
	public String getBankAndAccount() {
		return bankAndAccount;
	}
	public void setBankAndAccount(String bankAndAccount) {
		this.bankAndAccount = bankAndAccount;
	}
	public String getBuyerAlipayId() {
		return buyerAlipayId;
	}
	public void setBuyerAlipayId(String buyerAlipayId) {
		this.buyerAlipayId = buyerAlipayId;
	}
	public String getBuyerCompanyName() {
		return buyerCompanyName;
	}
	public void setBuyerCompanyName(String buyerCompanyName) {
		this.buyerCompanyName = buyerCompanyName;
	}
	public String getBuyerFeedback() {
		return buyerFeedback;
	}
	public void setBuyerFeedback(String buyerFeedback) {
		this.buyerFeedback = buyerFeedback;
	}
	public String getBuyerMobile() {
		return buyerMobile;
	}
	public void setBuyerMobile(String buyerMobile) {
		this.buyerMobile = buyerMobile;
	}
	public String getBuyerPhone() {
		return buyerPhone;
	}
	public void setBuyerPhone(String buyerPhone) {
		this.buyerPhone = buyerPhone;
	}
	public String getCloseReason() {
		return closeReason;
	}
	public void setCloseReason(String closeReason) {
		this.closeReason = closeReason;
	}
	public String getCodActualFee() {
		return codActualFee;
	}
	public void setCodActualFee(String codActualFee) {
		this.codActualFee = codActualFee;
	}
	public String getCodAudit() {
		return codAudit;
	}
	public void setCodAudit(String codAudit) {
		this.codAudit = codAudit;
	}
	public String getCodBuyerFee() {
		return codBuyerFee;
	}
	public void setCodBuyerFee(String codBuyerFee) {
		this.codBuyerFee = codBuyerFee;
	}
	public String getCodBuyerInitFee() {
		return codBuyerInitFee;
	}
	public void setCodBuyerInitFee(String codBuyerInitFee) {
		this.codBuyerInitFee = codBuyerInitFee;
	}
	public String getCodFee() {
		return codFee;
	}
	public void setCodFee(String codFee) {
		this.codFee = codFee;
	}
	public String getCodFeeDividend() {
		return codFeeDividend;
	}
	public void setCodFeeDividend(String codFeeDividend) {
		this.codFeeDividend = codFeeDividend;
	}
	public String getCodGmtSign() {
		return codGmtSign;
	}
	public void setCodGmtSign(String codGmtSign) {
		this.codGmtSign = codGmtSign;
	}
	public String getCodInitFee() {
		return codInitFee;
	}
	public void setCodInitFee(String codInitFee) {
		this.codInitFee = codInitFee;
	}
	public String getCodSellerFee() {
		return codSellerFee;
	}
	public void setCodSellerFee(String codSellerFee) {
		this.codSellerFee = codSellerFee;
	}
	public String getCodStatus() {
		return codStatus;
	}
	public void setCodStatus(String codStatus) {
		this.codStatus = codStatus;
	}
	public String getEntryCodStatus() {
		return entryCodStatus;
	}
	public void setEntryCodStatus(String entryCodStatus) {
		this.entryCodStatus = entryCodStatus;
	}
	public String getEntryDiscount() {
		return entryDiscount;
	}
	public void setEntryDiscount(String entryDiscount) {
		this.entryDiscount = entryDiscount;
	}
	public String getEntryStatus() {
		return entryStatus;
	}
	public void setEntryStatus(String entryStatus) {
		this.entryStatus = entryStatus;
	}
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getFromArea() {
		return fromArea;
	}
	public void setFromArea(String fromArea) {
		this.fromArea = fromArea;
	}
	public String getFromCity() {
		return fromCity;
	}
	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}
	public String getFromContact() {
		return fromContact;
	}
	public void setFromContact(String fromContact) {
		this.fromContact = fromContact;
	}
	public String getFromMobile() {
		return fromMobile;
	}
	public void setFromMobile(String fromMobile) {
		this.fromMobile = fromMobile;
	}
	public String getFromPhone() {
		return fromPhone;
	}
	public void setFromPhone(String fromPhone) {
		this.fromPhone = fromPhone;
	}
	public String getFromPost() {
		return fromPost;
	}
	public void setFromPost(String fromPost) {
		this.fromPost = fromPost;
	}
	public String getFromProvince() {
		return fromProvince;
	}
	public void setFromProvince(String fromProvince) {
		this.fromProvince = fromProvince;
	}
	public String getGmtCompleted() {
		return gmtCompleted;
	}
	public void setGmtCompleted(String gmtCompleted) {
		this.gmtCompleted = gmtCompleted;
	}
	public String getGmtGoodsSend() {
		return gmtGoodsSend;
	}
	public void setGmtGoodsSend(String gmtGoodsSend) {
		this.gmtGoodsSend = gmtGoodsSend;
	}
	public String getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(String gmtModified) {
		this.gmtModified = gmtModified;
	}
	
	
	public DataRelation getOrderEntries() {
		return orderEntries;
	}
	public void setOrderEntries(DataRelation orderEntries) {
		this.orderEntries = orderEntries;
	}
	
}
