package com.wofu.intf.jw;

import com.wofu.base.util.BusinessObject;

public class skuList extends BusinessObject{
	private String skuId="";    //sku的id
	private String skuHgId="0";  //海关编号
	private String isbs="true";     //是否保税商品
	private String hgzc=Params.hgzc;     //海关账册号
	private String hgxh="0";     //海关项号
	private String ownerCode=Params.ownerCode;//货主编号需要向WMS申请
	private String ownerName=Params.ownerName;//货主名称需要向WMS申请
	private String skuSpecId=""; //产品规格信息
	private String outerId="";   //outerId	商家设置的外部id
	private String barcode="";   //商品级别的条形码(SKUID)
	private String numIid="0";    //sku所属商品数字id(填写0)
	private String quantity="0";  //商品SKU的数量
	private String price="";     //商品SKU的价格
	private String status="normal";    //sku状态  normal:正常 ；delete:删除;putaway：上架;soldout：下架
	private String type="全量更新";      //库存更新方式 可选。全量更新
	public String getSkuId() {
		return skuId;
	}
	public void setSkuid(String skuId) {
		this.skuId = skuId;
	}
	public String getSkuHgId() {
		return skuHgId;
	}
	public void setSkuhgid(String skuHgId) {
		this.skuHgId = skuHgId;
	}
	public String getIsbs() {
		return isbs;
	}
	public void setIsbs(String isbs) {
		this.isbs = isbs;
	}
	public String getHgzc() {
		return hgzc;
	}
	public void setHgzc(String hgzc) {
		this.hgzc = hgzc;
	}
	public String getHgxh() {
		return hgxh;
	}
	public void setHgxh(String hgxh) {
		this.hgxh = hgxh;
	}
	public String getOwnerCode() {
		return ownerCode;
	}
	public void setOwnercode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnername(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getSkuSpecId() {
		return skuSpecId;
	}
	public void setSkuspecid(String skuSpecId) {
		this.skuSpecId = skuSpecId;
	}
	public String getOuterId() {
		return outerId;
	}
	public void setOuterid(String outerId) {
		this.outerId = outerId;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getNumIid() {
		return numIid;
	}
	public void setNumiid(String numIid) {
		this.numIid = numIid;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
