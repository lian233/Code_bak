package com.wofu.ecommerce.suning;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * ������
 *
 */
public class Order extends BusinessObject{
	
	private String orderCode = "";//�������
	private String orderLineStatus = "";//����״̬
	private String message = ""; //�������
	private String remark =""; //��ע
	private String label = "" ; //5��̶���־ 1��ɫ2��ɫ3��ɫ4��ɫ5��ɫ
	private Date orderSaleTime; //��������ʱ��
	
	private float totalFee = 0f ;//������Ʒ�ܽ��
	private float goodsMoney =0.0f;//�������̼�Ӧ�ս��
	//private float deductAmount=0.0f;//����֧��������Żݽ��
	private float totalBarginPrice=0.0f;//�˿���Ϊ����֧���ֽ� 
	private float postage=0.0f;//���֧���ʷ� 
	private float giftCertMoney=0.0f;//���֧������� 
	//private float giftCardMoney=0.0f; //���֧����Ʒ�����
	//�ջ�����Ϣ
	private String customerName="";//�ջ�������
	private String customerAddress="";//�ջ���ַ
	//private String consigneeAddrStte="";//�ջ�����
	private String provinceCode="";//�ջ�ʡ��
	private String cityCode="";//�ջ���
	private String districtCode="";//�ջ���
	private String consigneePostcode="";//�ʱ�
	//private String consigneeTel="";//�̶��绰
	private String mobNum="";//�ƶ��绰
	private String evaluationMark="";   //���۱�ʶ ��1������ 0δ����
	private String returnOrderFlag="";//�˻���־
	private String userName="";//��������ʺ�-дbuyernick
	private String sellerOrdRemark="";//���ұ�ע
	private String buyerOrdRemark="";//��ұ�ע
	//��Ʊ��Ϣ
	private String invoiceHead="";//��Ʊ̧ͷ
	private String invoiceType="";//��Ʊ��־    //��Ʊ���� -- ��ֵ������ͨ��01��ֵ 02��ͨ��
	private String invoice="";//��Ʊ����
	private String vatTaxpayerNumber="";//��ֵ˰��˰��ʶ���
	
	private DataRelation orderItemList =new DataRelation("orderItemList","com.wofu.ecommerce.suning.OrderItem");
	//private ArrayList<OperateInfo> operateInfoList = new ArrayList<OperateInfo>() ;//���������б�
	//��Ʊ
	//private ArrayList<String> invoiceItem = new ArrayList<String>() ;
	
	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOrderLineStatus() {
		return orderLineStatus;
	}

	public void setOrderLineStatus(String orderLineStatus) {
		this.orderLineStatus = orderLineStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public float getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(float totalFee) {
		this.totalFee = totalFee;
	}

	public float getGoodsMoney() {
		return goodsMoney;
	}

	public void setGoodsMoney(float goodsMoney) {
		this.goodsMoney = goodsMoney;
	}

	public float getTotalBarginPrice() {
		return totalBarginPrice;
	}

	public void setTotalBarginPrice(float totalBarginPrice) {
		this.totalBarginPrice = totalBarginPrice;
	}

	public float getPostage() {
		return postage;
	}

	public void setPostage(float postage) {
		this.postage = postage;
	}

	public float getGiftCertMoney() {
		return giftCertMoney;
	}

	public void setGiftCertMoney(float giftCertMoney) {
		this.giftCertMoney = giftCertMoney;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getConsigneePostcode() {
		return consigneePostcode;
	}

	public void setConsigneePostcode(String consigneePostcode) {
		this.consigneePostcode = consigneePostcode;
	}

	public String getMobNum() {
		return mobNum;
	}

	public void setMobNum(String mobNum) {
		this.mobNum = mobNum;
	}

	public DataRelation getOrderItemList() {
		return orderItemList;
	}

	public void setOrderItemList(DataRelation orderItemList) {
		this.orderItemList = orderItemList;
	}


	public Date getOrderSaleTime() {
		return orderSaleTime;
	}

	public void setOrderSaleTime(Date orderSaleTime) {
		this.orderSaleTime = orderSaleTime;
	}

	public String getEvaluationMark() {
		return evaluationMark;
	}

	public void setEvaluationMark(String evaluationMark) {
		this.evaluationMark = evaluationMark;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getReturnOrderFlag() {
		return returnOrderFlag;
	}

	public void setReturnOrderFlag(String returnOrderFlag) {
		this.returnOrderFlag = returnOrderFlag;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSellerOrdRemark() {
		return sellerOrdRemark;
	}

	public void setSellerOrdRemark(String sellerOrdRemark) {
		this.sellerOrdRemark = sellerOrdRemark;
	}

	public String getBuyerOrdRemark() {
		return buyerOrdRemark;
	}

	public void setBuyerOrdRemark(String buyerOrdRemark) {
		this.buyerOrdRemark = buyerOrdRemark;
	}

	public String getInvoiceHead() {
		return invoiceHead;
	}

	public void setInvoiceHead(String invoiceHead) {
		this.invoiceHead = invoiceHead;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public String getVatTaxpayerNumber() {
		return vatTaxpayerNumber;
	}

	public void setVatTaxpayerNumber(String vatTaxpayerNumber) {
		this.vatTaxpayerNumber = vatTaxpayerNumber;
	}
	
	
	
}
