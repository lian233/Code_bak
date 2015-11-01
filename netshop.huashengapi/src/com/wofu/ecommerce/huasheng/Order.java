package com.wofu.ecommerce.huasheng;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * ������
 *
 */
public class Order extends BusinessObject{
	private String order_id;		//������	
	private String status;			//����״̬
	private String pay_id;			//���ʽ
	private String pay_status;		//����״̬
	private double total_price;		//�û�ʵ��
	private Date ctime;				//����ʱ��
	private Date mtime;				//�޸�ʱ��
	private String comment;			//��ע
	private double express_price;	//��ݷ�	
	private String express_id;		//��ݺ�
	private String express_company;	//��ݹ�˾����
	private Date pay_time;			//����ʱ��
	private Date send_time;			//����ʱ��
	private String deliver_status;	//����״̬
	private String buyer_nickname;	//����ǳ�
	private String buyer_truename;	//��ʵ����
	private String buyer_card;		//���֤��
	private String name;			//�ջ�������
	private String postcode;		//�ʱ�
	private String phone;			//�ջ��˵绰
	private String province;		//ʡ
	private String city;			//��
	private String district;		//��
	private String address;			//��ַ
	private DataRelation detail = new DataRelation("detail","com.wofu.ecommerce.huasheng.OrderItem");	//������Ʒ�б�
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPay_id() {
		return pay_id;
	}
	public void setPay_id(String pay_id) {
		this.pay_id = pay_id;
	}
	public String getPay_status() {
		return pay_status;
	}
	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}
	public double getTotal_price() {
		return total_price;
	}
	public void setTotal_price(double total_price) {
		this.total_price = total_price;
	}
	public Date getCtime() {
		return ctime;
	}
	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}
	public Date getMtime() {
		return mtime;
	}
	public void setMtime(Date mtime) {
		this.mtime = mtime;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public double getExpress_price() {
		return express_price;
	}
	public void setExpress_price(double express_price) {
		this.express_price = express_price;
	}
	public String getExpress_id() {
		return express_id;
	}
	public void setExpress_id(String express_id) {
		this.express_id = express_id;
	}
	public String getExpress_company() {
		return express_company;
	}
	public void setExpress_company(String express_company) {
		this.express_company = express_company;
	}
	public Date getPay_time() {
		return pay_time;
	}
	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
	}
	public Date getSend_time() {
		return send_time;
	}
	public void setSend_time(Date send_time) {
		this.send_time = send_time;
	}
	public String getDeliver_status() {
		return deliver_status;
	}
	public void setDeliver_status(String deliver_status) {
		this.deliver_status = deliver_status;
	}
	public String getBuyer_nickname() {
		return buyer_nickname;
	}
	public void setBuyer_nickname(String buyer_nickname) {
		this.buyer_nickname = buyer_nickname;
	}
	public String getBuyer_truename() {
		return buyer_truename;
	}
	public void setBuyer_truename(String buyer_truename) {
		this.buyer_truename = buyer_truename;
	}
	public String getBuyer_card() {
		return buyer_card;
	}
	public void setBuyer_card(String buyer_card) {
		this.buyer_card = buyer_card;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public DataRelation getDetail() {
		return detail;
	}
	public void setDetail(DataRelation detail) {
		this.detail = detail;
	}
	
}
