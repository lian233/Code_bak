package com.wofu.ecommerce.weipinhui;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * 订单类
 *
 */
public class Order extends BusinessObject{
//	private String order_sn = "";//订单编号
//	private String stat = "";//订单状态
//	private Date add_time; //订单下单时间
//		private double goods_money = 0.0f ;//订单商品总金额
//		private double ex_fav_money = 0.0f ;//促销优惠金额
//	private String  remark ="";//买家留言
//	private String  transport_day ="";//期望收货时间
//	private String po="";//采购单号 
//	private String express_company="";//快递公司 
//		private double carriage=0.0f;//快递金额（计算 整张出库单商品金额总和+快递费用 == 订单金额）
//	private String vendor_id="";//发货时间 
//	private String vendor_name="";//订单关闭时间 
//	private String buyer="";//买家昵称
//	private String state="";//收货省份
//	private String city="";//收货市
//	private String county="";//收货县
//	private String address="";//收货街
//	private String postcode="";//邮编
//	private String mobile="";//移动电话
//	private String tel="";//联系电话
//	private String country_id="";//国家id
//	private String invoice="";//发票抬头
//		private double favourable_money=0.0f;//优惠金额
//	private DataRelation orderItemList =new DataRelation("orderItemList","com.wofu.ecommerce.weipinhui.OrderItem");
	
	
	private String order_id;	//订单编号
	private String order_status;	//订单状态编码
	private String buyer;	//收货人
	private String address;	//收货地址
	private String mobile;	//手机号码
	private String tel;	//联系电话
	private String postcode;	//邮政编码
	private String city;	//城市
	private String province;	//省份
	private String country_id;	//国家代码
	private String invoice;	//发票抬头
		private double carriage;	//快递金额（计算 整张出库单商品金额总和+快递费用 == 订单金额）
	private String remark;	//备注
	private String transport_day;	//期望收货时间
	private Integer vendor_id;	//供应商ID
	private String vendor_name;	//供应商名称
		private double promo_discount_amount;	//促销优惠金额
		private double discount_amount;	//优惠金额
		private double product_money;	//整张出库单商品金额总和(计算发票金额 == 整张出库单商品金额总和 + 快递费用 - 优惠金额 - 促销优惠金额)
	private Date add_time;	//订单下单时间
	private String po_no;	//PO单编号
	private String country;	//区/县
	private DataRelation orderItemList =new DataRelation("orderItemList","com.wofu.ecommerce.weipinhui.OrderItem");
	/**
	 * @return the order_id
	 */
	public String getOrder_id() {
		return order_id;
	}
	/**
	 * @param order_id the order_id to set
	 */
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	/**
	 * @return the order_status
	 */
	public String getOrder_status() {
		return order_status;
	}
	/**
	 * @param order_status the order_status to set
	 */
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}
	/**
	 * @return the buyer
	 */
	public String getBuyer() {
		return buyer;
	}
	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * @return the tel
	 */
	public String getTel() {
		return tel;
	}
	/**
	 * @param tel the tel to set
	 */
	public void setTel(String tel) {
		this.tel = tel;
	}
	/**
	 * @return the postcode
	 */
	public String getPostcode() {
		return postcode;
	}
	/**
	 * @param postcode the postcode to set
	 */
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}
	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}
	/**
	 * @return the country_id
	 */
	public String getCountry_id() {
		return country_id;
	}
	/**
	 * @param country_id the country_id to set
	 */
	public void setCountry_id(String country_id) {
		this.country_id = country_id;
	}
	/**
	 * @return the invoice
	 */
	public String getInvoice() {
		return invoice;
	}
	/**
	 * @param invoice the invoice to set
	 */
	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}
	/**
	 * @return the carriage
	 */
	public double getCarriage() {
		return carriage;
	}
	/**
	 * @param carriage the carriage to set
	 */
	public void setCarriage(double carriage) {
		this.carriage = carriage;
	}
	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * @return the transport_day
	 */
	public String getTransport_day() {
		return transport_day;
	}
	/**
	 * @param transport_day the transport_day to set
	 */
	public void setTransport_day(String transport_day) {
		this.transport_day = transport_day;
	}
	/**
	 * @return the vendor_id
	 */
	public Integer getVendor_id() {
		return vendor_id;
	}
	/**
	 * @param vendor_id the vendor_id to set
	 */
	public void setVendor_id(Integer vendor_id) {
		this.vendor_id = vendor_id;
	}
	/**
	 * @return the vendor_name
	 */
	public String getVendor_name() {
		return vendor_name;
	}
	/**
	 * @param vendor_name the vendor_name to set
	 */
	public void setVendor_name(String vendor_name) {
		this.vendor_name = vendor_name;
	}
	/**
	 * @return the promo_discount_amount
	 */
	public double getPromo_discount_amount() {
		return promo_discount_amount;
	}
	/**
	 * @param promo_discount_amount the promo_discount_amount to set
	 */
	public void setPromo_discount_amount(double promo_discount_amount) {
		this.promo_discount_amount = promo_discount_amount;
	}
	/**
	 * @return the discount_amount
	 */
	public double getDiscount_amount() {
		return discount_amount;
	}
	/**
	 * @param discount_amount the discount_amount to set
	 */
	public void setDiscount_amount(double discount_amount) {
		this.discount_amount = discount_amount;
	}
	/**
	 * @return the product_money
	 */
	public double getProduct_money() {
		return product_money;
	}
	/**
	 * @param product_money the product_money to set
	 */
	public void setProduct_money(double product_money) {
		this.product_money = product_money;
	}
	/**
	 * @return the add_time
	 */
	public Date getAdd_time() {
		return add_time;
	}
	/**
	 * @param add_time the add_time to set
	 */
	public void setAdd_time(Date add_time) {
		this.add_time = add_time;
	}
	/**
	 * @return the po_no
	 */
	public String getPo_no() {
		return po_no;
	}
	/**
	 * @param po_no the po_no to set
	 */
	public void setPo_no(String po_no) {
		this.po_no = po_no;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return the orderItemList
	 */
	public DataRelation getOrderItemList() {
		return orderItemList;
	}
	/**
	 * @param orderItemList the orderItemList to set
	 */
	public void setOrderItemList(DataRelation orderItemList) {
		this.orderItemList = orderItemList;
	}

	
}
