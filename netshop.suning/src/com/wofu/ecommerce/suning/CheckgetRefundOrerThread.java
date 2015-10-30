package com.wofu.ecommerce.suning;
import java.util.Date;
import com.wofu.common.tools.util.log.Log;

/**
 * 检测获取退货订单线程是否中止
 * @author windows7
 *
 */
public class CheckgetRefundOrerThread extends Thread {
	private static Date refundOrderDate=null;
	private static final long CheckTime = 15*60*1000L;
	@Override
	public void run() {
		try{
			for(;;){
				Log.info("检测获取退货订单线程开始!");
				Date date = SuNing.getCurrentDate_getRefundOrder();
				if(date==null){
					date = new Date();
					SuNing.setCurrentDate_getRefundOrder(date);
				}else{
					if(refundOrderDate==null){
						refundOrderDate=date;
					}else{
						if(date.compareTo(refundOrderDate)<=0){
							Log.error("检测线程","十五分钟没有检测到获取退货订单线程活动，准备重启线程");
							GetRefundOrders getRefundOrders = new GetRefundOrders();
							getRefundOrders.start();
						}else{
							refundOrderDate=date;
						}
						
					}
				}
				Log.info("检测退货订单线程结束!");
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
			Log.error("检查退货订单线程出错",ex.getMessage());
		}
	}

}
