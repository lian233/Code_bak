package com.wofu.ecommerce.jingdong;
import java.util.Date;
import com.wofu.common.tools.util.log.Log;

/**
 * 检测取商品线程是否中止
 * @author windows7
 *
 */
public class CheckGetItemThread extends Thread {
	private static Date getItemDate=null;
	private static final long CheckTime = 15*60*1000L;
	@Override
	public void run() {
		try{
			for(;;){
				Log.info("检测取商品线程开始!");
				Date date = Jingdong.getCurrentDate_getItem();
				if(date==null){
					date = new Date();
					Jingdong.setCurrentDate_getItem(date);
				}else{
					if(getItemDate==null){
						getItemDate=date;
					}else{
						if(date.compareTo(getItemDate)<=0){
							Log.error("检测线程","十五分钟没有检测到取商品线程活动，准备重启线程");
							getItems getItem = new getItems();
							getItem.start();
						}else{
							getItemDate=date;
						}
						
					}
				}
				Log.info("检测取商品线程结束!");
				long current = System.currentTimeMillis();
				while(current+CheckTime>System.currentTimeMillis()){
					try{
						Thread.sleep(100L);
					}catch(Exception e){
						Log.error("检测取商品线程", e.getMessage());
					}
				}
			}
			
		}catch(Exception ex){
			Log.error("检查取商品线程出错",ex.getMessage());
		}
	}

}
