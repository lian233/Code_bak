package com.wofu.ecommerce.ecshop;
import java.util.Date;
import java.util.Properties;

import com.wofu.ecommerce.ecshop.Params;
import com.wofu.common.service.Service;
public class Ecshop extends Service {

	//检测获取订单线程是否中止
	private static Date currentDate_getOrder=null;
	

	public static Date  getCurrentDate_getOrder() {
		return currentDate_getOrder;
	}

	public synchronized static void setCurrentDate_getOrder(Date currentDate_getOrder1) {
		currentDate_getOrder = currentDate_getOrder1;
	}
	
	//检测获取退货单线程是否中止
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
		return "ecshop订单处理系统 [V " + Version.version + "]";
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
		//----获取订单（新订单）
		GetOrders getorders = new GetOrders();
		getorders.start();
		
		//----获取ecshop 上架商品
		//GetItems getitems = new GetItems() ;
		//getitems.start() ;
		
		
		//----生成系统内部订单
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		
		
		//---更新订单发货信息
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.start();
		
		//检测线程
		CheckGetOrderThread checkGetOrderThread = new CheckGetOrderThread();
		checkGetOrderThread.start();
		
		
		//---获取ecshop 退换货单
		GetRefundOrders getRefundOrders = new GetRefundOrders() ;
		getRefundOrders.start() ; 
		
		//检测线程
		CheckGetRefundOrderThread checkGetRefundOrderThread = new CheckGetRefundOrderThread();
		checkGetRefundOrderThread.start();
		
		
		
		
		
		
	}
}

