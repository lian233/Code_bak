package com.wofu.ecommerce.dangdang;

import java.util.Date;
import java.util.Properties;

import com.wofu.ecommerce.dangdang.Params;
import com.wofu.common.service.Service;
/**
 * 
 * 启动总类
 *
 */
public class Dangdang extends Service {
	//检测获取订单线程是否中止
	private static Date currentDate_getOrder=null;
	

	public static Date  getCurrentDate_getOrder() {
		return currentDate_getOrder;
	}

	public synchronized static void setCurrentDate_getOrder(Date currentDate_getOrder1) {
		currentDate_getOrder = currentDate_getOrder1;
	}
	
	//检测获取发货线程是否中止
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
		return "当当订单处理系统 [V " + Version.version + "]";
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
		//获取订单（新订单）
		GetOrders getorders = new GetOrders();
		getorders.start();
		
		//获取当当上架商品
		getItems getitems = new getItems() ;
		getitems.start() ;
		
		//获取当当退换货单
		GetRefundOrders getRefundOrders = new GetRefundOrders() ;
		getRefundOrders.start() ;
		
		//生成系统内部订单
		if(Params.isgenorder){
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		}
		
		//更新订单状态（审核确认，退货收到确认）
		UpdateStatus updatestatus=new UpdateStatus();
		updatestatus.start();
		
		
		//更新订单发货信息
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.start() ;
		
		//更新库存
		
		UpdateStock updatestock=new UpdateStock();
		updatestock.start();
		
		//检测线程的启动
		CheckDeliveryOrerThread checkDeliveryOrerThread = new  CheckDeliveryOrerThread();
		checkDeliveryOrerThread.start();
		
		CheckThread  checkThread = new CheckThread();
		checkThread.start();
		
	}
}

