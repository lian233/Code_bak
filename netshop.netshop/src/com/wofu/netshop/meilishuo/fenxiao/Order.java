package com.wofu.netshop.meilishuo.fenxiao;

import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * ������
 * 
 */
public class Order extends BusinessObject
{
	private String order_id = "";// �������
	private String status_text = "";// ����״̬
	private Date ctime; // ��������ʱ��
	private float total_price = 0f;// ������Ʒ�ܽ��
	private String comment = "";// �������
	private float express_price = 0.0f;// �˷�
	private String express_id = "";// ��ݺ�
	private String express_company = "";// ��ݹ�˾
	private Date pay_time = new Date();// ֧��ʱ��
	private Date send_time = new Date();// ����ʱ��
	private Date last_status_time = new Date();// �����ر�ʱ��
	private Date pay_time_out = new Date();// �����رճ�ʱʱ��
	private Date receive_time_out;// �ջ�������
	private Date service_time_out = new Date();// �ۺ�ʱʱ��
	private String buyer_nickname = "";// ����ǳ�
	private int deliver_status;// ����״̬
	private String province = "";// �ջ�ʡ��
	private String city = "";// �ջ���
	private String district = "";// �ջ���
	private String street = "";// �ջ���
	private String postcode = "";// �ʱ�
	private String phone = "";// �ƶ��绰
	private String nickname = "";// ����ʺ�-дbuyernick
	private DataRelation orderItemList = new DataRelation("orderItemList",
			"com.wofu.netshop.meilishuo.fenxiao.OrderItem");
	private DataRelation serviceList = new DataRelation("serviceList",
			"com.wofu.netshop.meilishuo.fenxiao.Service");

	public String getOrder_id()
	{
		return order_id;
	}

	public void setOrder_id(String order_id)
	{
		this.order_id = order_id;
	}

	public String getStatus_text()
	{
		return status_text;
	}

	public void setStatus_text(String status_text)
	{
		this.status_text = status_text;
	}

	public Date getCtime()
	{
		return ctime;
	}

	public void setCtime(Date ctime)
	{
		this.ctime = ctime;
	}

	public float getTotal_price()
	{
		return total_price;
	}

	public void setTotal_price(float total_price)
	{
		this.total_price = total_price;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public float getExpress_price()
	{
		return express_price;
	}

	public void setExpress_price(float express_price)
	{
		this.express_price = express_price;
	}

	public String getExpress_id()
	{
		return express_id;
	}

	public void setExpress_id(String express_id)
	{
		this.express_id = express_id;
	}

	public String getExpress_company()
	{
		return express_company;
	}

	public void setExpress_company(String express_company)
	{
		this.express_company = express_company;
	}

	public Date getPay_time()
	{
		return pay_time;
	}

	public void setPay_time(Date pay_time)
	{
		this.pay_time = pay_time;
	}

	public Date getSend_time()
	{
		return send_time;
	}

	public void setSend_time(Date send_time)
	{
		this.send_time = send_time;
	}

	public Date getLast_status_time()
	{
		return last_status_time;
	}

	public void setLast_status_time(Date last_status_time)
	{
		this.last_status_time = last_status_time;
	}

	public Date getPay_time_out()
	{
		return pay_time_out;
	}

	public void setPay_time_out(Date pay_time_out)
	{
		this.pay_time_out = pay_time_out;
	}

	public Date getReceive_time_out()
	{
		return receive_time_out;
	}

	public void setReceive_time_out(Date receive_time_out)
	{
		this.receive_time_out = receive_time_out;
	}

	public Date getService_time_out()
	{
		return service_time_out;
	}

	public void setService_time_out(Date service_time_out)
	{
		this.service_time_out = service_time_out;
	}

	public String getBuyer_nickname()
	{
		return buyer_nickname;
	}

	public void setBuyer_nickname(String buyer_nickname)
	{
		this.buyer_nickname = buyer_nickname;
	}

	public int getDeliver_status()
	{
		return deliver_status;
	}

	public void setDeliver_status(int deliver_status)
	{
		this.deliver_status = deliver_status;
	}

	public String getProvince()
	{
		return province;
	}

	public void setProvince(String province)
	{
		this.province = province;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getDistrict()
	{
		return district;
	}

	public void setDistrict(String district)
	{
		this.district = district;
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getPostcode()
	{
		return postcode;
	}

	public void setPostcode(String postcode)
	{
		this.postcode = postcode;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public DataRelation getOrderItemList()
	{
		return orderItemList;
	}

	public void setOrderItemList(DataRelation orderItemList)
	{
		this.orderItemList = orderItemList;
	}

	public DataRelation getServiceList()
	{
		return serviceList;
	}

	public void setServiceList(DataRelation serviceList)
	{
		this.serviceList = serviceList;
	}

}
