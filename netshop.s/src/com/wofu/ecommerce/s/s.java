package com.wofu.ecommerce.s;

import java.util.Properties;
import java.lang.Thread.UncaughtExceptionHandler;
import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;

public class s extends Service
{

	@Override
	public String description() 
	{
		// TODO Auto-generated method stub
		return "名鞋库订单处理系统 [V " + Version.version + "]";
	}

	@Override
	public void end() throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Properties prop) throws Exception
	{
		/*Params类暂时没有，因为不知道那堆参数能不能直接复制过来使用，先不管
		     了*/
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
		
		UpdateStock updatestock=new UpdateStock();
		updatestock.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("updatestock", "发生未捕获异常"+e.getMessage());
				UpdateStock updatestock=new UpdateStock();  //重启线程
				updatestock.setUncaughtExceptionHandler(this);
				updatestock.start();
				
			}
			
		});
		updatestock.start();
	}

}
