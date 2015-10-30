package com.wofu.ecommerce.wqb;
import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
public class Order extends BusinessObject{
	private String City;
	private String County;
	private String BillNo;//出库单编号
	private String OrderId;//系统订单编号
	private String OrderNo;//网店订单号
	private String FxsNo;//分销商
	private String UserCode;//渠道商代码  u8编码
	private String CkNo;//仓库编号
	private String EshopCode;//网店授权号
	private String EShopName;//网店名称
	private String C_UserName;//买家ID
	private String C_Name;//收件人信息
	private String Province;//省份
	private String Address;//地址
	private String MobiTel;//手机号码
	private String Phone;//电话号码
	private String PostCode;//邮政编号
	private String C_Remark;//买家留言
	private String O_Remark;//卖家备注
	private DataRelation receiptSpec = new DataRelation("ReceiptSpec","com.wofu.ecommerce.wqb.ReceiptSpec");//订单发票信息
	private float stockOrderSumPrice;//实付出库单金额
	private float Exp_Fee;//分销商付给仓库的订单运费
	private float EShop_Exp_Price;//实付顾客的订单运费
	private String Exp_Name;//物流公司编码
	private String Exp_Code;//物流公司标签
	private String Exp_Cod;//是否货到付款订单;
	private float Cod_Fee;//货到付款手续费
	private float Exp_Codfee;//货到付款代收运费
	private DataRelation proSpec = new DataRelation("ProSpec","com.wofu.ecommerce.wqb.ProSpec");//商品明细
	private String StockOrder_Flag;//订单状态
	private Date AddTime;//订单创建时间
	private Date Exp_Time;//订单发货时间
	private String StockOrder_CancelFlag;//订单出库状态
	private String Fxs_Type;//分销商类型
	private String IsSplit;//是否有拆分订单
	private String Is_Receipt;//是否有发票 有、无
	private String Scan_Check;//出库单是否校验 默认：0， 1：是， 2 异常
	private String Send_Sms;//发送短信有、无
	private Date SaleStock_Time;//生成出库单时间
	private String Total_Weight;//商品重量
	private String Pf_Order;//订单类型  批发订单、正常正常
	private String Order_Refund;//顾客申请退款
	public String getOrderId() {
		return OrderId;
	}
	public void setOrderId(String orderId) {
		OrderId = orderId;
	}
	public String getOrderNo() {
		return OrderNo;
	}
	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}
	public String getFxsNo() {
		return FxsNo;
	}
	public void setFxsNo(String fxsNo) {
		FxsNo = fxsNo;
	}
	public String getCkNo() {
		return CkNo;
	}
	public void setCkNo(String ckNo) {
		CkNo = ckNo;
	}
	public String getEshopCode() {
		return EshopCode;
	}
	public void setEshopCode(String eshopCode) {
		EshopCode = eshopCode;
	}
	public String getEShopName() {
		return EShopName;
	}
	public void setEShopName(String shopName) {
		EShopName = shopName;
	}
	public String getC_UserName() {
		return C_UserName;
	}
	public void setC_UserName(String userName) {
		C_UserName = userName;
	}
	public String getProvince() {
		return Province;
	}
	public void setProvince(String province) {
		Province = province;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getMobiTel() {
		return MobiTel;
	}
	public void setMobiTel(String mobiTel) {
		MobiTel = mobiTel;
	}
	public String getPhone() {
		return Phone;
	}
	public void setPhone(String phone) {
		Phone = phone;
	}
	public String getPostCode() {
		return PostCode;
	}
	public void setPostCode(String postCode) {
		PostCode = postCode;
	}
	public String getC_Remark() {
		return C_Remark;
	}
	public void setC_Remark(String remark) {
		C_Remark = remark;
	}
	public DataRelation getReceiptSpec() {
		return receiptSpec;
	}
	public void setReceiptSpec(DataRelation receiptSpec) {
		this.receiptSpec = receiptSpec;
	}
	public float getStockOrderSumPrice() {
		return stockOrderSumPrice;
	}
	public void setStockOrderSumPrice(float stockOrderSumPrice) {
		this.stockOrderSumPrice = stockOrderSumPrice;
	}
	public float getExp_Fee() {
		return Exp_Fee;
	}
	public void setExp_Fee(float exp_Fee) {
		Exp_Fee = exp_Fee;
	}
	public float getEShop_Exp_Price() {
		return EShop_Exp_Price;
	}
	public void setEShop_Exp_Price(float shop_Exp_Price) {
		EShop_Exp_Price = shop_Exp_Price;
	}
	public String getExp_Name() {
		return Exp_Name;
	}
	public void setExp_Name(String exp_Name) {
		Exp_Name = exp_Name;
	}
	public String getExp_Code() {
		return Exp_Code;
	}
	public void setExp_Code(String exp_Code) {
		Exp_Code = exp_Code;
	}
	public String getExp_Cod() {
		return Exp_Cod;
	}
	public void setExp_Cod(String exp_Cod) {
		Exp_Cod = exp_Cod;
	}
	public float getCod_Fee() {
		return Cod_Fee;
	}
	public void setCod_Fee(float cod_Fee) {
		Cod_Fee = cod_Fee;
	}
	public float getExp_Codfee() {
		return Exp_Codfee;
	}
	public void setExp_Codfee(float exp_Codfee) {
		Exp_Codfee = exp_Codfee;
	}
	public DataRelation getProSpec() {
		return proSpec;
	}
	public void setProSpec(DataRelation proSpec) {
		this.proSpec = proSpec;
	}
	public String getStockOrder_Flag() {
		return StockOrder_Flag;
	}
	public void setStockOrder_Flag(String stockOrder_Flag) {
		StockOrder_Flag = stockOrder_Flag;
	}
	public Date getAddTime() {
		return AddTime;
	}
	public void setAddTime(Date addTime) {
		AddTime = addTime;
	}

	public String getStockOrder_CancelFlag() {
		return StockOrder_CancelFlag;
	}
	public void setStockOrder_CancelFlag(String stockOrder_CancelFlag) {
		StockOrder_CancelFlag = stockOrder_CancelFlag;
	}
	public String getFxs_Type() {
		return Fxs_Type;
	}
	public void setFxs_Type(String fxs_Type) {
		Fxs_Type = fxs_Type;
	}
	public String getIsSplit() {
		return IsSplit;
	}
	public void setIsSplit(String isSplit) {
		IsSplit = isSplit;
	}
	public Date getExp_Time() {
		return Exp_Time;
	}
	public void setExp_Time(Date exp_Time) {
		Exp_Time = exp_Time;
	}
	public String getIs_Receipt() {
		return Is_Receipt;
	}
	public void setIs_Receipt(String is_Receipt) {
		Is_Receipt = is_Receipt;
	}
	public String getScan_Check() {
		return Scan_Check;
	}
	public void setScan_Check(String scan_Check) {
		Scan_Check = scan_Check;
	}
	public String getSend_Sms() {
		return Send_Sms;
	}
	public void setSend_Sms(String send_Sms) {
		Send_Sms = send_Sms;
	}
	public Date getSaleStock_Time() {
		return SaleStock_Time;
	}
	public void setSaleStock_Time(Date saleStock_Time) {
		SaleStock_Time = saleStock_Time;
	}
	public String getTotal_Weight() {
		return Total_Weight;
	}
	public void setTotal_Weight(String total_Weight) {
		Total_Weight = total_Weight;
	}
	public String getPf_Order() {
		return Pf_Order;
	}
	public void setPf_Order(String pf_Order) {
		Pf_Order = pf_Order;
	}
	public String getOrder_Refund() {
		return Order_Refund;
	}
	public void setOrder_Refund(String order_Refund) {
		Order_Refund = order_Refund;
	}
	public String getC_Name() {
		return C_Name;
	}
	public void setC_Name(String name) {
		C_Name = name;
	}
	public String getO_Remark() {
		return O_Remark;
	}
	public void setO_Remark(String remark) {
		O_Remark = remark;
	}
	public String getBillNo() {
		return BillNo;
	}
	public void setBillNo(String billNo) {
		BillNo = billNo;
	}
	public String getCity() {
		return City;
	}
	public void setCity(String city) {
		City = city;
	}
	public String getCounty() {
		return County;
	}
	public void setCounty(String county) {
		County = county;
	}
	public String getUserCode() {
		return UserCode;
	}
	public void setUserCode(String userCode) {
		UserCode = userCode;
	}
	
	
	
	
	
	
	
	
	
}
