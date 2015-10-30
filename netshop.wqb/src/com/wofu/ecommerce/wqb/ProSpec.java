package com.wofu.ecommerce.wqb;

import com.wofu.base.util.BusinessObject;

public class ProSpec extends BusinessObject{
	private String proTitle;//商品标题
	private String proNo;//商品货号
	private String proSku;//商品Sku
	private float proPrice;//商品金额
	private String AreaNo;//商品库位
	private int proCount;//商品数量
	private String ProColorName;//商品颜色名称
	private String ProSizesName;//商品规格名称
	private String Pro_Unit;//商品Sku
	private float fxPrice;//分销价格
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
