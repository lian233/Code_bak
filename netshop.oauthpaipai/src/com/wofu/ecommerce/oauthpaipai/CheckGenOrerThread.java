package com.wofu.ecommerce.oauthpaipai;

import java.util.Date;

import com.wofu.common.tools.util.log.Log;

/**
 * 检测生成临时线程是否中止
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
				Log.info("检测生成内部订单线程开始!");
				Date date = PaiPai.getCurrentDate_genOrder();
				if(date==null){
					date = new Date();
					PaiPai.setCurrentDate_genOrder(date);
				}else{
					if(genOrderDate==null){
						genOrderDate=date;
					}else{
						if(date.compareTo(genOrderDate)<=0){
							Log.error("检测线程","十五分钟没有检测到生成内部订单的线程活动，准备重启线程");
							GenCustomerOrder genCustomerOrder = new GenCustomerOrder();
							genCustomerOrder.start();
						}else{
							genOrderDate=date;
						}
						
					}
				}
				Log.info("检测生成内部订单线程结束!");
				long current = System.currentTimeMillis();
				while(current+CheckTime>System.currentTimeMillis()){
					try{
						sleep(1000L);
					}catch(Exception e){
						Log.error("检测线程", e.getMessage());
					}
				}
			}
			
		}catch(Exception ex){
			Log.error("检查生成内部订单线程出错",ex.getMessage());
		}
	}

}
