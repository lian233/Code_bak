package com.wofu.ecommerce.oauthpaipai;

import java.util.Date;
import java.util.Vector;

public class Order {
	
	
	private String dealCode;			//订单编码 
	private String dealdesc;			//订单序列号，即是订单中的商品编码，多个商品时使用逗号隔开
	private String dealDetailLink;		//订单的详情连接 
	private String buyerRemark;			//买家下单时的留言内容
	private String dealPayType;			//订单的付款类型
	/*	UNKNOW:未定
		TENPAY:财付通
		OFF_LINE:线下交易
	*/
	private String dealPayTypeDesc;		//订单支付类型描述
	private String dealRateState;		//订单评价状态
	/*
	 * DEAL_RATE_NO_EVAL:评价未到期 
	 * DEAL_RATE_BUYER_NO_SELLER_NO:买家未评，卖家未评
	 * DEAL_RATE_BUYER_DONE_SELLER_NO:买家已评，卖家未评
	 * DEAL_RATE_BUYER_NO_SELLER_DONE:卖家已评，买家未评
	 * DEAL_RATE_BUYER_DONE_SELLER_DONE:买家已评，卖家已评 
	 * DEAL_RATE_DISABLE:不可评价
	 */
	private String dealRateStateDesc;	//订单的评价状态说明
	private String tenpayCode;			//财付通付款单号
	private String wuliuId;				//物流编码
	private String receiverAddress;		//收货人地址
	private String receiverMobile;		//收货人手机号码
	private String receiverName;		//收货人姓名
	private String receiverPhone;		//收货人电话号码
	private String receiverPostcode;	//收货人邮编
	private double sellerRecvRefund;	//退款:卖家实收金额
	private double buyerRecvRefund;		//退款:买家收到的退款金额
	private String buyerName;			//买家名称
	private String buyerUin;			//买家QQ号
	private double freight; 			//支付的运费(单位:分)
	private String transportType;  		//运送类型 
	/*
	 * TRANSPORT_NONE：卖家包邮，无需买家关心运送 
	 * TRANSPORT_MAIL：邮政寄送 
	 * TRANSPORT_EXPRESS：快递
	 * TRANSPORT_EMS：EMS TRANSPORT_UNKNOWN：未知的运输方式
	 */ 
	private String transportTypeDesc;	//运费类型说明
	private int whoPayShippingfee;		//承担运费方式: 1卖家;2买家
	private double totalCash;	  		//买家支付现金总额，包括所有可折算为现金的部分
	private double totalfee;			//商品总金额
	private double adjustfee;			//调整金额
	private double dealPayFeeTotal;  	//订单总支付金额 费用合计,一共要付的钱（包括可折合钱：积分、财付券之类）
	private String comboInfo;			//促销信息
	private double couponFee;			//折扣优惠信息
	private double dealPayFeePoint;		//实际积分支付金额
	private double dealPayFeeTicket;	//财付券支付金额	
	private String dealState;  			//订单状态: dealState 
	private String dealStateDesc;  		//订单状态说明：dealState 
	private String dealType;			//订单类型 
	private double shippingfeeCalc;		//运费合计说明,列出运费最终的计算式，以便每次订单详情展示给买家看
	private String sellerCrm;			//客服CRM
	private String sellerName;			//卖家名称
	private long sellerUin;				//卖家QQ
	/*
	SELL_TYPE_ALL:所有类型
	SELL_TYPE_BIN:一口价
	SELL_TYPE_AUCTION_SINGLE:单件拍卖
	SELL_TYPE_B2C:b2c订单
	*/
	private String dealTypeDesc;		//订单类型描述
	private Date createTime; 	 		//下单时间 
	private Date dealEndTime;			//结束时间
	private Date payTime;				//付款时间
	private Date payReturnTime;			//付款返款时间
	private Date recvfeeReturnTime;		//收到返款的时间
	private Date recvfeeTime;			//返款时间
	private Date sellerConsignmentTime; //卖家标记发货时间
	private Date lastUpdateTime;  		//最后修改时间 
	private int	hasInvoice;				//是否提供发票:0否,1是 
	private String invoiceContent;		//发票内容
	private String invoiceTitle;		//发票标题
	private String dealNoteType;		//订单的备注类型
	/*
	 * 订单备注类型 
		RED:红色
		YELLOW:黄色
		GREEN:绿色
		BLUE:蓝色
		PINK:粉红色
		UN_LABEL:未标注
	 */
	private String dealNote;			//订单备注内容
	private String dealFlag; 			//订单标志 
	private String availableAction;  	//订单在当前状态下可做的操作 
	private Date expectArrivalTime;		//期望到达时间
	private String wuliuCompany;		//物流公司
	private String wuliuCode;			//物流单号
	private String wuliuDesc;			//物流信息
	
