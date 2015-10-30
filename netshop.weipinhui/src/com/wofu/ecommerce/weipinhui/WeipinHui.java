package com.wofu.ecommerce.weipinhui;
import java.util.Date;
import java.util.Properties;

import com.wofu.ecommerce.weipinhui.Params;
import com.wofu.common.service.Service;

public class WeipinHui extends Service {
	//检测获取订单线程是否中止
	private static Date currentDate_getOrder=null;
	public static Date  getCurrentDate_getOrder() { return currentDate_getOrder; }
	public synchronized static void setCurrentDate_getOrder(Date currentDate_getOrder1) {
		currentDate_getOrder = currentDate_getOrder1;
	}
	
	//检测发货线程是否中止
	private static Date currentDate_orderDelivery=null;
	public static Date getCurrentDate_orderDelivery() { return currentDate_orderDelivery; }
	public synchronized static void setCurrentDate_orderDelivery(Date currentDate_orderDelivery1) {
		currentDate_orderDelivery = currentDate_orderDelivery1;
	}
	
	@Override
	public String description() {
		return "唯品会订单处理系统 [V " + Version.version + "]";
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
		//----获取订单（新订单）
		GetOrders getorders = new GetOrders();
		getorders.start();
		
		//----生成系统内部订单
		if(Params.isgenorder){
			GenCustomerOrder gencustomerorder = new GenCustomerOrder();
			gencustomerorder.start();
		}
		
		//---订单发货
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.start();
		
		//检测线程
		CheckDeliveryOrderThread checkDeliveryOrderThread= new CheckDeliveryOrderThread();
		checkDeliveryOrderThread.start();
		
		CheckGetOrderThread checkGetOrderThread = new CheckGetOrderThread();
		checkGetOrderThread.start();
	}
}

