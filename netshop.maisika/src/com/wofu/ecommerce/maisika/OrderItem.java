package com.wofu.ecommerce.maisika;

import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

/**
 * 
 * �����е���Ʒ
 *
 */
public class OrderItem extends BusinessObject{
	private String mid="";//��Ʒ��ʶ--����
	private String sku="";        //�̼�sku  ͨ��v_barcodeall������
	private float price=0.0f;//�г��� 
	private String  title; //����
	private int num=0;//��Ʒ����
	

	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}



	public int getNum() {
		// TODO Auto-generated method stub
		return num;
	}
	
	public void setNum(int num) {
		// TODO Auto-generated method stub
		this.num = num;
	}

	
	
	
	
	
}
