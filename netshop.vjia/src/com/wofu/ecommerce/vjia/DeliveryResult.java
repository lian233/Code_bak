package com.wofu.ecommerce.vjia;

import java.util.ArrayList;
import java.util.Hashtable;

public class DeliveryResult {
	private String orgid = "" ;//����ID
	private String ordercode = "" ;//������
	private int status = -2 ;//״̬ -2:δ��ѯ -1: ���� 0: ��Ͷ 1: �˻�
	private String isupdate = "" ;//�Ƿ��Ѹ��¹�״̬ 0: δ���� 1: �Ѹ���  �Ѹ��¹��Ķ����������ظ�����
	private boolean queryState = false ;
	private ArrayList<Hashtable<String, String>> deliveryNote = new ArrayList<Hashtable<String,String>>() ;//Ͷ����Ϣ��¼
	public ArrayList<Hashtable<String, String>> getDeliveryNote() {
		return deliveryNote;
	}
	public void setDeliveryNote(ArrayList<Hashtable<String, String>> deliveryNote) {
		this.deliveryNote = deliveryNote;
	}
	public String getIsupdate() {
		return isupdate;
	}
	public void setIsupdate(String isupdate) {
		this.isupdate = isupdate;
	}
	public String getOrdercode() {
		return ordercode;
	}
	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public boolean getQueryState() {
		return queryState;
	}
	public void setQueryState(boolean queryState) {
		this.queryState = queryState;
	}

	
}
