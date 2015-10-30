package com.wofu.distribution;

import java.util.Properties;

import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

/**
 * 
 * 分销历史订单
 *
 */
public class DistributionOrdersOld extends PageBusinessObject{
	private String Sheetid;
	private String refsheetid;
	private String Shopname;
	private String OutFlag;
	private String Flag;
	private String Editor;
	private String EditDate;
	private String Operator;
	private String Checker;
	private String CheckDate;
	private String Notes;
	private String Address;
	private String LinkTele;
	private String LinkMan;
	private String Delivery;
	private String PayMode;
	private String PostFee;
	private String PayFee;
	private String DeliverySheetID;
	
	
	
	
	public void search() throws Exception {
		
		String reqdata = this.getReqData();	
		Properties prop=StringUtil.getIniProperties(reqdata);
		String sqlwhere=prop.getProperty("sqlwhere");
		String currpage=prop.getProperty("currpage");
		String pagesize=prop.getProperty("pagesize");
				
		if (prop.containsKey("ordermode"))
			this.orderMode=prop.getProperty("ordermode");

		if (prop.containsKey("searchorderfieldname"))
			this.searchOrderFieldName=prop.getProperty("searchorderfieldname");

		this.setPagesize(Integer.valueOf(pagesize).intValue());
		this.setCurrpage(Integer.valueOf(currpage).intValue());
		
		String sql="select a.Sheetid,a.refsheetid,b.name shopname,case when a.OutFlag=1 then'已出库' when a.outflag=0 "+
		" then '未出库' end as outstockflag,case when a.Flag=1 then '未审核' when a.flag=100 then '已审核' end as orderstatus,"+
		"a.Editor,a.EditDate,a.Operator,a.Checker,a.CheckDate,a.Notes,a.Address,a.LinkTele,a.LinkMan,a.Delivery,case when a.PayMode=1 then '网上支付' when a.paymode=2 then '货到付款' end as maymode "+
		",a.PostFee,a.PayFee,a.DeliverySheetID "
		+"from customerorder a,Shop b where b.ID=a.inShopid "+sqlwhere;
		

		
		this.getRequest().getSession().removeAttribute("search_sql_"+this.getModuleid());
		this.getRequest().getSession().setAttribute("search_sql_"+this.getModuleid(),sql);
		
		this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sql,this.searchOrderFieldName,this.orderMode)));
		
	}




	public String getSheetid() {
		return Sheetid;
	}




	public void setSheetid(String sheetid) {
		this.Sheetid = sheetid;
	}




	public String getRefsheetid() {
		return refsheetid;
	}




	public void setRefsheetid(String refsheetid) {
		this.refsheetid = refsheetid;
	}




	public String getShopname() {
		return Shopname;
	}




	public void setShopname(String shopname) {
		Shopname = shopname;
	}




	public String getOutFlag() {
		return OutFlag;
	}




	public void setOutFlag(String outFlag) {
		OutFlag = outFlag;
	}




	public String getFlag() {
		return Flag;
	}




	public void setFlag(String flag) {
		Flag = flag;
	}




	public String getEditor() {
		return Editor;
	}




	public void setEditor(String editor) {
		Editor = editor;
	}




	public String getEditDate() {
		return EditDate;
	}




	public void setEditDate(String editDate) {
		EditDate = editDate;
	}




	public String getOperator() {
		return Operator;
	}




	public void setOperator(String operator) {
		Operator = operator;
	}




	public String getChecker() {
		return Checker;
	}




	public void setChecker(String checker) {
		Checker = checker;
	}




	public String getCheckDate() {
		return CheckDate;
	}




	public void setCheckDate(String checkDate) {
		CheckDate = checkDate;
	}




	public String getNotes() {
		return Notes;
	}




	public void setNotes(String notes) {
		Notes = notes;
	}




	public String getAddress() {
		return Address;
	}




	public void setAddress(String address) {
		Address = address;
	}




	public String getLinkTele() {
		return LinkTele;
	}




	public void setLinkTele(String linkTele) {
		LinkTele = linkTele;
	}




	public String getLinkMan() {
		return LinkMan;
	}




	public void setLinkMan(String linkMan) {
		LinkMan = linkMan;
	}




	public String getDelivery() {
		return Delivery;
	}




	public void setDelivery(String delivery) {
		Delivery = delivery;
	}




	public String getPayMode() {
		return PayMode;
	}




	public void setPayMode(String payMode) {
		PayMode = payMode;
	}




	public String getPostFee() {
		return PostFee;
	}




	public void setPostFee(String postFee) {
		PostFee = postFee;
	}




	public String getPayFee() {
		return PayFee;
	}




	public void setPayFee(String payFee) {
		PayFee = payFee;
	}




	public String getDeliverySheetID() {
		return DeliverySheetID;
	}




	public void setDeliverySheetID(String deliverySheetID) {
		DeliverySheetID = deliverySheetID;
	}

	
	

}
