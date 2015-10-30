package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class DvdOrder {
	
	/**
	* 订单编号
	*/
	
	private String order_id;
	
	/**
	* 订单状态编码
	*/
	
	private vipapis.common.OrderStatus status;
	
	/**
	* 收货人
	*/
	
	private String buyer;
	
	/**
	* 收货地址
	*/
	
	private String address;
	
	/**
	* 手机号码
	*/
	
	private String mobile;
	
	/**
	* 联系电话
	*/
	
	private String tel;
	
	/**
	* 邮政编码
	*/
	
	private String postcode;
	
	/**
	* 城市
	*/
	
	private String city;
	
	/**
	* 省份
	*/
	
	private String province;
	
	/**
	* 国家代码
	*/
	
	private String country_id;
	
	/**
	* 发票抬头
	*/
	
	private String invoice;
	
	/**
	* 快递金额（计算 整张出库单商品金额总和+快递费用 == 订单金额）
	*/
	
	private String carriage;
	
	/**
	* 备注
	*/
	
	private String remark;
	
	/**
	* 期望收货时间
	*/
	
	private String transport_day;
	
	/**
	* 供应商ID
	*/
	
	private Integer vendor_id;
	
	/**
	* 供应商名称
	*/
	
	private String vendor_name;
	
	/**
	* 促销优惠金额
	*/
	
	private String ex_fav_money;
	
	/**
	* 优惠金额
	*/
	
	private String favourable_money;
	
	/**
	* 整张出库单商品金额总和(计算发票金额 == 整张出库单商品金额总和 + 快递费用 - 优惠金额 - 促销优惠金额)
	*/
	
	private String product_money;
	
	/**
	* 订单下单时间
	*/
	
	private String add_time;
	
	/**
	* po号
	*/
	
	private String po_id;
	
	/**
	* 区/县
	*/
	
	private String county;
	
	public String getOrder_id(){
		return this.order_id;
	}
	
	public void setOrder_id(String value){
		this.order_id = value;
	}
	public vipapis.common.OrderStatus getStatus(){
		return this.status;
	}
	
	public void setStatus(vipapis.common.OrderStatus value){
		this.status = value;
	}
	public String getBuyer(){
		return this.buyer;
	}
	
	public void setBuyer(String value){
		this.buyer = value;
	}
	public String getAddress(){
		return this.address;
	}
	
	public void setAddress(String value){
		this.address = value;
	}
	public String getMobile(){
		return this.mobile;
	}
	
	public void setMobile(String value){
		this.mobile = value;
	}
	public String getTel(){
		return this.tel;
	}
	
	public void setTel(String value){
		this.tel = value;
	}
	public String getPostcode(){
		return this.postcode;
	}
	
	public void setPostcode(String value){
		this.postcode = value;
	}
	public String getCity(){
		return this.city;
	}
	
	public void setCity(String value){
		this.city = value;
	}
	public String getProvince(){
		return this.province;
	}
	
	public void setProvince(String value){
		this.province = value;
	}
	public String getCountry_id(){
		return this.country_id;
	}
	
	public void setCountry_id(String value){
		this.country_id = value;
	}
	public String getInvoice(){
		return this.invoice;
	}
	
	public void setInvoice(String value){
		this.invoice = value;
	}
	public String getCarriage(){
		return this.carriage;
	}
	
	public void setCarriage(String value){
		this.carriage = value;
	}
	public String getRemark(){
		return this.remark;
	}
	
	public void setRemark(String value){
		this.remark = value;
	}
	public String getTransport_day(){
		return this.transport_day;
	}
	
	public void setTransport_day(String value){
		this.transport_day = value;
	}
	public Integer getVendor_id(){
		return this.vendor_id;
	}
	
	public void setVendor_id(Integer value){
		this.vendor_id = value;
	}
	public String getVendor_name(){
		return this.vendor_name;
	}
	
	public void setVendor_name(String value){
		this.vendor_name = value;
	}
	public String getEx_fav_money(){
		return this.ex_fav_money;
	}
	
	public void setEx_fav_money(String value){
		this.ex_fav_money = value;
	}
	public String getFavourable_money(){
		return this.favourable_money;
	}
	
	public void setFavourable_money(String value){
		this.favourable_money = value;
	}
	public String getProduct_money(){
		return this.product_money;
	}
	
	public void setProduct_money(String value){
		this.product_money = value;
	}
	public String getAdd_time(){
		return this.add_time;
	}
	
	public void setAdd_time(String value){
		this.add_time = value;
	}
	public String getPo_id(){
		return this.po_id;
	}
	
	public void setPo_id(String value){
		this.po_id = value;
	}
	public String getCounty(){
		return this.county;
	}
	
	public void setCounty(String value){
		this.county = value;
	}
	
}