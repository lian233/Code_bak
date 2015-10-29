package com.wofu.fire.deliveryservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * ∂©µ•¿‡
 * @author Administrator
 *
 */
public class Order{
	private String order_id;
	private int status;
	private Date ctime;
	private Date mtime;
	private String comment;
	private String express_price;
	private String pay_time;
	private String buyer_nickname;
	private String name;
	private String phone;
	private String mobile;
	private String province;
	private String city;
	private String district;
	private String address;
	private String cert_no;
	private String cert_name;
	private List detail = new ArrayList<Detail>();//("detail","com.wofu.fire.deliveryservice.Detail");
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public String getExpress_price() {
		return express_price;
	}
	public void setExpress_price(String express_price) {
		this.express_price = express_price;
	}
	public String getPay_time() {
		return pay_time;
	}
	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}
	public String getBuyer_nickname() {
		return buyer_nickname;
	}
	public void setBuyer_nickname(String buyer_nickname) {
		this.buyer_nickname = buyer_nickname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	public String getCert_no() {
		return cert_no;
	}
	public void setCert_no(String cert_no) {
		this.cert_no = cert_no;
	}
	public String getCert_name() {
		return cert_name;
	}
	public void setCert_name(String cert_name) {
		this.cert_name = cert_name;
	}
	public List getDetail() {
		return detail;
	}
	public void setDetail(List detail) {
		this.detail = detail;
	}
	
	
}
