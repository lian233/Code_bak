package com.wofu.ecommerce.coo8;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Properties;

import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;

public class Coo8 extends Service {
	public String description() {
		return (new StringBuffer()).append("��ͽӿڴ���ϵͳ[").append(Version.version).append("]").toString();
	}

	public void end() throws Exception {
		
	}

	public void init(Properties prop) throws Exception {
		Params.init(prop);
		this.setParams(prop);
	}

	public void process() {
		
	}
	private static Date currentDate_DevOrder;
	

	public static Date getCurrentDate_DevOrder() {
		return currentDate_DevOrder;
	}

	public synchronized static void setCurrentDate_DevOrder(Date currentDate_DevOrder) {
		Coo8.currentDate_DevOrder = currentDate_DevOrder;
	}

	public void start() throws Exception {
		//��ȡ����
		getOrders getorders=new getOrders();
		getorders.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("getorders", "����δ�����쳣"+e.getMessage());
				getOrders getorders=new getOrders(); //��������һ���µ��߳�
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				
			}
			
		});
		getorders.start();
		
		//��ȡ��Ʒ
		//getProducts getproducts=new getProducts();
		//getproducts.start();
		//����
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("orderdelivery", "����δ�����쳣"+e.getMessage());
				OrderDelivery orderdelivery=new OrderDelivery(); //��������һ���µ��߳�
				orderdelivery.setUncaughtExceptionHandler(this);
				orderdelivery.start();
				
			}
			
		});
		orderdelivery.start();
		
		GenCustomerOrder gencustomerorder=new GenCustomerOrder();
		gencustomerorder.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("gencustomerorder", "����δ�����쳣"+e.getMessage());
				GenCustomerOrder gencustomerorder=new GenCustomerOrder();  //�����߳�
				gencustomerorder.setUncaughtExceptionHandler(this);
				gencustomerorder.start();
				
			}
			
		});
		gencustomerorder.start();
		
		//���¿��
		UpdateStock updatestock=new UpdateStock();
		updatestock.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("updatestock", "����δ�����쳣"+e.getMessage());
				UpdateStock updatestock=new UpdateStock(); //��������һ���µ��߳�
				updatestock.setUncaughtExceptionHandler(this);
				updatestock.start();
				
			}
			
		});
		updatestock.start();
		//��ⷢ���߳�
		CheckDeliveryOrerThread checkDeliveryOrerThread = new CheckDeliveryOrerThread();
		checkDeliveryOrerThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread arg0, Throwable e) {
				Log.error("orderDelivery", "����δ�����쳣"+e.getMessage());
				CheckDeliveryOrerThread checkDeliveryOrerThread = new CheckDeliveryOrerThread();
				checkDeliveryOrerThread.setUncaughtExceptionHandler(this);
				checkDeliveryOrerThread.start();
				
			}
			
		});
		checkDeliveryOrerThread.start();
	}
}
