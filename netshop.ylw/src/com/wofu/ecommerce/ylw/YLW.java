package com.wofu.ecommerce.ylw;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;

import com.wofu.ecommerce.ylw.Version;
import com.wofu.ecommerce.ylw.Params;
import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;
public class YLW extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "��������������ϵͳ [V " + Version.version + "]";
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
		//----��ȡ�������¶�����
		GetOrders getorders = new GetOrders();
		getorders.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread arg0, Throwable e) {
				Log.error("getorders", "����δ�����쳣"+e.getMessage());
				GetOrders getorders = new GetOrders();
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				
			}
			
		});
		getorders.start();
		
		//----��ȡ������ �ϼ���Ʒ
		//getItems getitems = new getItems() ;
		//getitems.start() ;
		
	/*	//---��ȡ������ �˻�����
		GetRefundOrders getRefundOrders = new GetRefundOrders() ;
		getRefundOrders.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread arg0, Throwable e) {
				Log.error("getRefundOrders", "����δ�����쳣"+e.getMessage());
				GetRefundOrders getRefundOrders = new GetRefundOrders() ;
				getRefundOrders.setUncaughtExceptionHandler(this);
				getRefundOrders.start();
				
			}
			
		});
		getRefundOrders.start() ;
		
		//----����ϵͳ�ڲ�����
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread arg0, Throwable e) {
				Log.error("getRefundOrders", "����δ�����쳣"+e.getMessage());
				GenCustomerOrder gencustomerorder = new GenCustomerOrder() ;
				gencustomerorder.setUncaughtExceptionHandler(this);
				gencustomerorder.start();
				
			}
			
		});
		gencustomerorder.start();
		
		//���¶���״̬�����ȷ�ϣ��˻��յ�ȷ�ϣ�
		//UpdateStatus updatestatus=new UpdateStatus();
		//updatestatus.start();
		
		//---���¶���������Ϣ
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread arg0, Throwable e) {
				Log.error("orderdelivery", "����δ�����쳣"+e.getMessage());
				OrderDelivery orderdelivery = new OrderDelivery() ;
				orderdelivery.setUncaughtExceptionHandler(this);
				orderdelivery.start();
				
			}
			
		});
		orderdelivery.start() ;
		
		//--���¿��  yykͣ
		UpdateStock updatestock=new UpdateStock();
		updatestock.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread arg0, Throwable e) {
				Log.error("updatestock", "����δ�����쳣"+e.getMessage());
				UpdateStock updatestock = new UpdateStock() ;
				updatestock.setUncaughtExceptionHandler(this);
				updatestock.start();
				
			}
			
		});
		updatestock.start();*/
	}
}

