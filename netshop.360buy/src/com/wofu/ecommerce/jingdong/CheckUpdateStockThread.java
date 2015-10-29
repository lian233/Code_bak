package com.wofu.ecommerce.jingdong;
import java.util.Date;
import com.wofu.common.tools.util.log.Log;

/**
 * �����¿���߳��Ƿ���ֹ
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
				Log.info("�����¿���߳̿�ʼ!");
				Date date = Jingdong.getCurrentDate_updatStock();
				if(date==null){
					date = new Date();
					Jingdong.setCurrentDate_updatStock(date);
				}else{
					if(updateStockDate==null){
						updateStockDate=date;
					}else{
						if(date.compareTo(updateStockDate)<=0){
							Log.error("����߳�","ʮ�����û�м�⵽���¿���̻߳��׼�������߳�");
							UpdateStatus updateStatus = new UpdateStatus();
							updateStatus.start();
						}else{
							updateStockDate=date;
						}
						
					}
				}
				Log.info("�����¿���߳̽���!");
				long current = System.currentTimeMillis();
				while(current+CheckTime>System.currentTimeMillis()){
					try{
						Thread.sleep(100L);
					}catch(Exception e){
						Log.error("�����¿���߳�", e.getMessage());
					}
				}
			}
			
		}catch(Exception ex){
			Log.error("�����¿���̳߳���",ex.getMessage());
		}
	}

}
