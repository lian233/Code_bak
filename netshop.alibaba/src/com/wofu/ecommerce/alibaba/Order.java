package com.wofu.ecommerce.alibaba;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Order extends BusinessObject{
	private String sellerRateStatus;   //��������״̬��4-�����ۣ�5-δ����
	private String buyerRateStatus;    //�������״̬��4-�����ۣ�5-δ����
	private long id;              //����ID
	private String alipayTradeId;   //֧�������׺�
	private String sellerAlipayId;   //����֧����ID
	private String buyerMemberId;    //��һ�Ա��¼��������Աid

	private Integer tradeType;    //1���������ף�2��Ԥ����ף�3��ETC�����յ����ף�4����ʱ���˽��ף�5�����Ͻ�ȫ���ף�6��ͳһ�������̣�7���ֽ׶ν��ף�8����������ף�9������ƾ֤֧������
	private String status;        //�������׹���5��״̬�� waitbuyerpay(�ȴ���Ҹ���), waitsellersend(�ȴ����ҷ���), waitbuyerreceive(�ȴ�����ջ�), success(���׳ɹ�), cancel(����ȡ����ΥԼ��Ƚ������); ��ʱ���˽��׹���4��״̬�� waitbuyerpay(�ȴ���Ҹ���), waitsellersend(�ȴ����ҷ���),(���׳ɹ�), cancel(����ȡ����ΥԼ��Ƚ������) �ֽ׶ν��װ�����waitbuyerpay(�ȴ���Ҹ���), waitsellersend(�ȴ����ҷ���), waitbuyerreceive(�ȴ�����ջ�), success(���׳ɹ�), cancel(����ȡ����ΥԼ��Ƚ������),waitselleract(�ȴ����Ҳ���),waitbuyerconfirmaction(�ȴ����ȷ�ϲ���),waitsellerpush(�ȴ������ƽ�)
	private String gmtCreate;     //����µ�ʱ�䣬����������ʱ��
	
	private String gmtPayment;   //��Ҹ���ʱ��
	//private Integer sourceId;    //��Ʒ��Ϣ����-��ƷID
	
	private String[] productPic; //��Ʒ��Ϣ����-��Ʒ����ͼƬ��URL��ַ
	private Integer productName; //��Ʒ��Ϣ����-��Ʒ����
	private Integer quantity;    //��Ʒ��Ϣ����-�����и���Ʒ�Ĺ�������
	
	private String[] specInfo;   //������Ϣ
	private String   specName;   //��������
	private String   specValue;  //����ֵ
	private String   specUnit;   //���Ե�λ
	private double   carriage;   //�˷ѣ���λ����
	private double   discount;   //�Ǽۻ��ۿۣ��ۿ�Ϊ��������λ:�֣�
	private double   sumPayment; //��Ʒ�ܼ�+�˷�=ʵ�����λ:�֣�
	private String   couponAmount; //�ּ�ȯʵ�����ѽ���λ:�֣�
	private String   toArea;      //�ջ������ڵ���
	private String   toPost;      //�ջ����ʱ�
	private String   toFullName;  //�ջ�������
	private String   toMobile;    //�ջ����ֻ�
	private String   toPhone;     //�ջ��˵绰
	
	private String bankAndAccount;  //��Ʊ��Ϣ����-���м��˻�
	private String buyerAlipayId;  //���֧����ID
	private String buyerCompanyName;  //��ҹ�˾��
	private String buyerFeedback;  //�������
	private String buyerMobile;  //����ֻ���
	private String buyerPhone;  //��ҵ绰
	
	private String closeReason;  //�رս�������
	private String codActualFee;  //cod���׵�ʵ�����λ����
	private String codAudit;  //�Ƿ�COD������������ɹ�
	private String codBuyerFee;  //��ҳе��ķ����
	private String codBuyerInitFee;  //��ҳе��ķ���ѳ�ʼֵ����λ����
	private String codFee;  //cod����ѣ���λ����
	private String codFeeDividend;  //cod���ҷ���
	private String codGmtSign;  //���ǩ��ʱ��
	private String codInitFee;  //cod����ѳ�ʼֵ����λ����
	private String codSellerFee;  //���ҳе��ķ���ѣ���λ����
	private String codStatus;  //COD����״̬��ȡֵ��Χ��0(��ʼֵ),20(�ӵ�),-20(���ӵ�),2(�ӵ���ʱ),30(���ճɹ�),-30(����ʧ��),3(���ճ�ʱ),100(ǩ�ճɹ�),-100(ǩ��ʧ��),10(�����Ⱥ��͸�������˾),-1(�û�ȡ����������)
	private String entryCodStatus;  //������ϸ��������״̬��ȡֵ��Χͬ����codStatus
	private String entryDiscount;  //������ϸ�ۿ�
	private String entryStatus;  //������ϸ״̬��ȡֵ��Χͬ����status
	private String fromAddress;  //�����ֵ���ַ
	private String fromArea;  //������
	private String fromCity;  //������
	private String fromContact;  //������ϵ��
	private String fromMobile;  //������ϵ�ֻ�
	private String fromPhone;  //������ϵ�绰
	private String fromPost;  //������ַ�ʱ�
	private String fromProvince;  //����ʡ
	private String gmtCompleted;  //�������ʱ��
	private String gmtGoodsSend;  //���ҷ���ʱ��
	private String gmtModified;  //��������޸�ʱ��
	
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
