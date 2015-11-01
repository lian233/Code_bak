package com.wofu.ecommerce.huasheng;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * 订单类
 *
 */
public class Order extends BusinessObject{
	private String order_id;		//订单号	
	private String status;			//订单状态
	private String pay_id;			//付款方式
	private String pay_status;		//付款状态
	private double total_price;		//用户实付
	private Date ctime;				//产生时间
	private Date mtime;				//修改时间
	private String comment;			//备注
	private double express_price;	//快递费	
	private String express_id;		//快递号
	private String express_company;	//快递公司编码
	private Date pay_time;			//付款时间
	private Date send_time;			//发货时间
	private String deliver_status;	//发货状态
	private String buyer_nickname;	//买家昵称
	private String buyer_truename;	//真实姓名
	private String buyer_card;		//身份证号
	private String name;			//收货人姓名
	private String postcode;		//邮编
	private String phone;			//收货人电话
	private String province;		//省
	private String city;			//市
	private String district;		//区
	private String address;			//地址
	private DataRelation detail = new DataRelation("detail","com.wofu.ecommerce.huasheng.OrderItem");	//订单商品列表
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
