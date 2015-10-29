package com.wofu.ecommerce.alibaba;

import java.util.Date;

import com.wofu.common.tools.util.log.Log;

/**
 * ����߳��Ƿ���ֹ
 * @author windows7
 *
 */
public class CheckDeliveryThread extends Thread {
	private static Date getOrderDeliveryDate=null;
	private static final long CheckTime = 15*60*1000L;
	@Override
	public void run() {
		try{
			for(;;){
				Log.info("��ⷢ���߳̿�ʼ!");
				Date date = Alibaba.getCurrentDate_Order_delivery();
				if(date==null){
					date = new Date();
					Alibaba.setCurrentDate_Order_delivery(date);
				}else{
					if(getOrderDeliveryDate==null){
						getOrderDeliveryDate=date;
					}else{
						if(date.compareTo(getOrderDeliveryDate)<=0){
							Log.error("����߳�","ʮ�����û�м�⵽�������̻߳��׼�������߳�");
							OrderDelivery orderDelivery = new OrderDelivery();
							orderDelivery.start();
						}else{
							getOrderDeliveryDate=date;
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
