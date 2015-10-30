package com.wofu.ecommerce.oauthpaipai;

import java.util.Date;
import java.util.Vector;

public class Order {
	
	
	private String dealCode;			//�������� 
	private String dealdesc;			//�������кţ����Ƕ����е���Ʒ���룬�����Ʒʱʹ�ö��Ÿ���
	private String dealDetailLink;		//�������������� 
	private String buyerRemark;			//����µ�ʱ����������
	private String dealPayType;			//�����ĸ�������
	/*	UNKNOW:δ��
		TENPAY:�Ƹ�ͨ
		OFF_LINE:���½���
	*/
	private String dealPayTypeDesc;		//����֧����������
	private String dealRateState;		//��������״̬
	/*
	 * DEAL_RATE_NO_EVAL:����δ���� 
	 * DEAL_RATE_BUYER_NO_SELLER_NO:���δ��������δ��
	 * DEAL_RATE_BUYER_DONE_SELLER_NO:�������������δ��
	 * DEAL_RATE_BUYER_NO_SELLER_DONE:�������������δ��
	 * DEAL_RATE_BUYER_DONE_SELLER_DONE:����������������� 
	 * DEAL_RATE_DISABLE:��������
	 */
	private String dealRateStateDesc;	//����������״̬˵��
	private String tenpayCode;			//�Ƹ�ͨ�����
	private String wuliuId;				//��������
	private String receiverAddress;		//�ջ��˵�ַ
	private String receiverMobile;		//�ջ����ֻ�����
	private String receiverName;		//�ջ�������
	private String receiverPhone;		//�ջ��˵绰����
	private String receiverPostcode;	//�ջ����ʱ�
	private double sellerRecvRefund;	//�˿�:����ʵ�ս��
	private double buyerRecvRefund;		//�˿�:����յ����˿���
	private String buyerName;			//�������
	private String buyerUin;			//���QQ��
	private double freight; 			//֧�����˷�(��λ:��)
	private String transportType;  		//�������� 
	/*
	 * TRANSPORT_NONE�����Ұ��ʣ�������ҹ������� 
	 * TRANSPORT_MAIL���������� 
	 * TRANSPORT_EXPRESS�����
	 * TRANSPORT_EMS��EMS TRANSPORT_UNKNOWN��δ֪�����䷽ʽ
	 */ 
	private String transportTypeDesc;	//�˷�����˵��
	private int whoPayShippingfee;		//�е��˷ѷ�ʽ: 1����;2���
	private double totalCash;	  		//���֧���ֽ��ܶ�������п�����Ϊ�ֽ�Ĳ���
	private double totalfee;			//��Ʒ�ܽ��
	private double adjustfee;			//�������
	private double dealPayFeeTotal;  	//������֧����� ���úϼ�,һ��Ҫ����Ǯ���������ۺ�Ǯ�����֡��Ƹ�ȯ֮�ࣩ
	private String comboInfo;			//������Ϣ
	private double couponFee;			//�ۿ��Ż���Ϣ
	private double dealPayFeePoint;		//ʵ�ʻ���֧�����
	private double dealPayFeeTicket;	//�Ƹ�ȯ֧�����	
	private String dealState;  			//����״̬: dealState 
	private String dealStateDesc;  		//����״̬˵����dealState 
	private String dealType;			//�������� 
	private double shippingfeeCalc;		//�˷Ѻϼ�˵��,�г��˷����յļ���ʽ���Ա�ÿ�ζ�������չʾ����ҿ�
	private String sellerCrm;			//�ͷ�CRM
	private String sellerName;			//��������
	private long sellerUin;				//����QQ
	/*
	SELL_TYPE_ALL:��������
	SELL_TYPE_BIN:һ�ڼ�
	SELL_TYPE_AUCTION_SINGLE:��������
	SELL_TYPE_B2C:b2c����
	*/
	private String dealTypeDesc;		//������������
	private Date createTime; 	 		//�µ�ʱ�� 
	private Date dealEndTime;			//����ʱ��
	private Date payTime;				//����ʱ��
	private Date payReturnTime;			//�����ʱ��
	private Date recvfeeReturnTime;		//�յ������ʱ��
	private Date recvfeeTime;			//����ʱ��
	private Date sellerConsignmentTime; //���ұ�Ƿ���ʱ��
	private Date lastUpdateTime;  		//����޸�ʱ�� 
	private int	hasInvoice;				//�Ƿ��ṩ��Ʊ:0��,1�� 
	private String invoiceContent;		//��Ʊ����
	private String invoiceTitle;		//��Ʊ����
	private String dealNoteType;		//�����ı�ע����
	/*
	 * ������ע���� 
		RED:��ɫ
		YELLOW:��ɫ
		GREEN:��ɫ
		BLUE:��ɫ
		PINK:�ۺ�ɫ
		UN_LABEL:δ��ע
	 */
	private String dealNote;			//������ע����
	private String dealFlag; 			//������־ 
	private String availableAction;  	//�����ڵ�ǰ״̬�¿����Ĳ��� 
	private Date expectArrivalTime;		//��������ʱ��
	private String wuliuCompany;		//������˾
	private String wuliuCode;			//��������
	private String wuliuDesc;			//������Ϣ
	
	private Vector<OrderItem> orderitems=new Vector<OrderItem>();	//������ϸ
	
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
