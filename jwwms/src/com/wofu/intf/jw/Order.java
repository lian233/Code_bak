package com.wofu.intf.jw;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
public class Order extends BusinessObject{
	private String uuid="";        //uuid
	private String orderCode="";  //�������
	private String hgBarcode="";  //��������
	private String printMsg="";   //��ӡ����ݵ�������Զ�����Ϣ
	private String orderTax="0";   //˰��
	private String platFromName=Params.platFromName; //��Դƽ̨����(OMSָ��)
	private String shopName=Params.shopName;     //��������(OMSָ��)
	private String orderStatus="WAIT_SELLER_SEND_GOODS";  //����״̬//"TRADE_CLOSED"
	private String type="һ�ڼ�";         //��������
	private String createDate="";   //�µ�ʱ��
	private String updateDate="";   //����ʱ��
	private String payTime="";      //֧��ʱ��
	private String logisticsCompanyCode="EMS"; //������˾����
	private String logisticsCompanyName="EMS"; //������˾����
	private String postPrice="";            //�ʷ�
	private String isDeliveryPay="false";       // �Ƿ��������(true/false)
	private String bunick="";              //��Ա�ǳ�
	private String invoiceName="";         //��Ʊ̧ͷ ���ŷ�Ʊ���ö��ŷָ�
	private String invoiceType="";         //��Ʊ����
	private String invoiceContent="";      //��Ʊ��ϸ
	private String sellersMessage="";      //��������
	private String buyerMessage="";        //�������
	private String merchantMessage="";        //�̼�����
	private String amountReceivable="0";   //Ӧ�ս��
	private String actualPayment="0";      //ʵ��֧��
	private String receiver="";            //�ռ�����Ϣ
	private DataRelation detail = new DataRelation("detail","com.wofu.intf.jw.detail");//������ϸ
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrdercode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getHgBarcode() {
		return hgBarcode;
	}
	public void setHgbarcode(String hgBarcode) {
		this.hgBarcode = hgBarcode;
	}
	public String getPrintMsg() {
		return printMsg;
	}
	public void setPrintmsg(String printMsg) {
		this.printMsg = printMsg;
	}
	public String getOrderTax() {
		return orderTax;
	}
	public void setOrdertax(String orderTax) {
		this.orderTax = orderTax;
	}
	public String getPlatFromName() {
		return platFromName;
	}
	public void setPlatfromname(String platFromName) {
		this.platFromName = platFromName;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopname(String shopName) {
		this.shopName = shopName;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderstatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreatedate(String createDate) {
		this.createDate = createDate;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdatedate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPaytime(String payTime) {
		this.payTime = payTime;
	}
	public String getLogisticsCompanyCode() {
		return logisticsCompanyCode;
	}
	public void setLogisticscompanycode(String logisticsCompanyCode) {
		this.logisticsCompanyCode = logisticsCompanyCode;
	}
	public String getLogisticsCompanyName() {
		return logisticsCompanyName;
	}
	public void setLogisticscompanyname(String logisticsCompanyName) {
		this.logisticsCompanyName = logisticsCompanyName;
	}
	public String getPostPrice() {
		return postPrice;
	}
	public void setPostprice(String postPrice) {
		this.postPrice = postPrice;
	}
	public String getIsDeliveryPay() {
		return isDeliveryPay;
	}
	public void setIsdeliverypay(String isDeliveryPay) {
		this.isDeliveryPay = isDeliveryPay;
	}
	public String getBunick() {
		return bunick;
	}
	public void setBunick(String bunick) {
		this.bunick = bunick;
	}
	public String getInvoiceName() {
		return invoiceName;
	}
	public void setInvoicename(String invoiceName) {
		this.invoiceName = invoiceName;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoicetype(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	public String getInvoiceContent() {
		return invoiceContent;
	}
	public void setInvoicecontent(String invoiceContent) {
		this.invoiceContent = invoiceContent;
	}
	public String getSellersMessage() {
		return sellersMessage;
	}
	public void setSellersmessage(String sellersMessage) {
		this.sellersMessage = sellersMessage;
	}
	public String getBuyerMessage() {
		return buyerMessage;
	}
	public void setBuyermessage(String buyerMessage) {
		this.buyerMessage = buyerMessage;
	}
	public String getAmountReceivable() {
		return amountReceivable;
	}
	public void setAmountreceivable(String amountReceivable) {
		this.amountReceivable = amountReceivable;
	}
	public String getActualPayment() {
		return actualPayment;
	}
	public void setActualpayment(String actualPayment) {
		this.actualPayment = actualPayment;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public DataRelation getDetail() {
		return detail;
	}
	public void setDetail(DataRelation detail) {
		this.detail = detail;
	}
	public String getMerchantMessage() {
		return merchantMessage;
	}
	public void setMerchantmessage(String merchantMessage) {
		this.merchantMessage = merchantMessage;
	}
	
	
}
