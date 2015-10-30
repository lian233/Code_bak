package com.wofu.ecommerce.ecshop;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * ������
 *
 */
public class ReturnOrder extends BusinessObject{
	
	private String order_sn = "";//�������
	private String user_name = "";//�������
	private String email = ""; //�������
	private String zipcode =""; //����ʱ�
	private String postscript = "" ; //�������
	
	private float order_amount = 0f ;//Ӧ����
	private float preferential =0.0f;//�Ż�
	private String province="";//ʡ��
	private String city="";//�� 
	private String district="";//��
	//�ջ�����Ϣ
	private float goods_amount=0f;//��Ʒ�ܽ��
	private String to_buyer="";//�̼�����
	private String shipping_id="";//�ջ�ʡ��
	private int add_time;//��������ʱ��
	private String address="";//�ջ���
	private String mobile="";//�ʱ�
	private String consignee="";//�ƶ��绰
	private String tel="";   //���۱�ʶ ��1������ 0δ����
	private int order_status;//����״̬ order_status  0 δȷ�� 1 ȷ�� 2 ��ȡ�� 3 ��Ч 4 �˻� 
	private int shipping_status;// 0 δ���� 1 �ѷ��� 2 ���ջ� 4 �˻�
	private int pay_status;//�Ƿ�֧��  pay_status 0 δ���� 1 ������ 2 �Ѹ���
	private String shipping_print="";//��ұ�ע
	private String pay_time="";//����֧��ʱ��
	private float fee=0f;//�˷�
	private String inv_payee="";//��Ʊ̧ͷ
	private String inv_content="";//��Ʊ����
	
	private DataRelation shop_info =new DataRelation("shop_info","com.wofu.ecommerce.ecshop.OrderItem");

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

	public String getShipping_id() {
		return shipping_id;
	}

	public void setShipping_id(String shipping_id) {
		this.shipping_id = shipping_id;
	}

	public int getAdd_time() {
		return add_time;
	}

	public void setAdd_time(int add_time) {
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

	public int getOrder_status() {
		return order_status;
	}

	public void setOrder_status(int order_status) {
		this.order_status = order_status;
	}

	public int getShipping_status() {
		return shipping_status;
	}

	public void setShipping_status(int shipping_status) {
		this.shipping_status = shipping_status;
	}

	public int getPay_status() {
		return pay_status;
	}

	public void setPay_status(int pay_status) {
		this.pay_status = pay_status;
	}

	public String getShipping_print() {
		return shipping_print;
	}

	public void setShipping_print(String shipping_print) {
		this.shipping_print = shipping_print;
	}

	public String getPay_time() {
		return pay_time;
	}

	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}

	public float getFee() {
		return fee;
	}

	public void setFee(float fee) {
		this.fee = fee;
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

	public DataRelation getShop_info() {
		return shop_info;
	}

	public void setShop_info(DataRelation shop_info) {
		this.shop_info = shop_info;
	}

}
