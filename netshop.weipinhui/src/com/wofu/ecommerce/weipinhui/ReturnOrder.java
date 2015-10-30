package com.wofu.ecommerce.weipinhui;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class ReturnOrder extends BusinessObject{
//	private String orderCode = "" ;//原订单号
//	private float dealMoney=0.00f;//交易金额
//	private float returnMoney=0.00f;//退款金额
//	private Date applyTime ;        //审请时间
//	private String statusDesc = ""; //退货状态
//	private String reason;           //退货原因
//	private String expressCompanyCode="";  //物流公司代码
//	private String mailNo="";              //运单号
//	private ArrayList<ReturnOrderItem> itemList = new ArrayList<ReturnOrderItem>() ;
	
	private Integer vendor_id = 0;	//供应商ID
	private String order_id = "";	//订单编号
	private String return_status = "";	//退货申请单状态
	private String return_reason = "";	//退货原因
	private String create_time = "";	//从b2c拉取客退订单状态时间
	private String back_sn = "";	//客退申请单号
	//退货单详情
	private DataRelation orderItemList =new DataRelation("orderItemList","com.wofu.ecommerce.weipinhui.ReturnOrderItem");
	/**
	 * @return the vendor_id
	 */
	public Integer getVendor_id() {
		return vendor_id;
	}
	/**
	 * @param vendor_id the vendor_id to set
	 */
	public void setVendor_id(Integer vendor_id) {
		this.vendor_id = vendor_id;
	}
	/**
	 * @return the order_id
	 */
	public String getOrder_id() {
		return order_id;
	}
	/**
	 * @param order_id the order_id to set
	 */
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	/**
	 * @return the return_status
	 */
	public String getReturn_status() {
		return return_status;
	}
	/**
	 * @param return_status the return_status to set
	 */
	public void setReturn_status(String return_status) {
		this.return_status = return_status;
	}
	/**
	 * @return the return_reason
	 */
	public String getReturn_reason() {
		return return_reason;
	}
	/**
	 * @param return_reason the return_reason to set
	 */
	public void setReturn_reason(String return_reason) {
		this.return_reason = return_reason;
	}
	/**
	 * @return the create_time
	 */
	public String getCreate_time() {
		return create_time;
	}
	/**
	 * @param create_time the create_time to set
	 */
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	/**
	 * @return the back_sn
	 */
	public String getBack_sn() {
		return back_sn;
	}
	/**
	 * @param back_sn the back_sn to set
	 */
	public void setBack_sn(String back_sn) {
		this.back_sn = back_sn;
	}
	/**
	 * @return the orderItemList
	 */
	public DataRelation getOrderItemList() {
		return orderItemList;
	}
	/**
	 * @param orderItemList the orderItemList to set
	 */
	public void setOrderItemList(DataRelation orderItemList) {
		this.orderItemList = orderItemList;
	}
}
