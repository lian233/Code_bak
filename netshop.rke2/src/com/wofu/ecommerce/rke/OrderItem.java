package com.wofu.ecommerce.rke;

import java.util.ArrayList;
import java.util.Date;

public class OrderItem{
	private String order_id;   //订单id（商城内部id）
	private String order_sn;   //订单号
	private String buyer_email;   //邮箱
	private float order_amount;   //订单金额
	private float goods_amount;      //商品金额
	private Date add_time;         //下单时间
	private String address;         //收货人详细地址
	private Date shipping_time;         //shipping_time
	private String payment_code;         //
	private String pay_sn;         //
	private String store_id;         //
	private String store_name;         //
	private String buyer_id;         //
	private String buyer_name;         //
	private String payment_time;         //
	private String finnshed_time;         //
	private String rcb_amount;         //
	private String pd_amount;         //
	private String shipping_fee;         //
	private String evaluation_state;         //
	private String order_state;         //
	private String refund_state;         //
	private String lock_state;         //
	private String delete_state;         //
	private String refund_amount;         //
	private String delay_time;         //
	private String order_from;         //
	private String shipping_code;         //
	private String card_money;         //
	private String card_id;         //
	private String card_code;         //
	private String prepay_id;         //
	private String fid;         //
	private String commision;         //
	private String tocash;         //
	private String wx_orderid;         //
	private String wx_transaction_id;         //
	private String state_desc;         //
	private String payment_name;         //
	//extend_order_common:
	private String shipping_express_id;         //
	private String evaluation_time;         //
	private String evalseller_state;         //
	private String evalseller_time;         //
	private String order_message;         //
	private String order_pointscount;         //
	private String voucher_price;         //
	private String voucher_code;         //
	private String deliver_explain;         //
	private String daddress_id;         //
	private String reciver_name;         //
	private String reciver_province_id;         //
	private String reciver_city_id;         //
	private String promotion_info;         //
	private String dlyo_pickup_code;         //
	private String distri_level_id;         //
	private String distri_level_name;         //
	private String distri_type_id;         //
	private String distri_type_name;         //
	private String distri_commi_per;         //
	private String commision_recflag;         //
	private String commision_rectime;         //
	private String commision_recvoucher;         //
	//reciver_info:
	private String phone;         //
	private String mob_phone;         //
	private String tel_phone;         //
	private String area;         //
	private String street;         //
	//extend_store:
	private String grade_id;         //
	private String member_id;         //
	private String member_name;         //
	private String seller_name;         //
	private String sc_id;         //
	private String store_company_name;         //
	private String province_id;         //
	private String area_info;         //
	private String store_address;         //
	private String store_zip;         //
	private String store_state;         //
	private String store_close_info;         //
	private String store_sort;         //
	private String store_time;         //
	private String store_end_time;         //
	private String store_label;         //
	private String store_banner;         //
	private String store_avatar;         //
	private String store_keywords;         //
	private String store_description;         //
	private String store_qq;         //
	private String store_ww;         //
	private String store_phone;         //
	private String store_zy;         //
	private String store_domain;         //
	private String store_domain_times;         //
	private String store_recommend;         //
	private String store_theme;         //
	private String store_credit;         //
	private String store_desccredit;         //
	private String store_servicecredit;         //
	private String store_deliverycredit;         //
	private String store_collect;         //
	private String store_slide;         //
	private String store_slide_url;         //
	private String store_stamp;         //
	private String store_printdesc;         //
	private String store_sales;         //
	private String store_presales;         //
	private String store_aftersales;         //
	private String store_workingtime;         //
	private String store_free_price;         //
	private String store_decoration_switch;         //
	private String store_decoration_only;         //
	private String store_decoration_image_count;         //
	private String live_store_name;         //
	private String live_store_address;         //
	private String live_store_tel;         //
	private String live_store_bus;         //
	private String is_own_shop;         //
	private String bind_all_gc;         //
	private String store_vrcode_prefix;         //
	//extend_member:
	private String member_truename;         //
	private String member_avatar;         //
	private String member_sex;         //
	private String member_birthday; 
	private String member_passwd; 
	private String member_paypwd; 
	private String member_email; 
	private String member_email_bind; 
	private String member_mobile; 
	private String member_mobile_bind; 
	private String member_qq; 
	private String member_ww; 
	private String member_login_num; 
	private String member_time; 
	private String member_login_time; 
	private String member_old_login_time; 
	private String member_login_ip; 
	private String member_old_login_ip; 
	private String member_qqopenid; 
	private String member_qqinfo; 
	private String member_sinaopenid; 
	private String member_sinainfo; 
	private String member_points; 
	private String available_predeposit; 
	private String freeze_predeposit; 
	private String available_rc_balance; 
	private String freeze_rc_balance; 
	private String inform_allow; 
	private String is_buy; 
	private String is_allowtalk; 
	private String member_state; 
	private String member_snsvisitnum; 
	private String member_areaid; 
	private String member_cityid; 
	private String member_provinceid; 
	private String member_areainfo; 
	private String member_privacy; 
	private String member_quicklink; 
	private String member_exppoints; 
	private String member_wxopenid; 
	private String import_id; 
	//goods:
	private String rec_id; 
	private String goods_id; 
	private String goods_name; 
	private String goods_price; 
	private String goods_num; 
	private String goods_image; 
	private String goods_pay_price; 
	private String goods_type; 
	private String promotions_id; 
	private String commis_rate; 
	private String gc_id; 
	private String sales_id; 
	private String gc_commi_per; 
	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getBuyer_email() {
		return buyer_email;
	}
	public void setBuyer_email(String buyer_email) {
		this.buyer_email = buyer_email;
	}
	public float getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(float order_amount) {
		this.order_amount = order_amount;
	}
	public float getGoods_amount() {
		return goods_amount;
	}
	public void setGoods_amount(float goods_amount) {
		this.goods_amount = goods_amount;
	}
	public Date getAdd_time() {
		return add_time;
	}
	public void setAdd_time(Date add_time) {
		this.add_time = add_time;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getShipping_time() {
		return shipping_time;
	}
	public void setShipping_time(Date shipping_time) {
		this.shipping_time = shipping_time;
	}
	///////////////////////////////////////////////////////////
	
	public String getPayment_code()
	{
		return payment_code;
	}

	public String getPay_sn()
	{
		return pay_sn;
	}

	public String getStore_id()
	{
		return store_id;
	}

	public String getStore_name()
	{
		return store_name;
	}

	public String getBuyer_id()
	{
		return buyer_id;
	}

	public String getBuyer_name()
	{
		return buyer_name;
	}

	public String getPayment_time()
	{
		return payment_time;
	}

	public String getFinnshed_time()
	{
		return finnshed_time;
	}

	public String getRcb_amount()
	{
		return rcb_amount;
	}

	public String getPd_amount()
	{
		return pd_amount;
	}

	public String getShipping_fee()
	{
		return shipping_fee;
	}

	public String getEvaluation_state()
	{
		return evaluation_state;
	}

	public String getOrder_state()
	{
		return order_state;
	}

	public String getRefund_state()
	{
		return 	refund_state;
	}

	public String getLock_state()
	{
		return lock_state;
	}

	public String getDelete_state()
	{
		return 	delete_state;
	}

	public String getRefund_amount()
	{
		return refund_amount;
	}

	public String getDelay_time()
	{
		return delay_time;
	}

	public String getOrder_from()
	{
		return order_from;
	}

	public String getShipping_code()
	{
		return shipping_code;
	}

	public String getCard_money()
	{
		return card_money;
	}

	public String getCard_id()
	{
		return card_id;
	}

	public String getCard_code()
	{
		return card_code;
	}

	public String getPrepay_id()
	{
		return prepay_id;
	}

	public String getFid()
	{
		return fid;
	}

	public String getCommision()
	{
		return commision;
	}

	public String getTocash()
	{
		return tocash;
	}

	public String getWx_orderid()
	{
		return wx_orderid;
	}

	public String getWx_transaction_id()
	{
		return wx_transaction_id;
	}

	public String getState_desc()
	{
		return state_desc;
	}

	public String getPayment_name()
	{
		return payment_name;
	}        
	
	public String getShipping_express_id()
	{
		return shipping_express_id;
	}

	public String getEvaluation_time()
	{
		return evaluation_time ;
	}

	public String getEvalseller_state()
	{
		return evalseller_state;
	}

	public String getEvalseller_time()
	{
		return evalseller_time ;
	}

	public String getOrder_message()
	{
		return order_message     ;
	}

	public String getOrder_pointscount()
	{
		return order_pointscount;
	}

	public String getVoucher_price()
	{
		return voucher_price;
	}

	public String getVoucher_code()
	{
		return voucher_code;
	}

	public String getDeliver_explain()
	{
		return deliver_explain;
	}

	public String getDaddress_id()
	{
		return daddress_id;
	}

	public String getReciver_name()
	{
		return reciver_name;
	}

	public String getReciver_province_id()
	{
		return reciver_province_id;
	}

	public String getReciver_city_id()
	{
		return reciver_city_id;
	}

	public String getPromotion_info()
	{
		return promotion_info;
	}

	public String getDlyo_pickup_code()
	{
		return dlyo_pickup_code;
	}

	public String getDistri_level_id()
	{
		return distri_level_id;
	}

	public String getDistri_level_name()
	{
		return distri_level_name;
	}

	public String getDistri_type_id()
	{
		return distri_type_id;
	}

	public String getDistri_type_name()
	{
		return distri_type_name;
	}

	public String getDistri_commi_per()
	{
		return distri_commi_per;
	}

	public String getCommision_recflag()
	{
		return commision_recflag;
	}

	public String getCommision_rectime()
	{
		return commision_rectime;
	}

	public String getCommision_recvoucher()
	{
		return commision_recvoucher;
	}

	public String getPhone()
	{
		return phone;
	}

	public String getMob_phone()
	{
		return mob_phone;
	}

	public String getTel_phone()
	{
		return tel_phone;
	}

	public String getArea()
	{
		return area;
	}

	public String getStreet()
	{
		return street;
	}

	public String getGrade_id()
	{
		return grade_id;
	}

	public String getMember_id()
	{
		return member_id;
	}

	public String getMember_name()
	{
		return member_name;
	}

	public String getSeller_name()
	{
		return seller_name;
	}

	public String getSc_id()
	{
		return sc_id;
	}

	public String getStore_company_name()
	{
		return store_company_name;
	}

	public String getProvince_id()
	{
		return province_id;
	}

	public String getArea_info()
	{
		return area_info;
	}

	public String getStore_address()
	{
		return store_address;
	}

	public String getStore_zip()
	{
		return store_zip;
	}

	public String getStore_state()
	{
		return store_state;
	}

	public String getStore_close_info()
	{
		return store_close_info;
	}

	public String getStore_sort()
	{
		return store_sort;
	}

	public String getStore_time()
	{
		return store_time;
	}

	public String getStore_end_time()
	{
		return store_end_time;
	}

	public String getStore_label()
	{
		return store_label;
	}

	public String getStore_banner()
	{
		return store_banner;
	}

	public String getStore_avatar()
	{
		return store_avatar;
	}

	public String getStore_keywords()
	{
		return store_keywords;
	}

	public String getStore_description()
	{
		return store_description;
	}

	public String getStore_qq()
	{
		return store_qq;
	}

	public String getStore_ww()
	{
		return store_ww;
	}

	public String getStore_phone()
	{
		return store_phone;
	}

	public String getStore_zy()
	{
		return store_zy;
	}

	public String getStore_domain()
	{
		return store_domain;
	}

	public String getStore_domain_times()
	{
		return store_domain_times;
	}

	public String getStore_recommend()
	{
		return store_recommend;
	}

	public String getStore_theme()
	{
		return store_theme;
	}

	public String getStore_credit()
	{
		return store_credit;
	}

	public String getStore_desccredit()
	{
		return store_desccredit;
	}

	public String getStore_servicecredit()
	{
		return store_servicecredit;
	}

	public String getStore_deliverycredit()
	{
		return store_deliverycredit;
	}

	public String getStore_collect()
	{
		return store_collect;
	}

	public String getStore_slide()
	{
		return store_slide;
	}

	public String getStore_slide_url()
	{
		return store_slide_url;
	}

	public String getStore_stamp()
	{
		return store_stamp;
	}

	public String getStore_printdesc()
	{
		return store_printdesc;
	}

	public String getStore_sales()
	{
		return store_sales;
	}

	public String getStore_presales()
	{
		return store_presales;
	}

	public String getStore_aftersales()
	{
		return store_aftersales;
	}

	public String getStore_workingtime()
	{
		return store_workingtime;
	}

	public String getStore_free_price()
	{
		return store_free_price;
	}

	public String getStore_decoration_switch()
	{
		return store_decoration_switch;
	}

	public String getStore_decoration_only()
	{
		return store_decoration_only;
	}

	public String getStore_decoration_image_count()
	{
		return store_decoration_image_count;
	}

	public String getLive_store_name()
	{
		return live_store_name;
	}

	public String getLive_store_address()
	{
		return live_store_address;
	}

	public String getLive_store_tel()
	{
		return live_store_tel;
	}

	public String getLive_store_bus()
	{
		return live_store_bus;
	}

	public String getIs_own_shop()
	{
		return is_own_shop;
	}

	public String getBind_all_gc()
	{
		return bind_all_gc;
	}

	public String getStore_vrcode_prefix()
	{
		return store_vrcode_prefix;
	}

	public String getMember_truename()
	{
		return member_truename;
	}

	public String getMember_avatar()
	{
		return member_avatar;
	}

	public String getMember_sex()
	{
		return member_sex;
	}
	public String getMember_birthday()
	{
		return member_birthday;
	}

	public String getMember_passwd()
	{
		return member_passwd;
	}

	public String getMember_paypwd()
	{
		return member_paypwd;
	}

	public String getMember_email()
	{
		return member_email;
	}

	public String getMember_email_bind()
	{
		return member_email_bind;
	}

	public String getMember_mobile()
	{
		return member_mobile;
	}

	public String getMember_mobile_bind()
	{
		return member_mobile_bind;
	}

	public String getMember_qq()
	{
		return member_qq;
	}

	public String getMember_ww()
	{
		return member_ww;
	}

	public String getMember_login_num()
	{
		return member_login_num;
	}

	public String getMember_time()
	{
		return member_time;
	}

	public String getMember_login_time()
	{
		return member_login_time;
	}

	public String getMember_old_login_time()
	{
		return member_old_login_time;
	}

	public String getMember_login_ip()
	{
		return member_login_ip;
	}

	public String getMember_old_login_ip()
	{
		return member_old_login_ip;
	}

	public String getMember_qqopenid()
	{
		return member_qqopenid;
	}

	public String getMember_qqinfo()
	{
		return member_qqinfo;
	}

	public String getMember_sinaopenid()
	{
		return member_sinaopenid;
	}

	public String getMember_sinainfo()
	{
		return member_sinainfo;
	}

	public String getMember_points()
	{
		return member_points;
	}

	public String getAvailable_predeposit()
	{
		return available_predeposit;
	}

	public String getFreeze_predeposit()
	{
		return freeze_predeposit;
	}

	public String getAvailable_rc_balance()
	{
		return available_rc_balance;
	}

	public String getFreeze_rc_balance()
	{
		return freeze_rc_balance;
	}

	public String getInform_allow()
	{
		return inform_allow;
	}

	public String getIs_buy()
	{
		return is_buy;
	}

	public String getIs_allowtalk()
	{
		return is_allowtalk;
	}

	public String getMember_state()
	{
		return member_state;
	}

	public String getMember_snsvisitnum()
	{
		return member_snsvisitnum;
	}

	public String getMember_areaid()
	{
		return member_areaid;
	}

	public String getMember_cityid()
	{
		return member_cityid;
	}

	public String getMember_provinceid()
	{
		return member_provinceid;
	}

	public String getMember_areainfo()
	{
		return member_areainfo;
	}

	public String getMember_privacy()
	{
		return member_privacy;
	}

	public String getMember_quicklink()
	{
		return member_quicklink;
	}

	public String getMember_exppoints()
	{
		return member_exppoints;
	}

	public String getMember_wxopenid()
	{
		return member_wxopenid;
	}

	public String getImport_id()
	{
		return import_id;
	}

	public String getRec_id()
	{
		return rec_id;
	}


	public String getGoods_price()
	{
		return goods_price;
	}

	public String getGoods_num()
	{
		return goods_num;
	}

	public String getGoods_image()
	{
		return goods_image;
	}

	public String getGoods_pay_price()
	{
		return goods_pay_price;
	}

	public String getGoods_type()
	{
		return goods_type;
	}

	public String getPromotions_id()
	{
		return promotions_id;
	}

	public String getCommis_rate()
	{
		return commis_rate;
	}

	public String getGc_id()
	{
		return gc_id;
	}

	public String getSales_id()
	{
		return sales_id;
	}

	public String getGc_commi_per()
	{
		return gc_commi_per;
	}

//////////////////////////////////////////
	public void setPayment_code(String payment_code)
	{
		this.payment_code=payment_code;
	}

	public void setPay_sn(String pay_sn)
	{
		this.pay_sn=pay_sn;
	}

	public void setStore_id(String store_id)
	{
		this.store_id=store_id;
	}

	public void setStore_name(String store_name)
	{
		this.store_name=store_name;
	}

	public void setBuyer_id(String buyer_id)
	{
		this.buyer_id=buyer_id;
	}

	public void setBuyer_name(String buyer_name)
	{
		this.buyer_name=buyer_name;
	}

	public void setPayment_time(String payment_time)
	{
		this.payment_time=payment_time;
	}

	public void setFinnshed_time(String finnshed_time)
	{
		this.finnshed_time=finnshed_time;
	}

	public void setRcb_amount(String rcb_amount)
	{
		this.rcb_amount=rcb_amount;
	}

	public void setPd_amount(String pd_amount)
	{
		this.pd_amount=pd_amount;
	}

	public void setShipping_fee(String shipping_fee)
	{
		this.shipping_fee=shipping_fee;
	}

	public void setEvaluation_state(String evaluation_state)
	{
		this.evaluation_state=evaluation_state;
	}

	public void setOrder_state(String order_state)
	{
		this.order_state=order_state;
	}

	public void setRefund_state(String refund_state)
	{
		this.refund_state=refund_state;
	}

	public void setLock_state(String lock_state)
	{
		this.lock_state=lock_state;
	}

	public void setDelete_state(String delete_state)
	{
		this.	delete_state=delete_state;
	}

	public void setRefund_amount(String refund_amount)
	{
		this.refund_amount=refund_amount;
	}

	public void setDelay_time(String delay_time)
	{
		this.delay_time=delay_time;
	}

	public void setOrder_from(String order_from)
	{
		this.order_from=order_from;
	}

	public void setShipping_code(String shipping_code)
	{
		this.shipping_code=shipping_code;
	}

	public void setCard_money(String card_money)
	{
		this.card_money=card_money;
	}

	public void setCard_id(String card_id)
	{
		this.card_id=card_id;
	}

	public void setCard_code(String card_code)
	{
		this.card_code=card_code;
	}

	public void setPrepay_id(String prepay_id)
	{
		this.prepay_id=prepay_id;
	}

	public void setFid(String fid)
	{
		this.fid=fid;
	}

	public void setCommision(String commision)
	{
		this.commision=commision;
	}

	public void setTocash(String tocash)
	{
		this.tocash=tocash;
	}

	public void setWx_orderid(String wx_orderid)
	{
		this.wx_orderid=wx_orderid;
	}

	public void setWx_transaction_id(String wx_transaction_id)
	{
		this.wx_transaction_id=wx_transaction_id;
	}

	public void setState_desc(String state_desc)
	{
		this.state_desc=state_desc;
	}

	public void setPayment_name(String state_desc)
	{
		this.payment_name=state_desc;
	}        
	
	public void setShipping_express_id(String shipping_express_id)
	{
		this.shipping_express_id=shipping_express_id;
	}

	public void setEvaluation_time(String evaluation_time)
	{
		this.evaluation_time =evaluation_time;
	}

	public void setEvalseller_state(String evalseller_state)
	{
		this.evalseller_state=evalseller_state;
	}

	public void setEvalseller_time(String evalseller_time)
	{
		this.evalseller_time =evalseller_time;
	}

	public void setOrder_message(String order_message)
	{
		this.order_message     =order_message;
	}

	public void setOrder_pointscount(String order_pointscount)
	{
		this.order_pointscount=order_pointscount;
	}

	public void setVoucher_price(String voucher_price)
	{
		this.voucher_price=voucher_price;
	}

	public void setVoucher_code(String voucher_code)
	{
		this.voucher_code=voucher_code;
	}

	public void setDeliver_explain(String deliver_explain)
	{
		this.deliver_explain=deliver_explain;
	}

	public void setDaddress_id(String daddress_id)
	{
		this.daddress_id=daddress_id;
	}

	public void setReciver_name(String reciver_name)
	{
		this.reciver_name=reciver_name;
	}

	public void setReciver_province_id(String reciver_province_id)
	{
		this.reciver_province_id=reciver_province_id;
	}

	public void setReciver_city_id(String reciver_city_id)
	{
		this.reciver_city_id=reciver_city_id;
	}

	public void setPromotion_info(String promotion_info)
	{
		this.promotion_info=promotion_info;
	}

	public void setDlyo_pickup_code(String dlyo_pickup_code)
	{
		this.dlyo_pickup_code=dlyo_pickup_code;
	}

	public void setDistri_level_id(String distri_level_id)
	{
		this.distri_level_id=distri_level_id;
	}

	public void setDistri_level_name(String distri_level_name)
	{
		this.distri_level_name=distri_level_name;
	}

	public void setDistri_type_id(String distri_type_id)
	{
		this.distri_type_id=distri_type_id;
	}

	public void setDistri_type_name(String distri_type_name)
	{
		this.distri_type_name=distri_type_name;
	}

	public void setDistri_commi_per(String distri_commi_per)
	{
		this.distri_commi_per=distri_commi_per;
	}

	public void setCommision_recflag(String commision_recflag)
	{
		this.commision_recflag=commision_recflag;
	}

	public void setCommision_rectime(String commision_rectime)
	{
		this.commision_rectime=commision_rectime;
	}

	public void setCommision_recvoucher(String commision_recvoucher)
	{
		this.commision_recvoucher=commision_recvoucher;
	}

	public void setPhone(String phone)
	{
		this.phone=phone;
	}

	public void setMob_phone(String mob_phone)
	{
		this.mob_phone=mob_phone;
	}

	public void setTel_phone(String tel_phone)
	{
		this.tel_phone=tel_phone;
	}

	public void setArea(String area)
	{
		this.area=area;
	}

	public void setStreet(String street)
	{
		this.street=street;
	}

	public void setGrade_id(String grade_id)
	{
		this.grade_id=grade_id;
	}

	public void setMember_id(String member_id)
	{
		this.member_id=member_id;
	}

	public void setMember_name(String member_name)
	{
		this.member_name=member_name;
	}

	public void setSeller_name(String seller_name)
	{
		this.seller_name=seller_name;
	}

	public void setSc_id(String sc_id)
	{
		this.sc_id=sc_id;
	}

	public void setStore_company_name(String store_company_name)
	{
		this.store_company_name=store_company_name;
	}

	public void setProvince_id(String province_id)
	{
		this.province_id=province_id;
	}

	public void setArea_info(String area_info)
	{
		this.area_info=area_info;
	}

	public void setStore_address(String store_address)
	{
		this.store_address=store_address;
	}

	public void setStore_zip(String store_zip)
	{
		this.store_zip=store_zip;
	}

	public void setStore_state(String store_state)
	{
		this.store_state=store_state;
	}

	public void setStore_close_info(String store_close_info)
	{
		this.store_close_info=store_close_info;
	}

	public void setStore_sort(String store_sort)
	{
		this.store_sort=store_sort;
	}

	public void setStore_time(String store_time)
	{
		this.store_time=store_time;
	}

	public void setStore_end_time(String store_end_time)
	{
		this.store_end_time=store_end_time;
	}

	public void setStore_label(String store_label)
	{
		this.store_label=store_label;
	}

	public void setStore_banner(String store_banner)
	{
		this.store_banner=store_banner;
	}

	public void setStore_avatar(String store_avatar)
	{
		this.store_avatar=store_avatar;
	}

	public void setStore_keywords(String store_keywords)
	{
		this.store_keywords=store_keywords;
	}

	public void setStore_description(String store_description)
	{
		this.store_description=store_description;
	}

	public void setStore_qq(String store_qq)
	{
		this.store_qq=store_qq;
	}

	public void setStore_ww(String store_ww)
	{
		this.store_ww=store_ww;
	}

	public void setStore_phone(String store_phone)
	{
		this.store_phone=store_phone;
	}

	public void setStore_zy(String store_zy)
	{
		this.store_zy=store_zy;
	}

	public void setStore_domain(String store_domain)
	{
		this.store_domain=store_domain;
	}

	public void setStore_domain_times(String store_domain_times)
	{
		this.store_domain_times=store_domain_times;
	}

	public void setStore_recommend(String store_recommend)
	{
		this.store_recommend=store_recommend;
	}

	public void setStore_theme(String store_theme)
	{
		this.store_theme=store_theme;
	}

	public void setStore_credit(String store_credit)
	{
		this.store_credit=store_credit;
	}

	public void setStore_desccredit(String store_desccredit)
	{
		this.store_desccredit=store_desccredit;
	}

	public void setStore_servicecredit(String store_servicecredit)
	{
		this.store_servicecredit=store_servicecredit;
	}

	public void setStore_deliverycredit(String store_deliverycredit)
	{
		this.store_deliverycredit=store_deliverycredit;
	}

	public void setStore_collect(String store_collect)
	{
		this.store_collect=store_collect;
	}

	public void setStore_slide(String store_slide)
	{
		this.store_slide=store_slide;
	}

	public void setStore_slide_url(String store_slide_url)
	{
		this.store_slide_url=store_slide_url;
	}

	public void setStore_stamp(String store_stamp)
	{
		this.store_stamp=store_stamp;
	}

	public void setStore_printdesc(String store_printdesc)
	{
		this.store_printdesc=store_printdesc;
	}

	public void setStore_sales(String store_sales)
	{
		this.store_sales=store_sales;
	}

	public void setStore_presales(String store_presales)
	{
		this.store_presales=store_presales;
	}

	public void setStore_aftersales(String store_aftersales)
	{
		this.store_aftersales=store_aftersales;
	}

	public void setStore_workingtime(String store_workingtime)
	{
		this.store_workingtime=store_workingtime;
	}

	public void setStore_free_price(String store_free_price)
	{
		this.store_free_price=store_free_price;
	}

	public void setStore_decoration_switch(String store_decoration_switch)
	{
		this.store_decoration_switch=store_decoration_switch;
	}

	public void setStore_decoration_only(String store_decoration_only)
	{
		this.store_decoration_only=store_decoration_only;
	}

	public void setStore_decoration_image_count(String store_decoration_image_count)
	{
		this.store_decoration_image_count=store_decoration_image_count;
	}

	public void setLive_store_name(String live_store_name)
	{
		this.live_store_name=live_store_name;
	}

	public void setLive_store_address(String live_store_address)
	{
		this.live_store_address=live_store_address;
	}

	public void setLive_store_tel(String live_store_tel)
	{
		this.live_store_tel=live_store_tel;
	}

	public void setLive_store_bus(String live_store_bus)
	{
		this.live_store_bus=live_store_bus;
	}

	public void setIs_own_shop(String is_own_shop)
	{
		this.is_own_shop=is_own_shop;
	}

	public void setBind_all_gc(String bind_all_gc)
	{
		this.bind_all_gc=bind_all_gc;
	}

	public void setStore_vrcode_prefix(String store_vrcode_prefix)
	{
		this.store_vrcode_prefix=store_vrcode_prefix;
	}

	public void setMember_truename(String member_truename)
	{
		this.member_truename=member_truename;
	}

	public void setMember_avatar(String member_avatar)
	{
		this.member_avatar=member_avatar;
	}

	public void setMember_sex(String member_sex)
	{
		this.member_sex=member_sex;
	}
	public void setMember_birthday(String member_birthday)
	{
		this.member_birthday=member_birthday;
	}

	public void setMember_passwd(String member_passwd)
	{
		this.member_passwd=member_passwd;
	}

	public void setMember_paypwd(String member_paypwd)
	{
		this.member_paypwd=member_paypwd;
	}

	public void setMember_email(String member_email)
	{
		this.member_email=member_email;
	}

	public void setMember_email_bind(String member_email_bind)
	{
		this.member_email_bind=member_email_bind;
	}

	public void setMember_mobile(String member_mobile)
	{
		this.member_mobile=member_mobile;
	}

	public void setMember_mobile_bind(String member_mobile_bind)
	{
		this.member_mobile_bind=member_mobile_bind;
	}

	public void setMember_qq(String member_qq)
	{
		this.member_qq=member_qq;
	}

	public void setMember_ww(String member_ww)
	{
		this.member_ww=member_ww;
	}

	public void setMember_login_num(String member_login_num)
	{
		this.member_login_num=member_login_num;
	}

	public void setMember_time(String member_login_num)
	{
		this.member_time=member_login_num;
	}

	public void setMember_login_time(String member_login_time)
	{
		this.member_login_time=member_login_time;
	}

	public void setMember_old_login_time(String member_old_login_time)
	{
		this.member_old_login_time=member_old_login_time;
	}

	public void setMember_login_ip(String member_login_ip)
	{
		this.member_login_ip=member_login_ip;
	}

	public void setMember_old_login_ip(String member_old_login_ip)
	{
		this.member_old_login_ip=member_old_login_ip;
	}

	public void setMember_qqopenid(String member_qqopenid)
	{
		this.member_qqopenid=member_qqopenid;
	}

	public void setMember_qqinfo(String member_qqinfo)
	{
		this.member_qqinfo=member_qqinfo;
	}

	public void setMember_sinaopenid(String member_sinaopenid)
	{
		this.member_sinaopenid=member_sinaopenid;
	}

	public void setMember_sinainfo(String member_sinainfo)
	{
		this.member_sinainfo=member_sinainfo;
	}

	public void setMember_points(String member_points)
	{
		this.member_points=member_points;
	}

	public void setAvailable_predeposit(String available_predeposit)
	{
		this.available_predeposit=available_predeposit;
	}

	public void setFreeze_predeposit(String freeze_predeposit)
	{
		this.freeze_predeposit=freeze_predeposit;
	}

	public void setAvailable_rc_balance(String available_rc_balance)
	{
		this.available_rc_balance=available_rc_balance;
	}

	public void setFreeze_rc_balance(String freeze_rc_balance)
	{
		this.freeze_rc_balance=freeze_rc_balance;
	}

	public void setInform_allow(String inform_allow)
	{
		this.inform_allow=inform_allow;
	}

	public void setIs_buy(String is_buy)
	{
		this.is_buy=is_buy;
	}

	public void setIs_allowtalk(String is_allowtalk)
	{
		this.is_allowtalk=is_allowtalk;
	}

	public void setMember_state(String is_allowtalk)
	{
		this.member_state=is_allowtalk;
	}

	public void setMember_snsvisitnum(String member_snsvisitnum)
	{
		this.member_snsvisitnum=member_snsvisitnum;
	}

	public void setMember_areaid(String member_areaid)
	{
		this.member_areaid=member_areaid;
	}

	public void setMember_cityid(String member_cityid)
	{
		this.member_cityid=member_cityid;
	}

	public void setMember_provinceid(String member_provinceid)
	{
		this.member_provinceid=member_provinceid;
	}

	public void setMember_areainfo(String member_areainfo)
	{
		this.member_areainfo=member_areainfo;
	}

	public void setMember_privacy(String member_privacy)
	{
		this.member_privacy=member_privacy;
	}

	public void setMember_quicklink(String member_quicklink)
	{
		this.member_quicklink=member_quicklink;
	}

	public void setMember_exppoints(String member_exppoints)
	{
		this.member_exppoints=member_exppoints;
	}

	public void setMember_wxopenid(String member_wxopenid)
	{
		this.member_wxopenid=member_wxopenid;
	}

	public void setImport_id(String import_id)
	{
		this.import_id=import_id;
	}

	public void setRec_id(String rec_id)
	{
		this.rec_id=rec_id;
	}


	public void setGoods_price(String goods_price)
	{
		this.goods_price=goods_price;
	}

	public void setGoods_num(String goods_num)
	{
		this.goods_num=goods_num;
	}

	public void setGoods_image(String goods_image)
	{
		this.goods_image=goods_image;
	}

	public void setGoods_pay_price(String goods_pay_price)
	{
		this.goods_pay_price=goods_pay_price;
	}

	public void setGoods_type(String goods_type)
	{
		this.goods_type=goods_type;
	}

	public void setPromotions_id(String promotions_id)
	{
		this.promotions_id=promotions_id;
	}

	public void setCommis_rate(String commis_rate)
	{
		this.commis_rate=commis_rate;
	}

	public void setGc_id(String gc_id)
	{
		this.gc_id=gc_id;
	}

	public void setSales_id(String sales_id)
	{
		this.sales_id=sales_id;
	}

	public void setGc_commi_per(String gc_commi_per)
	{
		this.gc_commi_per=gc_commi_per;
	}


	
	
	
	
}
