package com.wofu.ecommerce.ecshop;
import java.util.Date;
import java.util.Properties;

import com.wofu.ecommerce.ecshop.Params;
import com.wofu.common.service.Service;
public class Ecshop extends Service {

	//����ȡ�����߳��Ƿ���ֹ
	private static Date currentDate_getOrder=null;
	

	public static Date  getCurrentDate_getOrder() {
		return currentDate_getOrder;
	}

	public synchronized static void setCurrentDate_getOrder(Date currentDate_getOrder1) {
		currentDate_getOrder = currentDate_getOrder1;
	}
	
	//����ȡ�˻����߳��Ƿ���ֹ
	private static Date currentDate_getRefundOrder=null;
	

	public static Date  getCurrentDate_getRefundOrder() {
		return currentDate_getRefundOrder;
	}

	public synchronized static void setCurrentDate_getRefundOrder(Date currentDate_getRefundOrder1) {
		currentDate_getRefundOrder = currentDate_getRefundOrder1;
	}
	
	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "ecshop��������ϵͳ [V " + Version.version + "]";
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
		//----��ȡ�������¶�����
		GetOrders getorders = new GetOrders();
		getorders.start();
		
		//----��ȡecshop �ϼ���Ʒ
		//GetItems getitems = new GetItems() ;
		//getitems.start() ;
		
		
		//----����ϵͳ�ڲ�����
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		
		
		//---���¶���������Ϣ
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.start();
		
		//����߳�
		CheckGetOrderThread checkGetOrderThread = new CheckGetOrderThread();
		checkGetOrderThread.start();
		
		
		//---��ȡecshop �˻�����
		GetRefundOrders getRefundOrders = new GetRefundOrders() ;
		getRefundOrders.start() ; 
		
		//����߳�
		CheckGetRefundOrderThread checkGetRefundOrderThread = new CheckGetRefundOrderThread();
		checkGetRefundOrderThread.start();
		
		
		
		
		
		
	}
}

