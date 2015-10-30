package com.wofu.ecommerce.weipinhui;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * ������
 *
 */
public class Order extends BusinessObject{
//	private String order_sn = "";//�������
//	private String stat = "";//����״̬
//	private Date add_time; //�����µ�ʱ��
//		private double goods_money = 0.0f ;//������Ʒ�ܽ��
//		private double ex_fav_money = 0.0f ;//�����Żݽ��
//	private String  remark ="";//�������
//	private String  transport_day ="";//�����ջ�ʱ��
//	private String po="";//�ɹ����� 
//	private String express_company="";//��ݹ�˾ 
//		private double carriage=0.0f;//��ݽ����� ���ų��ⵥ��Ʒ����ܺ�+��ݷ��� == ������
//	private String vendor_id="";//����ʱ�� 
//	private String vendor_name="";//�����ر�ʱ�� 
//	private String buyer="";//����ǳ�
//	private String state="";//�ջ�ʡ��
//	private String city="";//�ջ���
//	private String county="";//�ջ���
//	private String address="";//�ջ���
//	private String postcode="";//�ʱ�
//	private String mobile="";//�ƶ��绰
//	private String tel="";//��ϵ�绰
//	private String country_id="";//����id
//	private String invoice="";//��Ʊ̧ͷ
//		private double favourable_money=0.0f;//�Żݽ��
//	private DataRelation orderItemList =new DataRelation("orderItemList","com.wofu.ecommerce.weipinhui.OrderItem");
	
	
	private String order_id;	//�������
	private String order_status;	//����״̬����
	private String buyer;	//�ջ���
	private String address;	//�ջ���ַ
	private String mobile;	//�ֻ�����
	private String tel;	//��ϵ�绰
	private String postcode;	//��������
	private String city;	//����
	private String province;	//ʡ��
	private String country_id;	//���Ҵ���
	private String invoice;	//��Ʊ̧ͷ
		private double carriage;	//��ݽ����� ���ų��ⵥ��Ʒ����ܺ�+��ݷ��� == ������
	private String remark;	//��ע
	private String transport_day;	//�����ջ�ʱ��
	private Integer vendor_id;	//��Ӧ��ID
	private String vendor_name;	//��Ӧ������
		private double promo_discount_amount;	//�����Żݽ��
		private double discount_amount;	//�Żݽ��
		private double product_money;	//���ų��ⵥ��Ʒ����ܺ�(���㷢Ʊ��� == ���ų��ⵥ��Ʒ����ܺ� + ��ݷ��� - �Żݽ�� - �����Żݽ��)
	private Date add_time;	//�����µ�ʱ��
	private String po_no;	//PO�����
	private String country;	//��/��
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
