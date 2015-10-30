package com.wofu.ecommerce.weidian;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;
import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;
public class weidian extends Service
{

	@Override
	public String description()
	{
		return "微店订单处理系统 [V " + Version.version + "]";
	}

	@Override
	public void end() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Properties arg0) throws Exception
	{
		Params.init(arg0);
		this.setParams(arg0);
		
	}

	@Override
	public void process()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() throws Exception
	{
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
		
		if(Params.isgenorder)
		{
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
		}

		
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
		
		
	}

}
