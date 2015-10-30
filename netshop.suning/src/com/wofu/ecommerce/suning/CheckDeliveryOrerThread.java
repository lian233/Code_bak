package com.wofu.ecommerce.suning;
import java.util.Date;
import com.wofu.common.tools.util.log.Log;

/**
 * 检测发货线程是否中止
 * @author windows7
 *
 */
public class CheckDeliveryOrerThread extends Thread {
	private static Date deliveryOrderDate=null;
	private static final long CheckTime = 15*60*1000L;
	@Override
	public void run() {
		try{
			for(;;){
				Log.info("检测发货线程开始!");
				Date date = SuNing.getCurrentDate_DevOrder();
				if(date==null){
					date = new Date();
					SuNing.setCurrentDate_DevOrder(date);
				}else{
					if(deliveryOrderDate==null){
						deliveryOrderDate=date;
					}else{
						if(date.compareTo(deliveryOrderDate)<=0){
							Log.error("检测线程","十五分钟没有检测到发货线程活动，准备重启线程");
							OrderDelivery orderDelivery = new OrderDelivery();
							orderDelivery.start();
						}else{
							deliveryOrderDate=date;
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
