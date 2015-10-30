package com.wofu.ecommerce.weidian2;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;

import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;
public class Weidian2 extends Service
{

	@Override
	public String description()
	{
		return "΢�궩������ϵͳ [V " + Version.version + "]";
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
				Log.error("getorders", "����δ�����쳣"+e.getMessage());
				GetOrders getorders = new GetOrders();  //�����߳�
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				Log.info("����ִ�У�getorders�߳�");
			}
		});
		getorders.start();
		
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("orderdelivery", "����δ�����쳣"+e.getMessage());
				OrderDelivery orderdelivery=new OrderDelivery();  //�����߳�
				orderdelivery.setUncaughtExceptionHandler(this);
				orderdelivery.start();
				System.out.println("����ִ�У�OrderDelivery�߳�");
			}
			
		});
		orderdelivery.start();
		/**
		GenCustomerOrder gencustomerorder=new GenCustomerOrder();
		gencustomerorder.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("gencustomerorder", "����δ�����쳣"+e.getMessage());
				GenCustomerOrder gencustomerorder = new GenCustomerOrder();  //�����߳�
				gencustomerorder.setUncaughtExceptionHandler(this);
				gencustomerorder.start();
				System.out.println("����������GenCustomerOrder�߳�");
			}
			
		});
		gencustomerorder.start();
		**/
		
		UpdateStock updatestock=new UpdateStock();
		updatestock.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("updatestock", "����δ�����쳣"+e.getMessage());
				UpdateStock updatestock=new UpdateStock();  //�����߳�
				updatestock.setUncaughtExceptionHandler(this);
				updatestock.start();
				System.out.println("����ִ�У�UpdateStock�߳�");
			}
			
		});
		updatestock.start();		
		
	}

}
