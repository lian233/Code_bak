package com.wofu.ecommerce.maisika;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * 订单类
 *
 */
public class Order extends BusinessObject{
	private int modify_time;//订单修改时间 这是改为INT了
	private String order_id = "";//订单编号
	private String order_state = "";//订单状态  包含  有无发货
	private int add_time; //订单创建时间  这是改为INT了
	private String order_sn; //订单编号
	private float order_amount = 0f ;//订单实际付款
	private String  order_message ="";//买家留言
	private float shipping_fee=0.0f;//运费
	private String shipping_code="";//快递号 
	private String shipping_express_id="";//快递公司 
	private int payment_time;//支付时间 
	private int shipping_time;//发货时间 
	private String reciver_name;//收货人姓名
	private String buyer_name="";//买家昵称
	private int deliver_status;//发货状态
	private String address="";//收货地址
	private String phone="";//电话
	private String payment_code="";	//支付模式
	private String mob_phone="";//移动电话
	private String nickname="";//买家帐号-写buyernick
	private DataRelation orderItemList =new DataRelation("order_goods","com.wofu.ecommerce.maisika.OrderItem");//这里里面s
	
	
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
