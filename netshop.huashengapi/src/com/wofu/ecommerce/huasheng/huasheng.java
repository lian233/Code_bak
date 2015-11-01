package com.wofu.ecommerce.huasheng;
import java.util.Date;
import java.util.Properties;
import com.wofu.common.service.Service;
import com.wofu.ecommerce.huasheng.CheckDeliveryOrderThread;
import com.wofu.ecommerce.huasheng.GenCustomerOrder;
import com.wofu.ecommerce.huasheng.OrderDelivery;
import com.wofu.ecommerce.huasheng.Params;
import com.wofu.ecommerce.huasheng.Version;

public class huasheng extends Service {
	//检测发货线程是否中止
	private static Date currentDate_orderDelivery=null;
	public static Date getCurrentDate_orderDelivery() { return currentDate_orderDelivery; }
	public synchronized static void setCurrentDate_orderDelivery(Date currentDate_orderDelivery1) {
		currentDate_orderDelivery = currentDate_orderDelivery1;
	}
	
	@Override
	public String description() {
		return "跨境商城(花生API)订单处理系统 [V " + Version.version + "]";
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
		//----生成系统内部订单
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		
		//---订单发货
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.start();
		
		//同步库存
//		UpdateStock updatestock=new UpdateStock();
//		updatestock.start();
		
		//检测线程
		CheckDeliveryOrderThread checkDeliveryOrderThread= new CheckDeliveryOrderThread();
		checkDeliveryOrderThread.start();
	}
}
