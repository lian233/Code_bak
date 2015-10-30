package com.wofu.ecommerce.ylzx;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;
import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;
public class YLZX extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "���������̳Ƕ�������ϵͳ [V " + Version.version + "]";
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
				Log.error("getorders", "����δ�����쳣"+e.getMessage());
				GetOrders getorders = new GetOrders();  //�����߳�
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				
			}
			
		});
		getorders.start();
		/**
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
		//yyk��
		
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
		
		
		
		
		
		
		getRefund getrefund=new getRefund();
		getrefund.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("getrefund", "����δ�����쳣"+e.getMessage());
				getRefund getrefund=new getRefund();  //�����߳�
				getrefund.setUncaughtExceptionHandler(this);
				getrefund.start();
			}
			
		});
		getrefund.start();
	**/
		
			
	}

}
