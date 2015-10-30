package com.wofu.ecommerce.amazon;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;

import com.wofu.ecommerce.amazon.Params;
import com.wofu.ecommerce.amazon.Version;
import com.wofu.ecommerce.amazon.getOrders;
import com.wofu.common.service.Service;
import com.wofu.common.tools.util.log.Log;

public class Amazon extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "亚马逊订单处理系统 [V " + Version.version + "]";
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
		//------------------
		getOrders getorders = new getOrders();
		getorders.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("getorders", "发生未捕获异常"+e.getMessage());
				getOrders getorders = new getOrders();
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();  //重启线程
				
			}
			
		});
		getorders.start();
		
		
		
		UpdateStatus updatestatus=new UpdateStatus();
		updatestatus.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("updatestatus", "发生未捕获异常"+e.getMessage());
				UpdateStatus updatestatus=new UpdateStatus();
				updatestatus.setUncaughtExceptionHandler(this);
				updatestatus.start();  //重启线程
				
			}
			
		});
		updatestatus.start();
		
		if(Params.isgenorder){
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("gencustomerorder", "发生未捕获异常"+e.getMessage());
				GenCustomerOrder gencustomerorder = new GenCustomerOrder();
				gencustomerorder.setUncaughtExceptionHandler(this);
				gencustomerorder.start();  //重启线程
				
			}
			
		});
		gencustomerorder.start();
		}
		
		//GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		//gencustomerorder.start();
		
		
		//ProcessOrderStatus processorderstatus=new ProcessOrderStatus();
		//processorderstatus.start();
		
		//-----------------
		UpdateStock updatestock=new UpdateStock();
		updatestock.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				Log.error("gencustomerorder", "发生未捕获异常"+e.getMessage());
				UpdateStock updatestock=new UpdateStock();
				updatestock.setUncaughtExceptionHandler(this);
				updatestock.start();  //重启线程
				
			}
			
		});
		updatestock.start();
	
		
		
		
	}

}
