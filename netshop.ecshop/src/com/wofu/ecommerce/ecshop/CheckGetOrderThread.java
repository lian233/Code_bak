package com.wofu.ecommerce.ecshop;

import java.util.Date;

import com.wofu.common.tools.util.log.Log;

/**
 * 检测线程是否中止
 * @author windows7
 *
 */
public class CheckGetOrderThread extends Thread {
	private static Date getOrderDate=null;
	private static final long CheckTime = 15*60*1000L;
	@Override
	public void run() {
		try{
			for(;;){
				Log.info("检测获取订单线程开始!");
				Date date = Ecshop.getCurrentDate_getOrder();
				if(date==null){
					date = new Date();
					Ecshop.setCurrentDate_getOrder(date);
				}else{
					if(getOrderDate==null){
						getOrderDate=date;
					}else{
						if(date.compareTo(getOrderDate)<=0){
							Log.error("检测线程","十五分钟没有检测到获取订单的线程活动，准备重启线程");
							GetOrders getOrder = new GetOrders();
							getOrder.start();
						}else{
							getOrderDate=date;
						}
						
					}
				}
				Log.info("检测获取订单线程结束!");
				long current = System.currentTimeMillis();
				while(current+CheckTime>System.currentTimeMillis()){
					try{
						Thread.sleep(1000L);
					}catch(Exception e){
						Log.error("检测线程", e.getMessage());
					}
				}
			}
			
		}catch(Exception ex){
			Log.error("检查获取订单线程出错",ex.getMessage());
		}
	}

}
