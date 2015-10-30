package com.wofu.ecommerce.dangdang;

import java.util.Date;
import java.util.Properties;

import com.wofu.ecommerce.dangdang.Params;
import com.wofu.common.service.Service;
/**
 * 
 * ��������
 *
 */
public class Dangdang extends Service {
	//����ȡ�����߳��Ƿ���ֹ
	private static Date currentDate_getOrder=null;
	

	public static Date  getCurrentDate_getOrder() {
		return currentDate_getOrder;
	}

	public synchronized static void setCurrentDate_getOrder(Date currentDate_getOrder1) {
		currentDate_getOrder = currentDate_getOrder1;
	}
	
	//����ȡ�����߳��Ƿ���ֹ
	private static Date currentDate_DevOrder=null;
	

	public static Date  getCurrentDate_DevOrder() {
		return currentDate_DevOrder;
	}

	public synchronized static void setCurrentDate_DevOrder(Date currentDate_DevOrder1) {
		currentDate_DevOrder = currentDate_DevOrder1;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "������������ϵͳ [V " + Version.version + "]";
	}

	@Override
	public void end() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(Properties prop) throws Exception {
		// TODO Auto-generated method stub
		Params.init(prop);
		this.setParams(prop);

	}

	@Override
	public void process() {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() throws Exception {
		
		// TODO Auto-generated method stub
		//��ȡ�������¶�����
		GetOrders getorders = new GetOrders();
		getorders.start();
		
		//��ȡ�����ϼ���Ʒ
		getItems getitems = new getItems() ;
		getitems.start() ;
		
		//��ȡ�����˻�����
		GetRefundOrders getRefundOrders = new GetRefundOrders() ;
		getRefundOrders.start() ;
		
		//����ϵͳ�ڲ�����
		if(Params.isgenorder){
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		}
		
		//���¶���״̬�����ȷ�ϣ��˻��յ�ȷ�ϣ�
		UpdateStatus updatestatus=new UpdateStatus();
		updatestatus.start();
		
		
		//���¶���������Ϣ
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.start() ;
		
		//���¿��
		
		UpdateStock updatestock=new UpdateStock();
		updatestock.start();
		
		//����̵߳�����
		CheckDeliveryOrerThread checkDeliveryOrerThread = new  CheckDeliveryOrerThread();
		checkDeliveryOrerThread.start();
		
		CheckThread  checkThread = new CheckThread();
		checkThread.start();
		
	}
}

