package com.wofu.intf.jw;

import com.wofu.base.util.BusinessObject;

public class skuList extends BusinessObject{
	private String skuId="";    //sku��id
	private String skuHgId="0";  //���ر��
	private String isbs="true";     //�Ƿ�˰��Ʒ
	private String hgzc=Params.hgzc;     //�����˲��
	private String hgxh="0";     //�������
	private String ownerCode=Params.ownerCode;//���������Ҫ��WMS����
	private String ownerName=Params.ownerName;//����������Ҫ��WMS����
	private String skuSpecId=""; //��Ʒ�����Ϣ
	private String outerId="";   //outerId	�̼����õ��ⲿid
	private String barcode="";   //��Ʒ�����������(SKUID)
	private String numIid="0";    //sku������Ʒ����id(��д0)
	private String quantity="0";  //��ƷSKU������
	private String price="";     //��ƷSKU�ļ۸�
	private String status="normal";    //sku״̬  normal:���� ��delete:ɾ��;putaway���ϼ�;soldout���¼�
	private String type="ȫ������";      //�����·�ʽ ��ѡ��ȫ������
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
