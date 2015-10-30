package com.wofu.ecommerce.maisika;
import java.util.ArrayList;
import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
import com.wofu.ecommerce.maisika.ReturnOrderItem;

public class ReturnOrder extends BusinessObject{
	private String order_id = "";//��ַ�ϵĶ�����
	private String order_sn = "";//������
	private String refund_sn = "";//�˿�id
	private String refund_state = "";//�˿�״̬
	private String goods_state = "";//����״̬
	private int add_time;//�˻�����ʱ��
	private String receive_message = "";//��ע
	private int ship_time;//����ʱ��
	private String refund_amount = "";//�˿���
	private String invoice_no = "";//
	private String express_id = "";//��ݺ�
	private String return_type = "";//return_type=2 ���˻�
	private String buyer_message = "";//�˻�ԭ��
	private String seller_state="";//���Ҵ���״̬
	private String reason_info = "";//�˻�����
	private String buyer_name = "";//����ǳ�
//	private String name = "";//�ջ�������
//	private String phone = "";//�ջ��˵绰
//	private String mobile = "";//�ջ����ֻ���
//	private String province = "";//ʡ
//	private String city = "";//��
//	private String district = "";//��
//	private String address = "";//��ַ

	private DataRelation returnItemList =new DataRelation("extend_order_goods","com.wofu.ecommerce.maisika.ReturnOrderItem");//��������s

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
