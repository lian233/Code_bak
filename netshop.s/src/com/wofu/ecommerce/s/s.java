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
		return "��Ь�ⶩ������ϵͳ [V " + Version.version + "]";
	}

	@Override
	public void end() throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Properties prop) throws Exception
	{
		/*Params����ʱû�У���Ϊ��֪���ǶѲ����ܲ���ֱ�Ӹ��ƹ���ʹ�ã��Ȳ���
		     ��*/
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
				
			}
			
		});
		gencustomerorder.start();
		
		genCustomerRet gencustomerret=new genCustomerRet();
		gencustomerret.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("gencustomerret", "����δ�����쳣"+e.getMessage());
				genCustomerRet gencustomerret=new genCustomerRet();  //�����߳�
				gencustomerret.setUncaughtExceptionHandler(this);
				gencustomerret.start();
				
			}
			
		});
		gencustomerret.start();
		
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("orderdelivery", "����δ�����쳣"+e.getMessage());
				OrderDelivery orderdelivery=new OrderDelivery();  //�����߳�
				orderdelivery.setUncaughtExceptionHandler(this);
				orderdelivery.start();
				
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
				
			}
			
		});
		updatestock.start();
	}

}
