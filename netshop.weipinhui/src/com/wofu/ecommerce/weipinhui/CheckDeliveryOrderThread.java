package com.wofu.ecommerce.weipinhui;

import java.util.Date;

import com.wofu.common.tools.util.log.Log;

/**
 * 检测线程是否中止
 * @author windows7
 *
 */
public class CheckDeliveryOrderThread extends Thread {
	private static Date orderDeliveryDate=null;
	private static final long CheckTime = 15*60*1000L;
	@Override
	public void run() {
		try{
			for(;;){
				Log.info("检测发货线程开始!");
				Date date = WeipinHui.getCurrentDate_orderDelivery();
				if(date==null){
					date = new Date();
					WeipinHui.setCurrentDate_orderDelivery(date);
				}else{
					if(orderDeliveryDate==null){
						orderDeliveryDate=date;
					}else{
						if(date.compareTo(orderDeliveryDate)<=0){
							Log.error("检测线程","十五分钟没有检测到发货的线程活动，准备重启线程");
							OrderDelivery orderDelivery = new OrderDelivery();
							orderDelivery.start();
						}else{
							orderDeliveryDate=date;
						}
						
					}
				}
				Log.info("检测发货线程结束!");
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
			Log.error("检查发货线程出错",ex.getMessage());
		}
	}

}
