package com.wofu.ecommerce.weidian;

import java.util.Date;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
import com.wofu.common.json.JSONObject;

public class Order extends BusinessObject
{
	//buyer_info:
	private String name;
	private String address;
	private String post;
	private String phone;
	private String province;
	private String city;
	private String region;
	private String self_address;
	
	//other:
	private String express_no;
	private String express_type;
	private String time;
	private String update_time;
	private String status;
	private String order_id;
	private String img;
	private String status2;
	private String buyer_note;
	private String seller_note;
	private String f_seller_id;
	private String f_shop_name;
	private String f_phone;
	
	private String price;
	private String trade_no;
	private String express_note;
	private String total;
	private String quantity;
	private String note;
	private String seller_id;
	private String sk;
	private String discount;
	private String discount_info;
	private String express_fee;
	private String total_fee;
	private String seller_phone;
	private String seller_name;
	private String is_close;
	private String user_phone;
	private String send_time;
	private Date pay_time;
	private Date add_time;
	private String buyer_identity_id;
	private String order_type;
	private String order_type_des;
	private String return_code;
	private String argue_flag;
	private String status_desc;
	private String fx_fee_value;
	private String confirm_expire;
	private String refund_time;
	private String buyer_refund_fee;
	private String item_id;
	private String total_price;
	private String sku_id;
	private String sku_title;
	private String item_name;
	private String url;
	private String fx_fee_rate;
	private String merchant_code;
	private String sku_merchant_code;
	private String modify_price_enable;
	private String express_fee_num;
	private String original_total_price;
	private String real_income_price;
//	private JSONObject buyer_info;
	
	private DataRelation orderItemList=new DataRelation("orderItemList"/*"items"*/,"com.wofu.ecommerce.weidian.OrderItem");	
	public DataRelation getOrderItemList()
	{
		return orderItemList;
	}
	public void setOrderItemList(DataRelation orderItemList) 
	{
		this.orderItemList = orderItemList;
	}
	
	public void    setName(String name){this.name = name;}
	public String  getName()		   {return name;}
	
	public void    setAddress(String address){this.address = address;}
	public String  getAddress()		   {return address;}
	
	public void    setPost(String post){this.post = post;}
	public String  getPost()		   {return post;}
	
	public void    setPhone(String phone){this.phone = phone;}
	public String  getPhone()		   {return phone;}
	
	public void    setProvince(String province){this.province = province;}
	public String  getProvince()		   {return province;}

	public void    setCity(String city){this.city = city;}
	public String  getCity()		   {return city;}
	
	public void    setRegion(String region){this.region = region;}
	public String  getRegion()		   {return region;}
	
	public void    setSelf_address(String self_address){this.self_address = self_address;}
	public String  getSelf_address()		   {return self_address;}
	
	public void    setExpress_no(String express_no){this.express_no = express_no;}
	public String  getExpress_no()		   {return express_no;}
	
	public void    setExpress_type(String express_type){this.express_type = express_type;}
	public String  getExpress_type()		   {return express_type;}
	
	public void    setTime(String time){this.time = time;}
	public String  getTime()		   {return time;}
	
	public void    setUpdate_time(String update_time){this.update_time = update_time;}
	public String  getUpdate_time()		   {return update_time;}
	
	public void    setStatus(String status){this.status = status;}
	public String  getStatus()		   {return status;}
	
	public void    setOrder_id(String order_id){this.order_id = order_id;}
	public String  getOrder_id()		   {return order_id;}
	
	public void    setImg(String img){this.img = img;}
	public String  getImg()		   {return img;}
	
	public void    setStatus2(String status2){this.status2 = status2;}
	public String  getStatus2()		   {return status2;}
	
	public void    setBuyer_note(String buyer_note){this.buyer_note = buyer_note;}
	public String  getBuyer_note()		   {return buyer_note;}
	
	public void    setSeller_note(String seller_note){this.seller_note = seller_note;}
	public String  getSeller_note()		   {return seller_note;}

	public void    setF_seller_id(String f_seller_id){this.f_seller_id = f_seller_id;}
	public String  getF_seller_id()		   {return f_seller_id;}
	
	public void    setF_shop_name(String f_shop_name){this.f_shop_name = f_shop_name;}
	public String  getF_shop_name()		   {return f_shop_name;}
	
	public void    setF_phone(String f_phone){this.f_phone = f_phone;}
	public String  getF_phone()		   {return f_phone;}
	
	public void    setPrice(String price){this.price = price;}
	public String  getPrice()		   {return price;}
	
	public void    setTrade_no(String trade_no){this.trade_no = trade_no;}
	public String  getTrade_no()		   {return trade_no;}
	
	public void    setExpress_note(String express_note){this.express_note = express_note;}
	public String  getExpress_note()		   {return express_note;}
	
	public void    setTotal(String total){this.total = total;}
	public String  getTotal()		   {return total;}
	
	public void    setQuantity(String quantity){this.quantity = quantity;}
	public String  getQuantity()		   {return quantity;}
	
	public void    setNote(String note){this.note = note;}
	public String  getNote()		   {return note;}
	
	public void    setSeller_id(String seller_id){this.seller_id = seller_id;}
	public String  getSeller_id()		   {return seller_id;}
		
