package com.wofu.ecommerce.maisika;
import java.util.ArrayList;
import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
import com.wofu.ecommerce.maisika.ReturnOrderItem;

public class ReturnOrder extends BusinessObject{
	private String order_id = "";//网址上的订单号
	private String order_sn = "";//订单号
	private String refund_sn = "";//退款id
	private String refund_state = "";//退款状态
	private String goods_state = "";//货物状态
	private int add_time;//退货产生时间
	private String receive_message = "";//备注
	private int ship_time;//发货时间
	private String refund_amount = "";//退款金额
	private String invoice_no = "";//
	private String express_id = "";//快递号
	private String return_type = "";//return_type=2 需退货
	private String buyer_message = "";//退货原因
	private String seller_state="";//卖家处理状态
	private String reason_info = "";//退货描述
	private String buyer_name = "";//买家昵称
//	private String name = "";//收货人姓名
//	private String phone = "";//收货人电话
//	private String mobile = "";//收货人手机号
//	private String province = "";//省
//	private String city = "";//市
//	private String district = "";//区
//	private String address = "";//地址

	private DataRelation returnItemList =new DataRelation("extend_order_goods","com.wofu.ecommerce.maisika.ReturnOrderItem");//这里里面s

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

//	public String getRefund_id() {
//		return refund_id;
//	}
//
//	public void setRefund_id(String refund_id) {
//		this.refund_id = refund_id;
//	}

	public String getRefund_state() {
		return refund_state;
	}

	public void setRefund_state(String refund_state) {
		this.refund_state = refund_state;
	}

	public String getGoods_state() {
		return goods_state;
	}

	public void setGoods_state(String goods_state) {
		this.goods_state = goods_state;
	}

	public int getAdd_time() {
		return add_time;
	}

	public void setAdd_time(int add_time) {
		this.add_time = add_time;
	}

	public String getReceive_message() {
		return receive_message;
	}

	public void setReceive_message(String receive_message) {
		this.receive_message = receive_message;
	}

	public int getShip_time() {
		return ship_time;
	}

	public void setShip_time(int ship_time) {
		this.ship_time = ship_time;
	}

	public String getRefund_amount() {
		return refund_amount;
	}

	public void setRefund_amount(String refund_amount) {
		this.refund_amount = refund_amount;
	}

	public String getInvoice_no() {
		return invoice_no;
	}

	public void setInvoice_no(String invoice_no) {
		this.invoice_no = invoice_no;
	}

	public String getExpress_id() {
		return express_id;
	}

	public void setExpress_id(String express_id) {
		this.express_id = express_id;
	}

	public String getReturn_type() {
		return return_type;
	}

	public void setReturn_type(String return_type) {
		this.return_type = return_type;
	}

	public String getBuyer_message() {
		return buyer_message;
	}

	public void setBuyer_message(String buyer_message) {
		this.buyer_message = buyer_message;
	}

	public String getSeller_state() {
		return seller_state;
	}

	public void setSeller_state(String seller_state) {
		this.seller_state = seller_state;
	}

	public String getReason_info() {
		return reason_info;
	}

	public void setReason_info(String reason_info) {
		this.reason_info = reason_info;
	}

	public String getBuyer_name() {
		return buyer_name;
	}

	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
	}

	public DataRelation getReturnItemList() {
		return returnItemList;
	}

	public void setReturnItemList(DataRelation returnItemList) {
		this.returnItemList = returnItemList;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setRefund_sn(String refund_sn) {
		this.refund_sn = refund_sn;
	}

	public String getRefund_sn() {
		return refund_sn;
	}
	

	
}
