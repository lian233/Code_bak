package com.wofu.ecommerce.ming_xie_ku;

import java.util.Properties;
import java.lang.Thread.UncaughtExceptionHandler;
import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;

public class ming_xie_ku extends Service
{

	@Override
	public String description() 
	{
		return "名鞋库订单处理系统 [V " + Version.version + "]";
	}

	@Override
	public void end() throws Exception 
	{

	}

	@Override
	public void init(Properties prop) throws Exception
	{
		Params.init(prop);
		this.setParams(prop);
	}

	@Override
	public void process() 
	{

	}

	@Override
	public void start() throws Exception 
	{
		/**等下逐个类进行测试看是否有一些类
		 * 的方法还没改成自己的工程的
		 * 例如一些错误提示的获取
		 * 和判断**/
		GetOrders getorders=new GetOrders();
		getorders.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread thread, Throwable e) 
			{
				Log.error("getorders", "发生未捕获异常"+e.getMessage());
				GetOrders getorders = new GetOrders();  //重启线程
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				Log.info("正在执行：getorders线程");
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
				System.out.println("正在重启：GenCustomerOrder线程");
			}
			
		});
		gencustomerorder.start();

		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("orderdelivery", "发生未捕获异常"+e.getMessage());
				OrderDelivery orderdelivery=new OrderDelivery();  //重启线程
				orderdelivery.setUncaughtExceptionHandler(this);
				orderdelivery.start();
				System.out.println("正在执行：OrderDelivery线程");
			}
			
		});
		orderdelivery.start();
		
		UpdateStock updatestock=new UpdateStock();
		updatestock.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("updatestock", "发生未捕获异常"+e.getMessage());
				UpdateStock updatestock=new UpdateStock();  //重启线程
				updatestock.setUncaughtExceptionHandler(this);
				updatestock.start();
				System.out.println("正在执行：UpdateStock线程");
			}
			
		});
		updatestock.start();
		//作废订单
		CancelCustomerOrder cancelCustomerOrder = new  CancelCustomerOrder();
		cancelCustomerOrder.start();
		
	}

}