	public void    setSk(String sk){this.sk = sk;}
	public String  getSk()		   {return sk;}
	
	public void    setDiscount(String discount){this.discount = discount;}
	public String  getDiscount()		   {return discount;}
	
	public void    setDiscount_info(String discount_info){this.discount_info = discount_info;}
	public String  getDiscount_info()		   {return discount_info;}
	
	public void    setExpress_fee(String express_fee){this.express_fee = express_fee;}
	public String  getExpress_fee()		   {return express_fee;}
	
	public void    setTotal_fee(String total_fee){this.total_fee = total_fee;}
	public String  getTotal_fee()		   {return total_fee;}
	
	public void    setSeller_phone(String seller_phone){this.seller_phone = seller_phone;}
	public String  getSeller_phone()		   {return seller_phone;}
	
	public void    setSeller_name(String seller_name){this.seller_name = seller_name;}
	public String  getSeller_name()		   {return seller_name;}
	
	public void    setIs_close(String is_close){this.is_close = is_close;}
	public String  getIs_close()		   {return is_close;}
	
	public void    setUser_phone(String user_phone){this.user_phone = user_phone;}
	public String  getUser_phone()		   {return user_phone;}
	
	public void    setSend_time(String send_time){this.send_time = send_time;}
	public String  getSend_time()		   {return send_time;}
	
	public void    setBuyer_identity_id(String buyer_identity_id){this.buyer_identity_id = buyer_identity_id;}
	public String  getBuyer_identity_id()		   {return buyer_identity_id;}

	public void    setOrder_type(String order_type){this.order_type = order_type;}
	public String  getOrder_type()		   {return order_type;}

	public void    setOrder_type_des(String order_type_des){this.order_type_des = order_type_des;}
	public String  getOrder_type_des()		   {return order_type_des;}

	public void    setReturn_code(String return_code){this.return_code = return_code;}
	public String  getReturn_code()		   {return return_code;}

	public void    setArgue_flag(String argue_flag){this.argue_flag = argue_flag;}
	public String  getArgue_flag()		   {return argue_flag;}

	public void    setStatus_desc(String status_desc){this.status_desc = status_desc;}
	public String  getStatus_desc()		   {return status_desc;}

	public void    setFx_fee_value(String fx_fee_value){this.fx_fee_value = fx_fee_value;}
	public String  getFx_fee_value()		   {return fx_fee_value;}

	public void    setConfirm_expire(String confirm_expire){this.confirm_expire = confirm_expire;}
	public String  getConfirm_expire()		   {return confirm_expire;}

	public void    setRefund_time(String refund_time){this.refund_time = refund_time;}
	public String  getRefund_time()		   {return refund_time;}

	public void    setBuyer_refund_fee(String buyer_refund_fee){this.buyer_refund_fee = buyer_refund_fee;}
	public String  getBuyer_refund_fee()		   {return buyer_refund_fee;}

	public void    setItem_id(String item_id){this.item_id = item_id;}
	public String  getItem_id()		   {return item_id;}

	public void    setTotal_price(String total_price){this.total_price = total_price;}
	public String  getTotal_price()		   {return total_price;}

	public void    setSku_id(String sku_id){this.sku_id = sku_id;}
	public String  getSku_id()		   {return sku_id;}

	public void    setSku_title(String sku_title){this.sku_title = sku_title;}
	public String  getSku_title()		   {return sku_title;}

	public void    setItem_name(String item_name){this.item_name = item_name;}
	public String  getItem_name()		   {return item_name;}

	public void    setUrl(String url){this.url = url;}
	public String  getUrl()		   {return url;}

	public void    setFx_fee_rate(String fx_fee_rate){this.fx_fee_rate = fx_fee_rate;}
	public String  getFx_fee_rate()		   {return fx_fee_rate;}

	public void    setMerchant_code(String merchant_code){this.merchant_code = merchant_code;}
	public String  getMerchant_code()		   {return merchant_code;}

	public void    setSku_merchant_code(String sku_merchant_code){this.sku_merchant_code = sku_merchant_code;}
	public String  getSku_merchant_code()		   {return sku_merchant_code;}

	public void    setModify_price_enable(String modify_price_enable){this.modify_price_enable = modify_price_enable;}
	public String  getModify_price_enable()		   {return modify_price_enable;}

	public void    setExpress_fee_num(String express_fee_num){this.express_fee_num = express_fee_num;}
	public String  getExpress_fee_num()		   {return express_fee_num;}

	public void    setOriginal_total_price(String original_total_price){this.original_total_price = original_total_price;}
	public String  getOriginal_total_price()		   {return original_total_price;}

	public void    setReal_income_price(String real_income_price){this.real_income_price = real_income_price;}
	public String  getReal_income_price()		   {return real_income_price;}
	public Date getPay_time() {
		return pay_time;
	}
	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
	}
	public Date getAdd_time() {
		return add_time;
	}
	public void setAdd_time(Date add_time) {
		this.add_time = add_time;
	}

	

//	public void setBuyer_info(JSONObject buyer_info)
//	{
//		this.buyer_info=buyer_info;
//	}
//	public JSONObject getBuyer_info()
//	{
//		return buyer_info;
//	}
}
