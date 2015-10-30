package com.wofu.ecommerce.wqb;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Properties;
import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;
public class WQB extends Service {
	private static Date deliveryDate;
	
	public static Date getDeliveryDate() {
		return deliveryDate;
	}

	public synchronized static void setDeliveryDate(Date deliveryDate) {
		WQB.deliveryDate = deliveryDate;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "网渠宝订单处理系统 [V " + Version.version + "]";
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
		
			
		GetOrders getorders = new GetOrders();
		getorders.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("getorders", "发生未捕获异常"+e.getMessage());
				GetOrders getorders = new GetOrders();  //重启线程
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				
			}
			
		});
		getorders.start();
		
		
		
		GenCustomerOrder gencustomerorder=new GenCustomerOrder();
		gencustomerorder.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("gencustomerorder", "发生未捕获异常"+e.getMessage());
				GenCustomerOrder gencustomerorder = new GenCustomerOrder();  //重启线程
				gencustomerorder.setUncaughtExceptionHandler(this);
				gencustomerorder.start();
				
			}
			
		});
		gencustomerorder.start();
		
		genCustomerRet gencustomerret=new genCustomerRet();
		gencustomerret.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("gencustomerret", "发生未捕获异常"+e.getMessage());
				genCustomerRet gencustomerret=new genCustomerRet();  //重启线程
				gencustomerret.setUncaughtExceptionHandler(this);
				gencustomerret.start();
				
			}
			
		});
		gencustomerret.start();
		
		
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("orderdelivery", "发生未捕获异常"+e.getMessage());
				OrderDelivery orderdelivery=new OrderDelivery();  //重启线程
				orderdelivery.setUncaughtExceptionHandler(this);
				orderdelivery.start();
				
			}
			
		});
		orderdelivery.start();
		//yyk用
		
		CheckDeliveryOrerThread checkDeliveryOrerThread=new CheckDeliveryOrerThread();
		checkDeliveryOrerThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("updatestock", "发生未捕获异常"+e.getMessage());
				CheckDeliveryOrerThread checkDeliveryOrerThread=new CheckDeliveryOrerThread();
				checkDeliveryOrerThread.setUncaughtExceptionHandler(this);
				checkDeliveryOrerThread.start();
				
			}
			
		});
		checkDeliveryOrerThread.start();
		
		
		
		
		
		/**
		getRefund getrefund=new getRefund();
		getrefund.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("getrefund", "发生未捕获异常"+e.getMessage());
				getRefund getrefund=new getRefund();  //重启线程
				getrefund.setUncaughtExceptionHandler(this);
				getrefund.start();
				
			}
			
		});
		getrefund.start();
		**/
		
		
	}
	

}
