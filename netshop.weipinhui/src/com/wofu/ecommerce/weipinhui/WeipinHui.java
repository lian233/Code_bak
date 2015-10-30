package com.wofu.ecommerce.weipinhui;
import java.util.Date;
import java.util.Properties;

import com.wofu.ecommerce.weipinhui.Params;
import com.wofu.common.service.Service;

public class WeipinHui extends Service {
	//����ȡ�����߳��Ƿ���ֹ
	private static Date currentDate_getOrder=null;
	public static Date  getCurrentDate_getOrder() { return currentDate_getOrder; }
	public synchronized static void setCurrentDate_getOrder(Date currentDate_getOrder1) {
		currentDate_getOrder = currentDate_getOrder1;
	}
	
	//��ⷢ���߳��Ƿ���ֹ
	private static Date currentDate_orderDelivery=null;
	public static Date getCurrentDate_orderDelivery() { return currentDate_orderDelivery; }
	public synchronized static void setCurrentDate_orderDelivery(Date currentDate_orderDelivery1) {
		currentDate_orderDelivery = currentDate_orderDelivery1;
	}
	
	@Override
	public String description() {
		return "ΨƷ�ᶩ������ϵͳ [V " + Version.version + "]";
	}

	@Override
	public void end() throws Exception {
		
	}

	@Override
	public void init(Properties prop) throws Exception {
		Params.init(prop);
		this.setParams(prop);
	}

	@Override
	public void process() {

	}

	@Override
	public void start() throws Exception {
		//----��ȡ�������¶�����
		GetOrders getorders = new GetOrders();
		getorders.start();
		
		//----����ϵͳ�ڲ�����
		if(Params.isgenorder){
			GenCustomerOrder gencustomerorder = new GenCustomerOrder();
			gencustomerorder.start();
		}
		
		//---��������
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.start();
		
		//����߳�
		CheckDeliveryOrderThread checkDeliveryOrderThread= new CheckDeliveryOrderThread();
		checkDeliveryOrderThread.start();
		
		CheckGetOrderThread checkGetOrderThread = new CheckGetOrderThread();
		checkGetOrderThread.start();
	}
}

