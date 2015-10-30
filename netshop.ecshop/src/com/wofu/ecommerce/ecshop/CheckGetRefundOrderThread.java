package com.wofu.ecommerce.ecshop;

import java.util.Date;

import com.wofu.common.tools.util.log.Log;

/**
 * ����߳��Ƿ���ֹ
 * @author windows7
 *
 */
public class CheckGetRefundOrderThread extends Thread {
	private static Date getRefundOrderDate=null;
	private static final long CheckTime = 15*60*1000L;
	@Override
	public void run() {
		try{
			for(;;){
				Log.info("����ȡ�˻������߳̿�ʼ!");
				Date date = Ecshop.getCurrentDate_getRefundOrder();
				if(date==null){
					date = new Date();
					Ecshop.setCurrentDate_getRefundOrder(date);
				}else{
					if(getRefundOrderDate==null){
						getRefundOrderDate=date;
					}else{
						if(date.compareTo(getRefundOrderDate)<=0){
							Log.error("����߳�","ʮ�����û�м�⵽��ȡ�˻��������̻߳��׼�������߳�");
							GetRefundOrders getRefundOrders = new GetRefundOrders();
							getRefundOrders.start();
						}else{
							getRefundOrderDate=date;
						}
						
					}
				}
				Log.info("����ȡ�˻������߳̽���!");
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
			Log.error("����ȡ�˻������̳߳���",ex.getMessage());
		}
	}

}
