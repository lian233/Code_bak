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
		return "�ҾӾͶ�������ϵͳ [V " + Version.version + "]";
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
		//----��ȡ����
		GetOrders getorders = new GetOrders();
		getorders.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread thread, Throwable e) 
			{//�����߳�
				Log.error("getorders", "����δ�����쳣"+e.getMessage());
				GetOrders getorders = new GetOrders();  
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				Log.info("����ִ�У�GetOrders�߳�");
			}
		});
		getorders.start();

		//----���¶���������Ϣ
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread thread, Throwable e) 
			{//�����߳�
				Log.error("getorders", "����δ�����쳣"+e.getMessage());
				GetOrders getorders = new GetOrders();  
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				Log.info("����ִ�У�GetOrders�߳�");
			}
		});
		orderdelivery.start();
		
		//���ɿͻ�����
		GenCustomerOrder genCustomerOrder = new GenCustomerOrder();
		genCustomerOrder.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread thread, Throwable e) 
			{//�����߳�
				Log.error("getorders", "����δ�����쳣"+e.getMessage());
				GetOrders getorders = new GetOrders();  
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				Log.info("����ִ�У�GetOrders�߳�");
			}
		});
		genCustomerOrder.start();
	}
}

