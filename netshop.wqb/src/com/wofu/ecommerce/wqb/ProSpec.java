package com.wofu.ecommerce.wqb;

import com.wofu.base.util.BusinessObject;

public class ProSpec extends BusinessObject{
	private String proTitle;//��Ʒ����
	private String proNo;//��Ʒ����
	private String proSku;//��ƷSku
	private float proPrice;//��Ʒ���
	private String AreaNo;//��Ʒ��λ
	private int proCount;//��Ʒ����
	private String ProColorName;//��Ʒ��ɫ����
	private String ProSizesName;//��Ʒ�������
	private String Pro_Unit;//��ƷSku
	private float fxPrice;//�����۸�
	public String getProTitle() {
		return proTitle;
	}
	public void setProTitle(String proTitle) {
		this.proTitle = proTitle;
	}
	public String getProNo() {
		return proNo;
	}
	public void setProNo(String proNo) {
		this.proNo = proNo;
	}
	public String getProSku() {
		return proSku;
	}
	public void setProSku(String proSku) {
		this.proSku = proSku;
	}
	public float getProPrice() {
		return proPrice;
	}
	public void setProPrice(float proPrice) {
		this.proPrice = proPrice;
	}
	public int getProCount() {
		return proCount;
	}
	public void setProCount(int proCount) {
		this.proCount = proCount;
	}

	public String getProColorName() {
		return ProColorName;
	}
	public void setProColorName(String proColorName) {
		ProColorName = proColorName;
	}
	public String getProSizesName() {
		return ProSizesName;
	}
	public void setProSizesName(String proSizesName) {
		ProSizesName = proSizesName;
	}
	public String getPro_Unit() {
		return Pro_Unit;
	}
	public void setPro_Unit(String pro_Unit) {
		Pro_Unit = pro_Unit;
	}
	public String getAreaNo() {
		return AreaNo;
	}
	public void setAreaNo(String areaNo) {
		AreaNo = areaNo;
	}
	public float getFxPrice() {
		return fxPrice;
	}
	public void setFxPrice(float fxPrice) {
		this.fxPrice = fxPrice;
	}
	
	
}
