package com.wofu.ecommerce.maisika;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * ������
 *
 */
public class Order extends BusinessObject{
	private int modify_time;//�����޸�ʱ�� ���Ǹ�ΪINT��
	private String order_id = "";//�������
	private String order_state = "";//����״̬  ����  ���޷���
	private int add_time; //��������ʱ��  ���Ǹ�ΪINT��
	private String order_sn; //�������
	private float order_amount = 0f ;//����ʵ�ʸ���
	private String  order_message ="";//�������
	private float shipping_fee=0.0f;//�˷�
	private String shipping_code="";//��ݺ� 
	private String shipping_express_id="";//��ݹ�˾ 
	private int payment_time;//֧��ʱ�� 
	private int shipping_time;//����ʱ�� 
	private String reciver_name;//�ջ�������
	private String buyer_name="";//����ǳ�
	private int deliver_status;//����״̬
	private String address="";//�ջ���ַ
	private String phone="";//�绰
	private String payment_code="";	//֧��ģʽ
	private String mob_phone="";//�ƶ��绰
	private String nickname="";//����ʺ�-дbuyernick
	private DataRelation orderItemList =new DataRelation("order_goods","com.wofu.ecommerce.maisika.OrderItem");//��������s
	
	
	public DataRelation getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(DataRelation orderItemList) {
		this.orderItemList = orderItemList;
	}

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
	public String getOrder_state() {
		return order_state;
	}
	public void setOrder_state(String order_state) {
		this.order_state = order_state;
	}
	public int getAdd_time() {
		return add_time;
	}
	public void setAdd_time(int add_time) {
		this.add_time = add_time;
	}
	
	public int getModify_time() {
		return modify_time;
	}
	public void setModify_time(int modify_time) {
		this.modify_time = modify_time;
	}
	
	public float getOrder_amount() {
		return order_amount;
	}	
	public void setOrder_amount(float order_amount) {
		this.order_amount = order_amount;
	}
	public String getOrder_message() {
		return order_message;
	}
	public void setOrder_messag(String order_message) {
		this.order_message = order_message;
	}
	public float getShipping_fee() {
		return shipping_fee;
	}
	public void setShipping_fee(float shipping_fee) {
		this.shipping_fee = shipping_fee;
	}
	public String getShipping_code() {
		return shipping_code;
	}
	public void setShipping_code(String shipping_code) {
		this.shipping_code = shipping_code;
	}
	public String getShipping_express_id() {
		return shipping_express_id;
	}
	public void setShipping_express_id(String shipping_express_id) {
		this.shipping_express_id = shipping_express_id;
	}
	public int getPayment_time() {
		return payment_time;
	}
	public void setPayment_time(int payment_time) {
		this.payment_time = payment_time;
	}
	public int getShipping_time() {
		return shipping_time;
	}
	public void setShipping_time(int shipping_time) {
		this.shipping_time = shipping_time;
	}

	public String getReciver_name() {
		return reciver_name;
	}
	public void setReciver_name(String reciver_name) {
		this.reciver_name = reciver_name;
	}

	public String getBuyer_name() {
		return buyer_name;
	}
	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
	}
	public int getDeliver_status() {
		return deliver_status;
	}
	public void setDeliver_status(int deliver_status) {
		this.deliver_status = deliver_status;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMob_phone() {
		return mob_phone;
	}
	public void setMob_phone(String mob_phone) {
		this.mob_phone = mob_phone;
	}
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public void setPayment_code(String payment_code) {
		this.payment_code = payment_code;
	}
	public String getPayment_code() {
		return payment_code;
	}


	
	

	
}
