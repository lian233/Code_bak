package com.wofu.ecommerce.suning;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * 订单类
 *
 */
public class Order extends BusinessObject{
	
	private String orderCode = "";//订单编号
	private String orderLineStatus = "";//订单状态
	private String message = ""; //买家留言
	private String remark =""; //备注
	private String label = "" ; //5类固定标志 1红色2黄色3绿色4蓝色5紫色
	private Date orderSaleTime; //订单创建时间
	
	private float totalFee = 0f ;//订单商品总金额
	private float goodsMoney =0.0f;//本订单商家应收金额
	//private float deductAmount=0.0f;//网银支付满额减优惠金额
	private float totalBarginPrice=0.0f;//顾客需为订单支付现金 
	private float postage=0.0f;//买家支付邮费 
	private float giftCertMoney=0.0f;//买家支付礼卷金额 
	//private float giftCardMoney=0.0f; //买家支付礼品卡金额
	//收货人信息
	private String customerName="";//收货人姓名
	private String customerAddress="";//收货地址
	//private String consigneeAddrStte="";//收货国家
	private String provinceCode="";//收货省份
	private String cityCode="";//收货市
	private String districtCode="";//收货区
	private String consigneePostcode="";//邮编
	//private String consigneeTel="";//固定电话
	private String mobNum="";//移动电话
	private String evaluationMark="";   //评价标识 ，1已评价 0未评价
	private String returnOrderFlag="";//退货标志
	private String userName="";//买家苏宁帐号-写buyernick
	private String sellerOrdRemark="";//卖家备注
	private String buyerOrdRemark="";//买家备注
	//发票信息
	private String invoiceHead="";//发票抬头
	private String invoiceType="";//发票标志    //发票类型 -- 增值还是普通（01增值 02普通）
	private String invoice="";//发票内容
	private String vatTaxpayerNumber="";//增值税纳税人识别号
	
	private DataRelation orderItemList =new DataRelation("orderItemList","com.wofu.ecommerce.suning.OrderItem");
	//private ArrayList<OperateInfo> operateInfoList = new ArrayList<OperateInfo>() ;//订单操作列表
	//发票
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
