package com.wofu.ecommerce.huasheng;

import java.util.Date;

import com.wofu.common.tools.util.log.Log;

/**
 * ����߳��Ƿ���ֹ
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
				Log.info("��ⷢ���߳̿�ʼ!");
				Date date = huasheng.getCurrentDate_orderDelivery();
				if(date==null){
					date = new Date();
					huasheng.setCurrentDate_orderDelivery(date);
				}else{
					if(orderDeliveryDate==null){
						orderDeliveryDate=date;
					}else{
						if(date.compareTo(orderDeliveryDate)<=0){
							Log.error("����߳�","ʮ�����û�м�⵽�������̻߳��׼�������߳�");
							OrderDelivery orderDelivery = new OrderDelivery();
							orderDelivery.start();
						}else{
							orderDeliveryDate=date;
						}
						
					}
				}
				Log.info("��ⷢ���߳̽���!");
				long current = System.currentTimeMillis();
				while(current+CheckTime>System.currentTimeMillis()){
					try{
						Thread.sleep(1000L);
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
