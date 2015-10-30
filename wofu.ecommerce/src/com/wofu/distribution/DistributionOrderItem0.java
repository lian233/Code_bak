package com.wofu.distribution;

import java.util.Properties;

import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

/**
 * 
 * 分销订单商品明细
 *
 */
public class DistributionOrderItem0 extends PageBusinessObject{
	private String barcodeid;//条形码barcodeid
	private String goodsID;//商品货号
	private float basePrice;//基本售价
	private String title;    //标题
	private float customPrice;//客户订单价, 系统以此为准（单价）
	private int stockQty;   //参考库存
	private int allStockQty;   //所有仓库参考库存
	private int orderQty;      //订货数量（实际数量，不含赠品）
	private int orderPQty;     //赠送数量
	private int purQty;        //审批订货数量（实际数量，不含赠品）
	private int purPQty;       //审批赠送数量
	private String PromFlag;       //是否特价(1=特价)
	private String taxRate;        //税率
	private String pKName;         //包装单位
	private String pKSpec;         //包装规格
	private int pKNum;         //包装规格数量
	private String notes;         //明细备注
	private String oID;         //子订单编号
	private float salePrice;         //销售价（网店设置的价格）
	private String paypresentid;     //赠品ID  
	private float distributePrice;  //出货价（出货给分销商的价）
	private String outerSkuID;       //商家编码
	private String outerID;          //商品外部ID
	
	
	
	
	public void search() throws Exception {
		String reqdata = this.getReqData();	
		Properties prop=StringUtil.getIniProperties(reqdata);
		String currpage="1";
		String pagesize="100";
		this.setPagesize(Integer.valueOf(pagesize).intValue());
		this.setCurrpage(Integer.valueOf(currpage).intValue());
		
		String sql="select goodsID,basePrice,BarCodeID,title,customPrice,stockQty,allStockQty,orderQty,orderPQty,purQty," +
				"purPQty,PromFlag,taxRate,pKName,pKSpec,notes,oID,salePrice,isnull(PayPresentID,0) PayPresentID,distributePrice," +
				"isnull(outerSkuID,'') outerSkuID,outerID from customerorderitem0 where 1=1 "+reqdata;
		

		
		this.getRequest().getSession().removeAttribute("search_sql_"+this.getModuleid());
		this.getRequest().getSession().setAttribute("search_sql_"+this.getModuleid(),sql);
		Log.info(this.toPaginationJSONArray(this.getPaginationData(sql,"BarCodeID",this.orderMode)));
		this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sql,"BarCodeID",this.orderMode)));
		
	}




	public String getBarcodeid() {
		return barcodeid;
	}




	public void setBarcodeid(String barcodeid) {
		this.barcodeid = barcodeid;
	}




	public String getGoodsID() {
		return goodsID;
	}




	public void setGoodsID(String goodsID) {
		this.goodsID = goodsID;
	}




	public float getBasePrice() {
		return basePrice;
	}




	public void setBasePrice(float basePrice) {
		this.basePrice = basePrice;
	}




	public String getTitle() {
		return title;
	}




	public void setTitle(String title) {
		this.title = title;
	}




	public float getCustomPrice() {
		return customPrice;
	}




	public void setCustomPrice(float customPrice) {
		this.customPrice = customPrice;
	}




	public int getStockQty() {
		return stockQty;
	}




	public void setStockQty(int stockQty) {
		this.stockQty = stockQty;
	}




	public int getAllStockQty() {
		return allStockQty;
	}




	public void setAllStockQty(int allStockQty) {
		this.allStockQty = allStockQty;
	}




	public int getOrderQty() {
		return orderQty;
	}




	public void setOrderQty(int orderQty) {
		this.orderQty = orderQty;
	}




	public int getOrderPQty() {
		return orderPQty;
	}




	public void setOrderPQty(int orderPQty) {
		this.orderPQty = orderPQty;
	}




	public int getPurQty() {
		return purQty;
	}




	public void setPurQty(int purQty) {
		this.purQty = purQty;
	}




	public int getPurPQty() {
		return purPQty;
	}




	public void setPurPQty(int purPQty) {
		this.purPQty = purPQty;
	}




	public String getPromFlag() {
		return PromFlag;
	}




	public void setPromFlag(String promFlag) {
		PromFlag = promFlag;
	}




	public String getTaxRate() {
		return taxRate;
	}




	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}




	public String getPKName() {
		return pKName;
	}




	public void setPKName(String name) {
		pKName = name;
	}




	public String getPKSpec() {
		return pKSpec;
	}




	public void setPKSpec(String spec) {
		pKSpec = spec;
	}




	public int getPKNum() {
		return pKNum;
	}




	public void setPKNum(int num) {
		pKNum = num;
	}




	public String getNotes() {
		return notes;
	}




	public void setNotes(String notes) {
		this.notes = notes;
	}




	public String getOID() {
		return oID;
	}




	public void setOID(String oid) {
		oID = oid;
	}




	public float getSalePrice() {
		return salePrice;
	}




	public void setSalePrice(float salePrice) {
		this.salePrice = salePrice;
	}




	public String getPaypresentid() {
		return paypresentid;
	}




	public void setPaypresentid(String paypresentid) {
		this.paypresentid = paypresentid;
	}




	public float getDistributePrice() {
		return distributePrice;
	}




	public void setDistributePrice(float distributePrice) {
		this.distributePrice = distributePrice;
	}




	public String getOuterSkuID() {
		return outerSkuID;
	}




	public void setOuterSkuID(String outerSkuID) {
		this.outerSkuID = outerSkuID;
	}




	public String getOuterID() {
		return outerID;
	}




	public void setOuterID(String outerID) {
		this.outerID = outerID;
	}
	
	




	
	

}
