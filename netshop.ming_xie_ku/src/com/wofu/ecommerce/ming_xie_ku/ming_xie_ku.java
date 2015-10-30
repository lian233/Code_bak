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
		return "��Ь�ⶩ������ϵͳ [V " + Version.version + "]";
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
		/**�����������в��Կ��Ƿ���һЩ��
		 * �ķ�����û�ĳ��Լ��Ĺ��̵�
		 * ����һЩ������ʾ�Ļ�ȡ
		 * ���ж�**/
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
		//���϶���
		CancelCustomerOrder cancelCustomerOrder = new  CancelCustomerOrder();
		cancelCustomerOrder.start();
		
	}

}
