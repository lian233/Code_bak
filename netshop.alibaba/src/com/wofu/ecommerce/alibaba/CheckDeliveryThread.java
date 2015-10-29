package com.wofu.ecommerce.alibaba;

import java.util.Date;

import com.wofu.common.tools.util.log.Log;

/**
 * 检测线程是否中止
 * @author windows7
 *
 */
public class CheckDeliveryThread extends Thread {
	private static Date getOrderDeliveryDate=null;
	private static final long CheckTime = 15*60*1000L;
	@Override
	public void run() {
		try{
			for(;;){
				Log.info("检测发货线程开始!");
				Date date = Alibaba.getCurrentDate_Order_delivery();
				if(date==null){
					date = new Date();
					Alibaba.setCurrentDate_Order_delivery(date);
				}else{
					if(getOrderDeliveryDate==null){
						getOrderDeliveryDate=date;
					}else{
						if(date.compareTo(getOrderDeliveryDate)<=0){
							Log.error("检测线程","十五分钟没有检测到发货的线程活动，准备重启线程");
							OrderDelivery orderDelivery = new OrderDelivery();
							orderDelivery.start();
						}else{
							getOrderDeliveryDate=date;
						}
						
					}
				}
				Log.info("检测发货线程结束!");
				long current = System.currentTimeMillis();
				while(current+CheckTime>System.currentTimeMillis()){
					try{
						Thread.sleep(100L);
					}catch(Exception e){
						Log.error("检测线程", e.getMessage());
					}
				}
			}
			
		}catch(Exception ex){
			Log.error("检查发货线程出错",ex.getMessage());
		}
	}

}
