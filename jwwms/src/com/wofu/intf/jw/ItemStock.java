package com.wofu.intf.jw;

import com.wofu.base.util.BusinessObject;

public class ItemStock extends BusinessObject{
	private String goodsId="";  //��Ʒ����
	private String outerId="";  //�ⲿ����
	private String skuId="";    //���ϱ���
	private String quantity=""; //����
	private String type="";     //ȫ������
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
