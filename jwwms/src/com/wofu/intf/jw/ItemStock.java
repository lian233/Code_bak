package com.wofu.intf.jw;

import com.wofu.base.util.BusinessObject;

public class ItemStock extends BusinessObject{
	private String goodsId="";  //商品货号
	private String outerId="";  //外部编码
	private String skuId="";    //物料编码
	private String quantity=""; //数量
	private String type="";     //全量更新
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getOuterId() {
		return outerId;
	}
	public void setOuterId(String outerId) {
		this.outerId = outerId;
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
