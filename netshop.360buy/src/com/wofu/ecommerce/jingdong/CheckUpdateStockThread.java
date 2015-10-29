package com.wofu.ecommerce.jingdong;
import java.util.Date;
import com.wofu.common.tools.util.log.Log;

/**
 * 检测更新库存线程是否中止
 * @author windows7
 *
 */
public class CheckUpdateStockThread extends Thread {
	private static Date updateStockDate=null;
	private static final long CheckTime = 15*60*1000L;
	@Override
	public void run() {
		try{
			for(;;){
				Log.info("检测更新库存线程开始!");
				Date date = Jingdong.getCurrentDate_updatStock();
				if(date==null){
					date = new Date();
					Jingdong.setCurrentDate_updatStock(date);
				}else{
					if(updateStockDate==null){
						updateStockDate=date;
					}else{
						if(date.compareTo(updateStockDate)<=0){
							Log.error("检测线程","十五分钟没有检测到更新库存线程活动，准备重启线程");
							UpdateStatus updateStatus = new UpdateStatus();
							updateStatus.start();
						}else{
							updateStockDate=date;
						}
						
					}
				}
				Log.info("检测更新库存线程结束!");
				long current = System.currentTimeMillis();
				while(current+CheckTime>System.currentTimeMillis()){
					try{
						Thread.sleep(100L);
					}catch(Exception e){
						Log.error("检测更新库存线程", e.getMessage());
					}
				}
			}
			
		}catch(Exception ex){
			Log.error("检查更新库存线程出错",ex.getMessage());
		}
	}

}
