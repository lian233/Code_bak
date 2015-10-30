package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class ExportOrderInfo {
	
	/**
	* 订单号码
	* @sampleValue order_id 
	*/
	
	private String order_id;
	
	/**
	* 订单商品状态
	* @sampleValue state 
	*/
	
	private String state;
	
	/**
	* 仓库名称
	* @sampleValue warehouse_name 
	*/
	
	private String warehouse_name;
	
	/**
	* EBS分仓代码
	* @sampleValue ebs_warehouse 
	*/
	
	private String ebs_warehouse;
	
	/**
	* B2C分仓代码
	* @sampleValue b2c_warehouse 
	*/
	
	private String b2c_warehouse;
	
	/**
	* 客户类型
	* @sampleValue user_type 
	*/
	
	private Integer user_type;
	
	/**
	* 客户名称
	* @sampleValue user_name 
	*/
	
	private String user_name;
	
	/**
	* 用户下单ID
	* @sampleValue buyer_id 
	*/
	
	private Integer buyer_id;
	
	/**
	* 收货地址
	* @sampleValue address 
	*/
	
	private String address;
	
	/**
	* 收货人
	* @sampleValue buyer 
	*/
	
	private String buyer;
	
	/**
	* 区域编码
	* @sampleValue area_id 
	*/
	
	private String area_id;
	
	/**
	* 邮政编码
	* @sampleValue postcode 
	*/
	
	private String postcode;
	
	/**
	* 城市
	* @sampleValue city 
	*/
	
	private String city;
	
	/**
	* 省份
	* @sampleValue province 
	*/
	
	private String province;
	
	/**
	* 国家代码
	* @sampleValue country_id 
	*/
	
	private String country_id;
	
	/**
	* 电话号码
	* @sampleValue tel 
	*/
	
	private String tel;
	
	/**
	* 手机号码
	* @sampleValue mobile 
	*/
	
	private String mobile;
	
	/**
	* 支付类型
	* @sampleValue pay_type 
	*/
	
	private String pay_type;
	
	/**
	* POS机刷卡标识
	* @sampleValue pos 
	*/
	
	private Integer pos;
	
	/**
	* 客户要求送货时间
	* @sampleValue transport_day 
	*/
	
	private String transport_day;
	
	/**
	* 备注
	* @sampleValue remark 
	*/
	
	private String remark;
	
	/**
	* 类型说明
	* @sampleValue order_type 
	*/
	
	private String order_type;
	
	/**
	* 订单类型
	* @sampleValue vipclub 
	*/
	
	private String vipclub;
	
	/**
	* 发票抬头
	* @sampleValue invoice 
	*/
	
	private String invoice;
	
	/**
	* 整张出库单商品金额总和
	* @sampleValue goods_money 
	*/
	
	private String goods_money;
	
	/**
	* 应收金额=单据金额x折扣比例
	* @sampleValue money 
	*/
	
	private String money;
	
	/**
	* 折扣
	* @sampleValue aigo 
	*/
	
	private String aigo;
	
	/**
	* 优惠金额
	* @sampleValue favourable_money 
	*/
	
	private String favourable_money;
	
	/**
	* 促销优惠金额
	* @sampleValue ex_fav_money 
	*/
	
	private String ex_fav_money;
	
	/**
	* 电子货币包
	* @sampleValue surplus 
	*/
	
	private String surplus;
	
	/**
	* 运费
	* @sampleValue carriage 
	*/
	
	private String carriage;
	
	/**
	* 运单号码
	* @sampleValue transport_no 
	*/
	
	private String transport_no;
	
	/**
	* 承运商编码
	* @sampleValue carrier_code 
	*/
	
	private String carrier_code;
	
	/**
	* 承运商名称
	* @sampleValue carrier 
	*/
	
	private String carrier;
	
	/**
	* 配送信息 运单详情
	* @sampleValue transport_detail 
	*/
	
	private String transport_detail;
	
	/**
	* b2c物流状态编码
	* @sampleValue b2c_transport_code 
	*/
	
	private Integer b2c_transport_code;
	
	/**
	* 运送方式说明
	* @sampleValue transport_id 
	*/
	
	private String transport_id;
	
	/**
	* 承载物类型
	* @sampleValue transport_type 
	*/
	
	private String transport_type;
	
	/**
	* 供应商代码
	* @sampleValue vendor_code 
	*/
	
	private String vendor_code;
	
	/**
	* 供应商ID
	* @sampleValue vendor_id 
	*/
	
	private Integer vendor_id;
	
	/**
	* 供应商名称
	* @sampleValue vendor_name 
	*/
	
	private String vendor_name;
	
	/**
	* 品牌名称
	* @sampleValue brand_name 
	*/
	
	private String brand_name;
	
	/**
	* 商品信息
	* @sampleValue goods_list 
	*/
	
	private List<vipapis.delivery.ExportProduct> goods_list;
	
	public String getOrder_id(){
		return this.order_id;
	}
	
	public void setOrder_id(String value){
		this.order_id = value;
	}
	public String getState(){
		return this.state;
	}
	
	public void setState(String value){
		this.state = value;
	}
	public String getWarehouse_name(){
		return this.warehouse_name;
	}
	
	public void setWarehouse_name(String value){
		this.warehouse_name = value;
	}
	public String getEbs_warehouse(){
		return this.ebs_warehouse;
	}
	
	public void setEbs_warehouse(String value){
		this.ebs_warehouse = value;
	}
	public String getB2c_warehouse(){
		return this.b2c_warehouse;
	}
	
	public void setB2c_warehouse(String value){
		this.b2c_warehouse = value;
	}
	public Integer getUser_type(){
		return this.user_type;
	}
	
	public void setUser_type(Integer value){
		this.user_type = value;
	}
	public String getUser_name(){
		return this.user_name;
	}
	
	public void setUser_name(String value){
		this.user_name = value;
	}
	public Integer getBuyer_id(){
		return this.buyer_id;
	}
	
	public void setBuyer_id(Integer value){
		this.buyer_id = value;
	}
	public String getAddress(){
		return this.address;
	}
	
	public void setAddress(String value){
		this.address = value;
	}
	public String getBuyer(){
		return this.buyer;
	}
	
	public void setBuyer(String value){
		this.buyer = value;
	}
	public String getArea_id(){
		return this.area_id;
	}
	
	public void setArea_id(String value){
		this.area_id = value;
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
	public String getTel(){
		return this.tel;
	}
	
	public void setTel(String value){
		this.tel = value;
	}
	public String getMobile(){
		return this.mobile;
	}
	
	public void setMobile(String value){
		this.mobile = value;
	}
	public String getPay_type(){
		return this.pay_type;
	}
	
	public void setPay_type(String value){
		this.pay_type = value;
	}
	public Integer getPos(){
		return this.pos;
	}
	
	public void setPos(Integer value){
		this.pos = value;
	}
	public String getTransport_day(){
		return this.transport_day;
	}
	
	public void setTransport_day(String value){
		this.transport_day = value;
	}
	public String getRemark(){
		return this.remark;
	}
	
	public void setRemark(String value){
		this.remark = value;
	}
	public String getOrder_type(){
		return this.order_type;
	}
	
	public void setOrder_type(String value){
		this.order_type = value;
	}
	public String getVipclub(){
		return this.vipclub;
	}
	
	public void setVipclub(String value){
		this.vipclub = value;
	}
	public String getInvoice(){
		return this.invoice;
	}
	
	public void setInvoice(String value){
		this.invoice = value;
	}
	public String getGoods_money(){
		return this.goods_money;
	}
	
	public void setGoods_money(String value){
		this.goods_money = value;
	}
	public String getMoney(){
		return this.money;
	}
	
	public void setMoney(String value){
		this.money = value;
	}
	public String getAigo(){
		return this.aigo;
	}
	
	public void setAigo(String value){
		this.aigo = value;
	}
	public String getFavourable_money(){
		return this.favourable_money;
	}
	
	public void setFavourable_money(String value){
		this.favourable_money = value;
	}
	public String getEx_fav_money(){
		return this.ex_fav_money;
	}
	
	public void setEx_fav_money(String value){
		this.ex_fav_money = value;
	}
	public String getSurplus(){
		return this.surplus;
	}
	
	public void setSurplus(String value){
		this.surplus = value;
	}
	public String getCarriage(){
		return this.carriage;
	}
	
	public void setCarriage(String value){
		this.carriage = value;
	}
	public String getTransport_no(){
		return this.transport_no;
	}
	
	public void setTransport_no(String value){
		this.transport_no = value;
	}
	public String getCarrier_code(){
		return this.carrier_code;
	}
	
	public void setCarrier_code(String value){
		this.carrier_code = value;
	}
	public String getCarrier(){
		return this.carrier;
	}
	
	public void setCarrier(String value){
		this.carrier = value;
	}
	public String getTransport_detail(){
		return this.transport_detail;
	}
	
	public void setTransport_detail(String value){
		this.transport_detail = value;
	}
	public Integer getB2c_transport_code(){
		return this.b2c_transport_code;
	}
	
	public void setB2c_transport_code(Integer value){
		this.b2c_transport_code = value;
	}
	public String getTransport_id(){
		return this.transport_id;
	}
	
	public void setTransport_id(String value){
		this.transport_id = value;
	}
	public String getTransport_type(){
		return this.transport_type;
	}
	
	public void setTransport_type(String value){
		this.transport_type = value;
	}
	public String getVendor_code(){
		return this.vendor_code;
	}
	
	public void setVendor_code(String value){
		this.vendor_code = value;
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
	public String getBrand_name(){
		return this.brand_name;
	}
	
	public void setBrand_name(String value){
		this.brand_name = value;
	}
	public List<vipapis.delivery.ExportProduct> getGoods_list(){
		return this.goods_list;
	}
	
	public void setGoods_list(List<vipapis.delivery.ExportProduct> value){
		this.goods_list = value;
	}
	
}