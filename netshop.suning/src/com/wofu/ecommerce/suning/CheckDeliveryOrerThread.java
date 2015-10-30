package com.wofu.ecommerce.suning;
import java.util.Date;
import com.wofu.common.tools.util.log.Log;

/**
 * ��ⷢ���߳��Ƿ���ֹ
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
				Log.info("��ⷢ���߳̿�ʼ!");
				Date date = SuNing.getCurrentDate_DevOrder();
				if(date==null){
					date = new Date();
					SuNing.setCurrentDate_DevOrder(date);
				}else{
					if(deliveryOrderDate==null){
						deliveryOrderDate=date;
					}else{
						if(date.compareTo(deliveryOrderDate)<=0){
							Log.error("����߳�","ʮ�����û�м�⵽�����̻߳��׼�������߳�");
							OrderDelivery orderDelivery = new OrderDelivery();
							orderDelivery.start();
						}else{
							deliveryOrderDate=date;
						}
						
					}
				}
				Log.info("��ⷢ���߳̽���!");
				long current = System.currentTimeMillis();
				while(current+CheckTime>System.currentTimeMillis()){
					try{
						Thread.sleep(100L);
					}catch(Exception e){
						Log.error("����߳�", e.getMessage());
					}
				}
			}
			
		}catch(Exception ex){
			Log.error("��鷢���̳߳���",ex.getMessage());
		}
	}

}
