package com.wofu.ecommerce.jingdong;
import java.util.Date;
import java.util.Properties;
import com.wofu.common.service.Service;
import com.wofu.ecommerce.jingdong.Params;
public class Jingdong extends Service {
	//检测获取订单线程是否中止
	private static Date currentDate_getOrder=null;
	

	public static Date  getCurrentDate_getOrder() {
		return currentDate_getOrder;
	}

	public synchronized static void setCurrentDate_getOrder(Date currentDate_getOrder1) {
		currentDate_getOrder = currentDate_getOrder1;
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
	
	//检测获取取商品线程是否中止
	private static Date currentDate_getItem=null;
	

	public static Date  getCurrentDate_getItem() {
		return currentDate_getItem;
	}

	public synchronized static void setCurrentDate_getItem(Date currentDate_getItem1) {
		currentDate_getItem = currentDate_getItem1;
	}
	
	//检测更新库存线程是否中止
	private static Date currentDate_updatStock=null;
	

	public static Date  getCurrentDate_updatStock() {
		return currentDate_updatStock;
	}

	public synchronized static void setCurrentDate_updatStock(Date currentDate_updatStock1) {
		currentDate_updatStock = currentDate_updatStock1;
	}

	public String description() {
		return "360buy订单处理系统 [V " + Version.version + "]";
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
		
		//获取订单（新订单，取消的订单，退货单）
		getOrders getorders = new getOrders();
		
		getorders.start();
		
		
		//获取京东最新修改的商品 V2
		getItems getitems = new getItems() ;
		getitems.start() ;
		
		//生成系统内部订单
		System.out.println("生成订单作业是否开启"+Params.isgenorder);
		if(Params.isgenorder){
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		}
		

		
		//更新订单发货信息 V2  百丽停
		OrderDelivery orderdelivery = new OrderDelivery() ;
		
		orderdelivery.start() ;
		
		
		//更新库存 V2
		
//		UpdateStock updatestock=new UpdateStock();
//		updatestock.start();
		
		
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
		//检测取商品线程
		CheckGetItemThread checkGetItemThread= new CheckGetItemThread();
		checkGetItemThread.start();
		// 检测更新库存线程
		CheckUpdateStockThread checkUpdateStockThread = new CheckUpdateStockThread();
		checkUpdateStockThread.start();
		
		
	}
}

