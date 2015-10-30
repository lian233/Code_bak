package com.wofu.ecommerce.ecshop;

import java.util.Date;

import com.wofu.common.tools.util.log.Log;

/**
 * 检测线程是否中止
 * @author windows7
 *
 */
public class CheckGetRefundOrderThread extends Thread {
	private static Date getRefundOrderDate=null;
	private static final long CheckTime = 15*60*1000L;
	@Override
	public void run() {
		try{
			for(;;){
				Log.info("检测获取退货订单线程开始!");
				Date date = Ecshop.getCurrentDate_getRefundOrder();
				if(date==null){
					date = new Date();
					Ecshop.setCurrentDate_getRefundOrder(date);
				}else{
					if(getRefundOrderDate==null){
						getRefundOrderDate=date;
					}else{
						if(date.compareTo(getRefundOrderDate)<=0){
							Log.error("检测线程","十五分钟没有检测到获取退货订单的线程活动，准备重启线程");
							GetRefundOrders getRefundOrders = new GetRefundOrders();
							getRefundOrders.start();
						}else{
							getRefundOrderDate=date;
						}
						
					}
				}
				Log.info("检测获取退货订单线程结束!");
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
			Log.error("检查获取退货订单线程出错",ex.getMessage());
		}
	}

}
