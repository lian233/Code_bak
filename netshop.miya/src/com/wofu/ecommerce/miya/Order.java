package com.wofu.ecommerce.miya;


import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.common.json.JSONObject;

public class Order extends BusinessObject{
	private String order_id;//订单号
	private String order_state; //订单状态	1.待付款2. 已付款待发货3. 发货中【预留，暂不使用】4. 发货完成5. 订单完结6. 已取消
	private String order_payment;//应付金额（不包含运费）
	private String order_remark;//订单备注
	private String pay_type;//支付方式
	private String order_time;//下单时间
	private String confirm_time;//付款确认时间, 未付款为"0000-00-00 00:00:00"
	private String cancel_time;//订单取消时间, 未取消为"0000-00-00 00:00:00"
	private String sheet_code;//运单号, 未发货为空
	private String logistic_id;//物流公司ID
	private String ship_price;//运费
	private String order_total_price;//订单总金额（不包含运费）
	private String pay_price;//实际支付金额（包含运费）
	private Date modify_time;//订单修改时间
	private String return_info_list;//退货信息
	private String need_invoice;//是否需要发票 0不需要, 1需要
	private String address_info;//收货人信息()
//	  order_state
//    "dst_province": "",     #省
//    "dst_city": "",       #市
//    "dst_area": "",       #区	
//    "dst_street": "",       #街道
//    "dst_address": "",      #地址
//    "dst_name": "",       #收货人姓名
//    "dst_mobile": "",       #收货人手机
//    "dst_tel": ""        #收货人座机
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getOrder_state() {
		return order_state;
	}
	public void setOrder_state(String order_state) {
		this.order_state = order_state;
	}
	public String getOrder_payment() {
		return order_payment;
	}
	public void setOrder_payment(String order_payment) {
		this.order_payment = order_payment;
	}
	public String getOrder_remark() {
		return order_remark;
	}
	public void setOrder_remark(String order_remark) {
		this.order_remark = order_remark;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public String getOrder_time() {
		return order_time;
	}
	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}
	public String getConfirm_time() {
		return confirm_time;
	}
	public void setConfirm_time(String confirm_time) {
		this.confirm_time = confirm_time;
	}
	public String getCancel_time() {
		return cancel_time;
	}
	public void setCancel_time(String cancel_time) {
		this.cancel_time = cancel_time;
	}
	public String getSheet_code() {
		return sheet_code;
	}
	public void setSheet_code(String sheet_code) {
		this.sheet_code = sheet_code;
	}
	public String getLogistic_id() {
		return logistic_id;
	}
	public void setLogistic_id(String logistic_id) {
		this.logistic_id = logistic_id;
	}
	public String getShip_price() {
		return ship_price;
	}
	public void setShip_price(String ship_price) {
		this.ship_price = ship_price;
	}
	public String getOrder_total_price() {
		return order_total_price;
	}
	public void setOrder_total_price(String order_total_price) {
		this.order_total_price = order_total_price;
	}
	public String getPay_price() {
		return pay_price;
	}
	public void setPay_price(String pay_price) {
		this.pay_price = pay_price;
	}

	public String getReturn_info_list() {
		return return_info_list;
	}
	public void setReturn_info_list(String return_info_list) {
		this.return_info_list = return_info_list;
	}
	public String getNeed_invoice() {
		return need_invoice;
	}
	public void setNeed_invoice(String need_invoice) {
		this.need_invoice = need_invoice;
	}
	public String getAddress_info() {
		return address_info;
	}
	public void setAddress_info(String address_info) {
		this.address_info = address_info;
	}
	public Date getModify_time() {
		return modify_time;
	}
	public void setModify_time(Date modify_time) {
		this.modify_time = modify_time;
	}
	
}