	private Vector<OrderItem> orderitems=new Vector<OrderItem>();	//订单明细
	
	public String getAvailableAction() {
		return availableAction;
	}
	public void setAvailableAction(String availableAction) {
		this.availableAction = availableAction;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getBuyerRemark() {
		return buyerRemark;
	}
	public void setBuyerRemark(String buyerRemark) {
		this.buyerRemark = buyerRemark;
	}
	public String getBuyerUin() {
		return buyerUin;
	}
	public void setBuyerUin(String buyerUin) {
		this.buyerUin = buyerUin;
	}
	public String getComboInfo() {
		return comboInfo;
	}
	public void setComboInfo(String comboInfo) {
		this.comboInfo = comboInfo;
	}
	public double getCouponFee() {
		return couponFee;
	}
	public void setCouponFee(double couponFee) {
		this.couponFee = couponFee;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getDealCode() {
		return dealCode;
	}
	public void setDealCode(String dealCode) {
		this.dealCode = dealCode;
	}
	public String getDealDetailLink() {
		return dealDetailLink;
	}
	public void setDealDetailLink(String dealDetailLink) {
		this.dealDetailLink = dealDetailLink;
	}
	public Date getDealEndTime() {
		return dealEndTime;
	}
	public void setDealEndTime(Date dealEndTime) {
		this.dealEndTime = dealEndTime;
	}
	public String getDealFlag() {
		return dealFlag;
	}
	public void setDealFlag(String dealFlag) {
		this.dealFlag = dealFlag;
	}
	public String getDealNote() {
		return dealNote;
	}
	public void setDealNote(String dealNote) {
		this.dealNote = dealNote;
	}
	public String getDealNoteType() {
		return dealNoteType;
	}
	public void setDealNoteType(String dealNoteType) {
		this.dealNoteType = dealNoteType;
	}
	public Double getDealPayFeeTotal() {
		return dealPayFeeTotal;
	}
	public void setDealPayFeeTotal(Double dealPayFeeTotal) {
		this.dealPayFeeTotal = dealPayFeeTotal;
	}
	public String getDealPayType() {
		return dealPayType;
	}
	public void setDealPayType(String dealPayType) {
		this.dealPayType = dealPayType;
	}
	public String getDealRateState() {
		return dealRateState;
	}
	public void setDealRateState(String dealRateState) {
		this.dealRateState = dealRateState;
	}
	public String getDealState() {
		return dealState;
	}
	public void setDealState(String dealState) {
		this.dealState = dealState;
	}
	public String getDealStateDesc() {
		return dealStateDesc;
	}
	public void setDealStateDesc(String dealStateDesc) {
		this.dealStateDesc = dealStateDesc;
	}
	public double getFreight() {
		return freight;
	}
	public void setFreight(double freight) {
		this.freight = freight;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Date getPayReturnTime() {
		return payReturnTime;
	}
	public void setPayReturnTime(Date payReturnTime) {
		this.payReturnTime = payReturnTime;
	}
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	public String getReceiverAddress() {
		return receiverAddress;
	}
	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}
	public String getReceiverMobile() {
		return receiverMobile;
	}
	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getReceiverPhone() {
		return receiverPhone;
	}
	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}
	public String getReceiverPostcode() {
		return receiverPostcode;
	}
	public void setReceiverPostcode(String receiverPostcode) {
		this.receiverPostcode = receiverPostcode;
	}
	public Date getRecvfeeReturnTime() {
		return recvfeeReturnTime;
	}
	public void setRecvfeeReturnTime(Date recvfeeReturnTime) {
		this.recvfeeReturnTime = recvfeeReturnTime;
	}
	public Date getRecvfeeTime() {
		return recvfeeTime;
	}
	public void setRecvfeeTime(Date recvfeeTime) {
		this.recvfeeTime = recvfeeTime;
	}
	public Date getSellerConsignmentTime() {
		return sellerConsignmentTime;
	}
	public void setSellerConsignmentTime(Date sellerConsignmentTime) {
		this.sellerConsignmentTime = sellerConsignmentTime;
	}
	public String getTenpayCode() {
		return tenpayCode;
	}
	public void setTenpayCode(String tenpayCode) {
		this.tenpayCode = tenpayCode;
	}
	public double getTotalCash() {
		return totalCash;
	}

	public String getTransportType() {
		return transportType;
	}
	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}
	public String getWuliuId() {
		return wuliuId;
	}
	public void setWuliuId(String wuliuId) {
		this.wuliuId = wuliuId;
	}
	public Vector<OrderItem> getOrderitems() {
		return orderitems;
	}
	public void addOrderItem(OrderItem orderitem) {
		orderitems.add(orderitem);
	}
	public double getBuyerRecvRefund() {
		return buyerRecvRefund;
	}
	public void setBuyerRecvRefund(double buyerRecvRefund) {
		this.buyerRecvRefund = buyerRecvRefund;
	}
	public String getDealdesc() {
		return dealdesc;
	}
	public void setDealdesc(String dealdesc) {
		this.dealdesc = dealdesc;
	}
	public double getDealPayFeePoint() {
		return dealPayFeePoint;
	}
	public void setDealPayFeePoint(double dealPayFeePoint) {
		this.dealPayFeePoint = dealPayFeePoint;
	}
	public double getDealPayFeeTicket() {
		return dealPayFeeTicket;
	}
	public void setDealPayFeeTicket(double dealPayFeeTicket) {
		this.dealPayFeeTicket = dealPayFeeTicket;
	}
	public String getDealPayTypeDesc() {
		return dealPayTypeDesc;
	}
	public void setDealPayTypeDesc(String dealPayTypeDesc) {
		this.dealPayTypeDesc = dealPayTypeDesc;
	}
	public String getDealRateStateDesc() {
		return dealRateStateDesc;
	}
	public void setDealRateStateDesc(String dealRateStateDesc) {
		this.dealRateStateDesc = dealRateStateDesc;
	}
	public String getDealType() {
		return dealType;
	}
	public void setDealType(String dealType) {
		this.dealType = dealType;
	}
	public String getDealTypeDesc() {
		return dealTypeDesc;
	}
	public void setDealTypeDesc(String dealTypeDesc) {
		this.dealTypeDesc = dealTypeDesc;
	}
	public int getHasInvoice() {
		return hasInvoice;
	}
	public void setHasInvoice(int hasInvoice) {
		this.hasInvoice = hasInvoice;
	}
	public String getInvoiceContent() {
		return invoiceContent;
	}
	public void setInvoiceContent(String invoiceContent) {
		this.invoiceContent = invoiceContent;
	}
	public String getInvoiceTitle() {
		return invoiceTitle;
	}
	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}
	public String getSellerCrm() {
		return sellerCrm;
	}
	public void setSellerCrm(String sellerCrm) {
		this.sellerCrm = sellerCrm;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public double getSellerRecvRefund() {
		return sellerRecvRefund;
	}
	public void setSellerRecvRefund(double sellerRecvRefund) {
		this.sellerRecvRefund = sellerRecvRefund;
	}
	public long getSellerUin() {
		return sellerUin;
	}
	public void setSellerUin(long sellerUin) {
		this.sellerUin = sellerUin;
	}
	public double getShippingfeeCalc() {
		return shippingfeeCalc;
	}
	public void setShippingfeeCalc(double shippingfeeCalc) {
		this.shippingfeeCalc = shippingfeeCalc;
	}
	public String getTransportTypeDesc() {
		return transportTypeDesc;
	}
	public void setTransportTypeDesc(String transportTypeDesc) {
		this.transportTypeDesc = transportTypeDesc;
	}
	public int getWhoPayShippingfee() {
		return whoPayShippingfee;
	}
	public void setWhoPayShippingfee(int whoPayShippingfee) {
		this.whoPayShippingfee = whoPayShippingfee;
	}
	public void setDealPayFeeTotal(double dealPayFeeTotal) {
		this.dealPayFeeTotal = dealPayFeeTotal;
	}
	public void setOrderitems(Vector<OrderItem> orderitems) {
		this.orderitems = orderitems;
	}
	public void setTotalCash(double totalCash) {
		this.totalCash = totalCash;
	}
	public Date getExpectArrivalTime() {
		return expectArrivalTime;
	}
	public void setExpectArrivalTime(Date expectArrivalTime) {
		this.expectArrivalTime = expectArrivalTime;
	}
	public String getWuliuCode() {
		return wuliuCode;
	}
	public void setWuliuCode(String wuliuCode) {
		this.wuliuCode = wuliuCode;
	}
	public String getWuliuCompany() {
		return wuliuCompany;
	}
	public void setWuliuCompany(String wuliuCompany) {
		this.wuliuCompany = wuliuCompany;
	}
	public String getWuliuDesc() {
		return wuliuDesc;
	}
	public void setWuliuDesc(String wuliuDesc) {
		this.wuliuDesc = wuliuDesc;
	}
	public double getAdjustfee() {
		return adjustfee;
	}
	public void setAdjustfee(double adjustfee) {
		this.adjustfee = adjustfee;
	}
	public double getTotalfee() {
		return totalfee;
	}
	public void setTotalfee(double totalfee) {
		this.totalfee = totalfee;
	}	
}
