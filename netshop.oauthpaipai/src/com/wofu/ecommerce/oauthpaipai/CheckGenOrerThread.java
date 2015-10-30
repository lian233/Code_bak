package com.wofu.ecommerce.oauthpaipai;

import java.util.Date;

import com.wofu.common.tools.util.log.Log;

/**
 * ���������ʱ�߳��Ƿ���ֹ
 * @author windows7
 *
 */
public class CheckGenOrerThread extends Thread {
	private static Date genOrderDate=null;
	private static final long CheckTime = 15*60*1000L;
	@Override
	public void run() {
		try{
			for(;;){
				Log.info("��������ڲ������߳̿�ʼ!");
				Date date = PaiPai.getCurrentDate_genOrder();
				if(date==null){
					date = new Date();
					PaiPai.setCurrentDate_genOrder(date);
				}else{
					if(genOrderDate==null){
						genOrderDate=date;
					}else{
						if(date.compareTo(genOrderDate)<=0){
							Log.error("����߳�","ʮ�����û�м�⵽�����ڲ��������̻߳��׼�������߳�");
							GenCustomerOrder genCustomerOrder = new GenCustomerOrder();
							genCustomerOrder.start();
						}else{
							genOrderDate=date;
						}
						
					}
				}
				Log.info("��������ڲ������߳̽���!");
				long current = System.currentTimeMillis();
				while(current+CheckTime>System.currentTimeMillis()){
					try{
						sleep(1000L);
					}catch(Exception e){
						Log.error("����߳�", e.getMessage());
					}
				}
			}
			
		}catch(Exception ex){
			Log.error("��������ڲ������̳߳���",ex.getMessage());
		}
	}

}
