package com.wofu.netshop.jingdong;

import java.util.concurrent.CountDownLatch;

public class GenCustomerOrderRunnable implements Runnable{
	private String jobName="�ӿڶ������ɿͻ�������ҵ";
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
