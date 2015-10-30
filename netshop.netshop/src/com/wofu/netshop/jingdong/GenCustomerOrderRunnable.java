package com.wofu.netshop.jingdong;

import java.util.concurrent.CountDownLatch;

public class GenCustomerOrderRunnable implements Runnable{
	private String jobName="接口订单生成客户订单作业";
	private CountDownLatch watch;
	private String username="";
	public GenCustomerOrderRunnable(CountDownLatch watch,String username){
		this.watch=watch;
		this.username=username;
	}
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
