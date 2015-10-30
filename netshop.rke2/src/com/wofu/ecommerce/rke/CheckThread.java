package com.wofu.ecommerce.rke;
import java.util.Date;

import com.wofu.common.tools.util.log.Log;

/**
 * ����ȡ�����߳��Ƿ���ֹ
 * @author windows7
 *
 */
public class CheckThread extends Thread {
	private static Date getOrderDate=null;
	private static final long CheckTime = 15*60*1000L;
	@Override
	public void run() {
		try{
			for(;;){
				Log.info("����ȡ�����߳̿�ʼ!");
				Date date = RKE.getCurrentDate_getOrder();
				if(date==null){
					date = new Date();
					RKE.setCurrentDate_getOrder(date);
				}else{
					if(getOrderDate==null){
						getOrderDate=date;
					}else{
						if(date.compareTo(getOrderDate)<=0){
							Log.error("����߳�","ʮ�����û�м�⵽��ȡ�������̻߳��׼�������߳�");
							GetOrders getOrder = new GetOrders();
							getOrder.start();
						}else{
							getOrderDate=date;
						}
						
					}
				}
				Log.info("����ȡ�����߳̽���!");
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
			Log.error("����ȡ�����̳߳���",ex.getMessage());
		}
	}

}