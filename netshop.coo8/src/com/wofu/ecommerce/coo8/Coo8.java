package com.wofu.ecommerce.coo8;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Properties;

import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;

public class Coo8 extends Service {
	public String description() {
		return (new StringBuffer()).append("库巴接口处理系统[").append(Version.version).append("]").toString();
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
		//获取订单
		getOrders getorders=new getOrders();
		getorders.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("getorders", "发生未捕获异常"+e.getMessage());
				getOrders getorders=new getOrders(); //重新启动一个新的线程
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				
			}
			
		});
		getorders.start();
		
		//获取商品
		//getProducts getproducts=new getProducts();
		//getproducts.start();
		//发货
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("orderdelivery", "发生未捕获异常"+e.getMessage());
				OrderDelivery orderdelivery=new OrderDelivery(); //重新启动一个新的线程
				orderdelivery.setUncaughtExceptionHandler(this);
				orderdelivery.start();
				
			}
			
		});
		orderdelivery.start();
		
		GenCustomerOrder gencustomerorder=new GenCustomerOrder();
		gencustomerorder.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("gencustomerorder", "发生未捕获异常"+e.getMessage());
				GenCustomerOrder gencustomerorder=new GenCustomerOrder();  //重启线程
				gencustomerorder.setUncaughtExceptionHandler(this);
				gencustomerorder.start();
				
			}
			
		});
		gencustomerorder.start();
		
		//更新库存
		UpdateStock updatestock=new UpdateStock();
		updatestock.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("updatestock", "发生未捕获异常"+e.getMessage());
				UpdateStock updatestock=new UpdateStock(); //重新启动一个新的线程
				updatestock.setUncaughtExceptionHandler(this);
				updatestock.start();
				
			}
			
		});
		updatestock.start();
		//检测发货线程
		CheckDeliveryOrerThread checkDeliveryOrerThread = new CheckDeliveryOrerThread();
		checkDeliveryOrerThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread arg0, Throwable e) {
				Log.error("orderDelivery", "发生未捕获异常"+e.getMessage());
				CheckDeliveryOrerThread checkDeliveryOrerThread = new CheckDeliveryOrerThread();
				checkDeliveryOrerThread.setUncaughtExceptionHandler(this);
				checkDeliveryOrerThread.start();
				
			}
			
		});
		checkDeliveryOrerThread.start();
	}
}
