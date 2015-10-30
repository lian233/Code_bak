package com.wofu.ecommerce.suning;
import java.util.Date;
import com.wofu.common.tools.util.log.Log;

/**
 * ����ȡ�˻������߳��Ƿ���ֹ
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
				Log.info("����ȡ�˻������߳̿�ʼ!");
				Date date = SuNing.getCurrentDate_getRefundOrder();
				if(date==null){
					date = new Date();
					SuNing.setCurrentDate_getRefundOrder(date);
				}else{
					if(refundOrderDate==null){
						refundOrderDate=date;
					}else{
						if(date.compareTo(refundOrderDate)<=0){
							Log.error("����߳�","ʮ�����û�м�⵽��ȡ�˻������̻߳��׼�������߳�");
							GetRefundOrders getRefundOrders = new GetRefundOrders();
							getRefundOrders.start();
						}else{
							refundOrderDate=date;
						}
						
					}
				}
				Log.info("����˻������߳̽���!");
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
			Log.error("����˻������̳߳���",ex.getMessage());
		}
	}

}
