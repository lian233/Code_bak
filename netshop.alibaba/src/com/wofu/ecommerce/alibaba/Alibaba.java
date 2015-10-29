package com.wofu.ecommerce.alibaba;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Properties;

import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;

public class Alibaba extends Service {
	//����ȡ�����߳��Ƿ���ֹ
	private static Date currentDate_getOrder=null;
	

	public static Date  getCurrentDate_getOrder() {
		return currentDate_getOrder;
	}

	public synchronized static void setCurrentDate(Date currentDate_getOrder1) {
		currentDate_getOrder = currentDate_getOrder1;
	}
	
	//��ⷢ���߳��Ƿ���ֹ
	private static Date currentDate_Order_delivery=null;
	

	public static Date  getCurrentDate_Order_delivery() {
		return currentDate_Order_delivery;
	}

	public synchronized static void setCurrentDate_Order_delivery(Date currentDate_Order_delivery1) {
		currentDate_Order_delivery = currentDate_Order_delivery1;
	}

	public String description() {
		return (new StringBuffer()).append("����Ͱͽӿڴ���ϵͳ[").append(Version.version).append("]").toString();
	}

	public void end() throws Exception {
		
	}

	public void init(Properties prop) throws Exception {
		Params.init(prop);
		this.setParams(prop);
	}

	public void process() {
		
	}

	public void start() throws Exception {
		//��ȡ����
		getOrders getorders=new getOrders();
		getorders.start();
		
		//��ȡ��Ʒ---------------
		getProductList getproductlist=new getProductList();
		getproductlist.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("getproductlist", "����δ�����쳣"+e.getMessage());
				getProductList getproductlist=new getProductList();  //�����߳�
				getproductlist.setUncaughtExceptionHandler(this);
				getproductlist.start();
				
			}
			
		});
		getproductlist.start();
		
		
		//����---------------------
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.start();
		
		//���¿��
		UpdateStock updatestock=new UpdateStock();
		updatestock.start();
		
		//�ӿ����ɿͻ�����
		if(Params.isgenorder){
		GenCustomerOrder genCustomerOrder= new GenCustomerOrder();
		genCustomerOrder.start();
		}
		
		//����߳�
		CheckThread checkThread = new CheckThread();
		checkThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread arg0, Throwable arg1) {
				Log.error("����̱߳���ֹ", arg1.getMessage());
				CheckThread checkThread = new CheckThread();
				checkThread.setUncaughtExceptionHandler(this);
				checkThread.start();
				
			}
			
		});
		checkThread.start();
		
	}
}
