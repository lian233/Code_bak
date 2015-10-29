package com.wofu.ecommerce.jingdong;
import java.util.Date;
import com.wofu.common.tools.util.log.Log;

/**
 * ���ȡ��Ʒ�߳��Ƿ���ֹ
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
				Log.info("���ȡ��Ʒ�߳̿�ʼ!");
				Date date = Jingdong.getCurrentDate_getItem();
				if(date==null){
					date = new Date();
					Jingdong.setCurrentDate_getItem(date);
				}else{
					if(getItemDate==null){
						getItemDate=date;
					}else{
						if(date.compareTo(getItemDate)<=0){
							Log.error("����߳�","ʮ�����û�м�⵽ȡ��Ʒ�̻߳��׼�������߳�");
							getItems getItem = new getItems();
							getItem.start();
						}else{
							getItemDate=date;
						}
						
					}
				}
				Log.info("���ȡ��Ʒ�߳̽���!");
				long current = System.currentTimeMillis();
				while(current+CheckTime>System.currentTimeMillis()){
					try{
						Thread.sleep(100L);
					}catch(Exception e){
						Log.error("���ȡ��Ʒ�߳�", e.getMessage());
					}
				}
			}
			
		}catch(Exception ex){
			Log.error("���ȡ��Ʒ�̳߳���",ex.getMessage());
		}
	}

}
