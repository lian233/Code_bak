package com.wofu.ecommerce.rke;
import java.util.ArrayList;
import java.util.Date;
public class Order{
	private String order_id;   //����id���̳��ڲ�id��
	private String order_sn;   //������
	private String user_name;   //�û���
	private String email;   //����
	private String zipcode;   //��������
	private String postscript;   //������ע��Ϣ
	private float order_amount;   //�������
	private float preferential;   //�������
	private String province;      //ʡ��
	private String city;      //ʡ��
	private String district;      //ʡ��
	private float goods_amount;      //��Ʒ���
	private String to_buyer;         //���������
	private String shipping_name;         //���������
	private Date add_time;         //�µ�ʱ��
	private String address;         //�ջ�����ϸ��ַ
	private String mobile;         //�ջ����ֻ���
	private String consignee;         //�ջ�������
	private String tel;         //�ջ��˵绰
	private String order_status;         //����״̬
	private String shipping_status;         //����״̬
	private String pay_status;         //֧��״̬
	private String shipping_print;         //������ӡ����Ϣ
	private Date pay_time;         //����ʱ��
	private float fee;         //�������ã����ͷ���+֧��������+��װ���õȣ�
	private Date confirm_time;         //����ȷ��ʱ��
	private Date shipping_time;         //shipping_time
	private String inv_payee;         //��Ʊ����
	private String inv_content;         //��Ʊ����
	private String user_sn;         //u8����
	private ArrayList<OrderItem> OrderItems =new ArrayList<OrderItem>();//������ϸ
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getPostscript() {
		return postscript;
	}
	public void setPostscript(String postscript) {
		this.postscript = postscript;
	}
	public float getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(float order_amount) {
		this.order_amount = order_amount;
	}
	public float getPreferential() {
		return preferential;
	}
	public void setPreferential(float preferential) {
		this.preferential = preferential;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public float getGoods_amount() {
		return goods_amount;
	}
	public void setGoods_amount(float goods_amount) {
		this.goods_amount = goods_amount;
	}
	public String getTo_buyer() {
		return to_buyer;
	}
	public void setTo_buyer(String to_buyer) {
		this.to_buyer = to_buyer;
	}
	public String getShipping_name() {
		return shipping_name;
	}
	public void setShipping_name(String shipping_name) {
		this.shipping_name = shipping_name;
	}
	public Date getAdd_time() {
		return add_time;
	}
	public void setAdd_time(Date add_time) {
		this.add_time = add_time;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getConsignee() {
		return consignee;
	}
	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getOrder_status() {
		return order_status;
	}
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}
	public String getShipping_status() {
		return shipping_status;
	}
	public void setShipping_status(String shipping_status) {
		this.shipping_status = shipping_status;
	}
	public String getPay_status() {
		return pay_status;
	}
	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}
	public String getShipping_print() {
		return shipping_print;
	}
	public void setShipping_print(String shipping_print) {
		this.shipping_print = shipping_print;
	}
	public Date getPay_time() {
		return pay_time;
	}
	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
	}
	public float getFee() {
		return fee;
	}
	public void setFee(float fee) {
		this.fee = fee;
	}
	public Date getConfirm_time() {
		return confirm_time;
	}
	public void setConfirm_time(Date confirm_time) {
		this.confirm_time = confirm_time;
	}
	public Date getShipping_time() {
		return shipping_time;
	}
	public void setShipping_time(Date shipping_time) {
		this.shipping_time = shipping_time;
	}
	public String getInv_payee() {
		return inv_payee;
	}
	public void setInv_payee(String inv_payee) {
		this.inv_payee = inv_payee;
	}
	public String getInv_content() {
		return inv_content;
	}
	public void setInv_content(String inv_content) {
		this.inv_content = inv_content;
	}
	public ArrayList<OrderItem> getOrderItems() {
		return OrderItems;
	}
	public void setOrderItems(ArrayList<OrderItem> orderItems) {
		OrderItems = orderItems;
	}
	public String getUser_sn() {
		return user_sn;
	}
	public void setUser_sn(String user_sn) {
		this.user_sn = user_sn;
	}
	
	
	
	
	
	
	
	
	
	
}
