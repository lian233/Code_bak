package com.wofu.ecommerce.miya;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;
import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.miya.CheckOrder;
import com.wofu.ecommerce.miya.OrderDelivery;
import com.wofu.ecommerce.miya.Params;
import com.wofu.ecommerce.miya.Version;
public class MiYa extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "蜜芽网订单处理系统 [V " + Version.version + "]";
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
		
//		//抓取订单
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
//		//生成订单
		if(Params.isgenorder){
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
		}
//		
//		if(Params.isgenorderRet){
//			genCustomerRet gencustomerret=new genCustomerRet();
//			gencustomerret.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){
//
//				public void uncaughtException(Thread thread, Throwable e) {
//					Log.error("gencustomerret", "发生未捕获异常"+e.getMessage());
//					genCustomerRet gencustomerret=new genCustomerRet();  //重启线程
//					gencustomerret.setUncaughtExceptionHandler(this);
//					gencustomerret.start();
//					
//				}
//				
//			});
//			gencustomerret.start();
//		}
//		
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
		
		//检查抓单
		CheckOrder checkorder = new CheckOrder();
		checkorder.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				
				Log.error("getorders", "发生未捕获异常"+e.getMessage());
				CheckOrder checkorder = new CheckOrder();  //重启线程
				checkorder.setUncaughtExceptionHandler(this);
				checkorder.start();
				
			}
			
		});
		checkorder.start();
//		
//		
//		
//		
//		
//		
//		GetRefund getrefund=new GetRefund();
//		getrefund.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){
//
//			public void uncaughtException(Thread thread, Throwable e) {
//				Log.error("getrefund", "发生未捕获异常"+e.getMessage());
//				GetRefund getrefund=new GetRefund();  //重启线程
//				getrefund.setUncaughtExceptionHandler(this);
//				getrefund.start();
//			}
//			
//		});
//		getrefund.start();
	
		
			
	}

}
