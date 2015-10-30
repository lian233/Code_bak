package com.wofu.ecommerce.weidian2;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class Order extends BusinessObject
{
	private String order_id;
	private int status;
	private String total_price;
	private String ctime;
	private String mtime;
	private String cmment;
	private String express_price;
	private String express_id;
	private String express_company;
	private String pay_time;
	private String send_time;
	private String deliver_status;
	private String buyer_nickname;
	private String postcode;
	private String name;
	private String phone;
	private String mobile;
	private String province;
	private String city;
	private String district;
	private String address;
	private String sku;
	private String mid;
	private String title;
	private String num;
	private String price;
	private String prop;
	
	//他orderItemList是一个数组  会把orderItemList自动赋值到OrderItem
	private DataRelation orderItemList=new DataRelation("detail","com.wofu.ecommerce.weidian2.OrderItem");
	public DataRelation getOrderItemList(){return orderItemList;}
	public void setOrderItemList(DataRelation orderItemList) {this.orderItemList = orderItemList;}
	
	public void   setOrder_id(String order_id){this.order_id=order_id;}
	public String getOrder_id(){return order_id;}
	
	public void   setStatus(int status){this.status=status;}
	public int getStatus(){return status;}
	
	public void   setTotal_price(String total_price){this.total_price=total_price;}
	public String getTotal_price(){return total_price;}
	
	public void   setCtime(String ctime){this.ctime=ctime;}
	public String getCtime(){return ctime.replace("/", "-");}
	
	public void   setMtime(String mtime){this.mtime=mtime;}
	public String getMtime(){return mtime.replace("/", "-");}
	
	public void   setCmment(String cmment){this.cmment=cmment;}
	public String getCmment(){return cmment;}
	
	public void   setExpress_price(String express_price){this.express_price=express_price;}
	public String getExpress_price(){return express_price;}

	public void   setExpress_id(String express_id){this.express_id=express_id;}
	public String getExpress_id(){return express_id;}
	
	public void   setexpress_company(String express_company){this.express_company=express_company;}
	public String getexpress_company(){return express_company;}
	
	public void   setPay_time(String pay_time){this.pay_time=pay_time;}
	public String getPay_time(){return pay_time;}
	
	public void   setSend_time(String send_time){this.send_time=send_time;}
	public String getSend_time(){return send_time;}
	
	public void   setDeliver_status(String deliver_status){this.deliver_status=deliver_status;}
	public String getDeliver_status(){return deliver_status;}
	
	public void   setBuyer_nickname(String buyer_nickname){this.buyer_nickname=buyer_nickname;}
	public String getBuyer_nickname(){return buyer_nickname;}
	
	public void   setPostcode(String postcode){this.postcode=postcode;}
	public String getPostcode(){return postcode;}
	
	public void   setName(String name){this.name=name;}
	public String getName(){
		if(name.equals("'''")){
			return "";
		}
		else{
			return name;}
	}
	public void   setPhone(String phone){this.phone=phone;}
	public String getPhone(){return phone;}
	
	public void   setMobile(String mobile){this.mobile=mobile;}
	public String getMobile(){return mobile;}
	
	public void   setProvince(String province){this.province=province;}
	public String getProvince(){return province;}
	
	public void   setCity(String city){this.city=city;}
	public String getCity(){return city;}
	
	public void   setDistrict(String district){this.district=district;}
	public String getDistrict(){return district;}
	
	public void   setAddress(String address){this.address=address;}
	public String getAddress(){return address;}
	
	public void   setsku(String sku){this.sku=sku;}
	public String getsku(){return sku;}

	public void   setmid(String mid){this.mid=mid;}
	public String getmid(){return mid;}

	public void   settitle(String title){this.title=title;}
	public String gettitle(){return title;}
	
	public void   setnum(String num){this.num=num;}
	public String getnum(){return num;}
	
	public void   setprice(String price){this.price=price;}
	public String getprice(){return price;}
	
	public void   setprop(String prop){this.prop=prop;}
	public String getprop(){return prop;}
}
