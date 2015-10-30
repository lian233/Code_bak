package com.wofu.ecommerce.jiaju;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;
import com.wofu.ecommerce.jiaju.Params;
import com.wofu.ecommerce.jiaju.GetOrders;
import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;

public class jiaju extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "家居就订单处理系统 [V " + Version.version + "]";
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
		//----获取订单
		GetOrders getorders = new GetOrders();
		getorders.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread thread, Throwable e) 
			{//重启线程
				Log.error("getorders", "发生未捕获异常"+e.getMessage());
				GetOrders getorders = new GetOrders();  
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				Log.info("正在执行：GetOrders线程");
			}
		});
		getorders.start();

		//----更新订单发货信息
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread thread, Throwable e) 
			{//重启线程
				Log.error("getorders", "发生未捕获异常"+e.getMessage());
				GetOrders getorders = new GetOrders();  
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				Log.info("正在执行：GetOrders线程");
			}
		});
		orderdelivery.start();
		
		//生成客户订单
		GenCustomerOrder genCustomerOrder = new GenCustomerOrder();
		genCustomerOrder.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread thread, Throwable e) 
			{//重启线程
				Log.error("getorders", "发生未捕获异常"+e.getMessage());
				GetOrders getorders = new GetOrders();  
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				Log.info("正在执行：GetOrders线程");
			}
		});
		genCustomerOrder.start();
	}
}

