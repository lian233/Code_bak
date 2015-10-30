package com.wofu.distribution;

import java.util.Properties;

import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

/**
 * 
 * ����������Ʒ��ϸ
 *
 */
public class DistributionOrderItem extends PageBusinessObject{
	private String barcodeid;//������barcodeid
	private String goodsID;//��Ʒ����
	private float basePrice;//�����ۼ�
	private String title;    //����
	private float customPrice;//�ͻ�������, ϵͳ�Դ�Ϊ׼�����ۣ�
	private int stockQty;   //�ο����
	private int allStockQty;   //���вֿ�ο����
	private int orderQty;      //����������ʵ��������������Ʒ��
	private int orderPQty;     //��������
	private int purQty;        //��������������ʵ��������������Ʒ��
	private int purPQty;       //������������
	private String PromFlag;       //�Ƿ��ؼ�(1=�ؼ�)
	private String taxRate;        //˰��
	private String pKName;         //��װ��λ
	private String pKSpec;         //��װ���
	private int pKNum;         //��װ�������
	private String notes;         //��ϸ��ע
	private String oID;         //�Ӷ������
	private float salePrice;         //���ۼۣ��������õļ۸�
	private String paypresentid;     //��ƷID  
	private float distributePrice;  //�����ۣ������������̵ļۣ�
	private String outerSkuID;       //�̼ұ���
	private String outerID;          //��Ʒ�ⲿID
	
	
	
	
	public void search() throws Exception {
		String reqdata = this.getReqData();	
		Properties prop=StringUtil.getIniProperties(reqdata);
		String currpage="1";
		String pagesize="100";
		this.setPagesize(Integer.valueOf(pagesize).intValue());
		this.setCurrpage(Integer.valueOf(currpage).intValue());
		
		String sql="select goodsID,basePrice,BarCodeID,title,customPrice,stockQty,allStockQty,orderQty,orderPQty,purQty," +
				"purPQty,PromFlag,taxRate,pKName,pKSpec,notes,oID,salePrice,isnull(PayPresentID,0) PayPresentID,distributePrice," +
				"isnull(outerSkuID,'') outerSkuID,outerID from customerorderitem with(nolock) where 1=1 "+reqdata;
		

		
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
