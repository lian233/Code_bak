package com.wofu.ecommerce.suning;
import java.util.Date;
import java.util.Properties;
import com.wofu.ecommerce.suning.Params;
import com.wofu.common.service.Service;
public class SuNing extends Service {
	
	//检测获取订单线程是否中止
	private static Date currentDate_getOrder=null;
	

	public static Date  getCurrentDate_getOrder() {
		return currentDate_getOrder;
	}

	public synchronized static void setCurrentDate_getOrder(Date currentDate_getOrder1) {
		currentDate_getOrder = currentDate_getOrder1;
	}
	
	//检测获取退货订单线程是否中止
	private static Date currentDate_getRefundOrder=null;
	

	public static Date  getCurrentDate_getRefundOrder() {
		return currentDate_getRefundOrder;
	}

	public synchronized static void setCurrentDate_getRefundOrder(Date currentDate_RefundgetOrder1) {
		currentDate_getRefundOrder = currentDate_RefundgetOrder1;
	}
	
	//检测生成内部订单线程是否中止
	private static Date currentDate_genOrder=null;
	

	public static Date  getCurrentDate_genOrder() {
		return currentDate_genOrder;
	}

	public synchronized static void setCurrentDate_genOrder(Date currentDate_genOrder1) {
		currentDate_genOrder = currentDate_genOrder1;
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
		return "苏宁订单处理系统 [V " + Version.version + "]";
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
		
		//----获取苏宁 上架商品
		//getItems getitems = new getItems() ;
		//getitems.start() ;
		
		//---获取苏宁 退换货单
		GetRefundOrders getRefundOrders = new GetRefundOrders() ;
		getRefundOrders.start() ;
		System.out.println("是否生成订单:"+Params.isgenorder);
		//----生成系统内部订单
		if(Params.isgenorder){
			GenCustomerOrder gencustomerorder = new GenCustomerOrder();
			gencustomerorder.start();
		}
		
		//---更新订单发货信息
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.start() ;
		
		//--更新库存  yyk停
		
		UpdateStock updatestock=new UpdateStock();
		updatestock.start();
		
		//检测发货线程
		CheckDeliveryOrerThread checkDeliveryOrerThread = new CheckDeliveryOrerThread();
		checkDeliveryOrerThread.start();
		
		//检测生成内部临时订单
		if(Params.isgenorder){
		CheckGenOrerThread checkGenOrerThread= new CheckGenOrerThread();
		checkGenOrerThread.start();
		}
		//检测获取订单线程
		CheckThread checkThread= new CheckThread();
		checkThread.start();
		//检测获取退货订单线程
		CheckgetRefundOrerThread checkgetRefundOrerThread= new CheckgetRefundOrerThread();
		checkgetRefundOrerThread.start();
		
	}
}

