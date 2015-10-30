package com.wofu.ecommerce.jit;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;
import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;
public class JIT extends Service {
	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "JIT订单处理系统 [V " + Version.version + "]";
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
		
		//测试用
		if(Params.isGetSinglePick){		//获取单个拣货单
			SinglePickGetOrders singlePickGetOrders = new SinglePickGetOrders();
			singlePickGetOrders.start();
		}
		if(Params.isGetSinglePo){	//获取单个订单
			SinglePoGetOrders singlePoGetOrders = new SinglePoGetOrders();
			singlePoGetOrders.start();
		}
		
		//生成客户订单
//		GenCustomerOrder gencustomerorder=new GenCustomerOrder();
//		gencustomerorder.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){
//			public void uncaughtException(Thread thread, Throwable e) {
//				Log.error("gencustomerorder", "发生未捕获异常"+e.getMessage());
//				GenCustomerOrder gencustomerorder = new GenCustomerOrder();  //重启线程
//				gencustomerorder.setUncaughtExceptionHandler(this);
//				gencustomerorder.start();
//
//			}
//		});
//		gencustomerorder.start();
		
		//过期po查询(检查漏单)
//		GetExpireOrders getExpireOrders = new GetExpireOrders();
//		getExpireOrders.start();
		
		//出库
//		OrderDelivery orderdelivery=new OrderDelivery();
//		orderdelivery.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){
//
//			public void uncaughtException(Thread thread, Throwable e) {
//				Log.error("orderdelivery", "发生未捕获异常"+e.getMessage());
//				OrderDelivery orderdelivery=new OrderDelivery();  //重启线程
//				orderdelivery.setUncaughtExceptionHandler(this);
//				orderdelivery.start();
//				
//			}
//			
//		});
//		orderdelivery.start();
	}

}
