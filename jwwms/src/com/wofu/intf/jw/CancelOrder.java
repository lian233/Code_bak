package com.wofu.intf.jw;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
public class CancelOrder extends BusinessObject{
	private String uuid="";        //uuid
	private String orderCode="";  //配货单号
	private String hgBarcode="";  //海关条码
	private String printMsg="";   //打印到快递单上面的自定义信息
	private String orderTax="0";   //税费
	private String platFromName=Params.platFromName; //来源平台名称(OMS指定)
	private String shopName=Params.shopName;     //店铺名称(OMS指定)
	private String orderStatus="TRADE_CLOSED";  //交易状态//""
	private String type="一口价";         //订单类型
	private String createDate="";   //下单时间
	private String updateDate="";   //更新时间
	private String payTime="";      //支付时间
	private String logisticsCompanyCode="EMS"; //物流公司编码
	private String logisticsCompanyName="EMS"; //物流公司名称
	private String postPrice="";            //邮费
	private String isDeliveryPay="false";       // 是否货到付款(true/false)
	private String bunick="";              //会员昵称
	private String invoiceName="";         //发票抬头 多张发票，用逗号分隔
	private String invoiceType="";         //发票内型
	private String invoiceContent="";      //发票明细
	private String sellersMessage="";      //卖家留言
	private String buyerMessage="";        //买家留言
	private String merchantMessage="";        //商家留言
	private String amountReceivable="0";   //应收金额
	private String actualPayment="0";      //实际支付
	private String receiver="";            //收件人信息
	private DataRelation detail = new DataRelation("detail","com.wofu.intf.jw.detail");//订单明细
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
