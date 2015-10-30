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
		return "邮乐网订单处理系统 [V " + Version.version + "]";
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
		//----获取订单（新订单）
		GetOrders getorders = new GetOrders();
		getorders.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread arg0, Throwable e) {
				Log.error("getorders", "发生未捕获异常"+e.getMessage());
				GetOrders getorders = new GetOrders();
				getorders.setUncaughtExceptionHandler(this);
				getorders.start();
				
			}
			
		});
		getorders.start();
		
		//----获取邮乐网 上架商品
		//getItems getitems = new getItems() ;
		//getitems.start() ;
		
	/*	//---获取邮乐网 退换货单
		GetRefundOrders getRefundOrders = new GetRefundOrders() ;
		getRefundOrders.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread arg0, Throwable e) {
				Log.error("getRefundOrders", "发生未捕获异常"+e.getMessage());
				GetRefundOrders getRefundOrders = new GetRefundOrders() ;
				getRefundOrders.setUncaughtExceptionHandler(this);
				getRefundOrders.start();
				
			}
			
		});
		getRefundOrders.start() ;
		
		//----生成系统内部订单
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread arg0, Throwable e) {
				Log.error("getRefundOrders", "发生未捕获异常"+e.getMessage());
				GenCustomerOrder gencustomerorder = new GenCustomerOrder() ;
				gencustomerorder.setUncaughtExceptionHandler(this);
				gencustomerorder.start();
				
			}
			
		});
		gencustomerorder.start();
		
		//更新订单状态（审核确认，退货收到确认）
		//UpdateStatus updatestatus=new UpdateStatus();
		//updatestatus.start();
		
		//---更新订单发货信息
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread arg0, Throwable e) {
				Log.error("orderdelivery", "发生未捕获异常"+e.getMessage());
				OrderDelivery orderdelivery = new OrderDelivery() ;
				orderdelivery.setUncaughtExceptionHandler(this);
				orderdelivery.start();
				
			}
			
		});
		orderdelivery.start() ;
		
		//--更新库存  yyk停
		UpdateStock updatestock=new UpdateStock();
		updatestock.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			public void uncaughtException(Thread arg0, Throwable e) {
				Log.error("updatestock", "发生未捕获异常"+e.getMessage());
				UpdateStock updatestock = new UpdateStock() ;
				updatestock.setUncaughtExceptionHandler(this);
				updatestock.start();
				
			}
			
		});
		updatestock.start();*/
	}
}

