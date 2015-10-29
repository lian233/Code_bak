package com.wofu.ecommerce.alibaba;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Properties;

import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;

public class Alibaba extends Service {
	//检测获取订单线程是否中止
	private static Date currentDate_getOrder=null;
	

	public static Date  getCurrentDate_getOrder() {
		return currentDate_getOrder;
	}

	public synchronized static void setCurrentDate(Date currentDate_getOrder1) {
		currentDate_getOrder = currentDate_getOrder1;
	}
	
	//检测发货线程是否中止
	private static Date currentDate_Order_delivery=null;
	

	public static Date  getCurrentDate_Order_delivery() {
		return currentDate_Order_delivery;
	}

	public synchronized static void setCurrentDate_Order_delivery(Date currentDate_Order_delivery1) {
		currentDate_Order_delivery = currentDate_Order_delivery1;
	}

	public String description() {
		return (new StringBuffer()).append("阿里巴巴接口处理系统[").append(Version.version).append("]").toString();
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
		//获取订单
		getOrders getorders=new getOrders();
		getorders.start();
		
		//获取商品---------------
		getProductList getproductlist=new getProductList();
		getproductlist.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("getproductlist", "发生未捕获异常"+e.getMessage());
				getProductList getproductlist=new getProductList();  //重启线程
				getproductlist.setUncaughtExceptionHandler(this);
				getproductlist.start();
				
			}
			
		});
		getproductlist.start();
		
		
		//发货---------------------
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.start();
		
		//更新库存
		UpdateStock updatestock=new UpdateStock();
		updatestock.start();
		
		//接口生成客户订单
		if(Params.isgenorder){
		GenCustomerOrder genCustomerOrder= new GenCustomerOrder();
		genCustomerOrder.start();
		}
		
		//检测线程
		CheckThread checkThread = new CheckThread();
		checkThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread arg0, Throwable arg1) {
				Log.error("检测线程被中止", arg1.getMessage());
				CheckThread checkThread = new CheckThread();
				checkThread.setUncaughtExceptionHandler(this);
				checkThread.start();
				
			}
			
		});
		checkThread.start();
		
	}
}
