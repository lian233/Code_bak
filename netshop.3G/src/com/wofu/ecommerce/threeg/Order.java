package com.wofu.ecommerce.threeg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
	
	private String OrderId;
	private String OrderStatus;
	private String orderMoneyStatus;
	private String UserName;
	private String phone;
	private String address;
	private String provinceId;
	private String provinceName;
	private String cityId;
	private String cityName;
	private String areaid;
	private String areaName;
	private String createTime;
	private String modified;   //更新时间
	private String payMode;  //1是在线支付	2是货到付款
	private String totalMoney; //订单金额 不包含运费
	private String ScoreMoney; //积分抵用金额
	private String PayFee;		//到付手续费
	private String ShippingFee;	//运费
	private String LogisticOperator; //快递公司
	private String LogisticId;//快递单号
	private String Remark;		//备注
	private String OrderRemarks;//订单备注
	
	private List<OrderItem> orderitems=new ArrayList<OrderItem>();
	
	public List<OrderItem> getOrderitems() {
		return orderitems;
	}
	public void setOrderitems(List<OrderItem> orderitems) {
		this.orderitems = orderitems;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAreaid() {
		return areaid;
	}
	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getLogisticId() {
		return LogisticId;
	}
	public void setLogisticId(String logisticId) {
		LogisticId = logisticId;
	}
	public String getLogisticOperator() {
		return LogisticOperator;
	}
	public void setLogisticOperator(String logisticOperator) {
		LogisticOperator = logisticOperator;
	}
	public String getOrderId() {
		return OrderId;
	}
	public void setOrderId(String orderId) {
		OrderId = orderId;
	}
	public String getOrderMoneyStatus() {
		return orderMoneyStatus;
	}
	public void setOrderMoneyStatus(String orderMoneyStatus) {
		this.orderMoneyStatus = orderMoneyStatus;
	}
	public String getOrderStatus() {
		return OrderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		OrderStatus = orderStatus;
	}
	public String getPayFee() {
		return PayFee;
	}
	public void setPayFee(String payFee) {
		PayFee = payFee;
	}
	public String getPayMode() {
		return payMode;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getRemark() {
		return Remark;
	}
	public void setRemark(String remark) {
		Remark = remark;
	}
	public String getScoreMoney() {
		return ScoreMoney;
	}
	public void setScoreMoney(String scoreMoney) {
		ScoreMoney = scoreMoney;
	}
	public String getShippingFee() {
		return ShippingFee;
	}
	public void setShippingFee(String shippingFee) {
		ShippingFee = shippingFee;
	}
	public String getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getOrderRemarks() {
		return OrderRemarks;
	}
	public void setOrderRemarks(String orderRemarks) {
		OrderRemarks = orderRemarks;
	}
	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	
}
