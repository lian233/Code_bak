package com.wofu.netshop.alibaba.fenxiao;

import java.util.concurrent.CountDownLatch;

public class GenCustomerRetRunnable implements Runnable{
	private String jobName="����Ͱͽӿ��˻��������ɿͻ��˻�������ҵ";
	private CountDownLatch watch;
	private String username="";
	public GenCustomerRetRunnable(CountDownLatch watch,String username){
		this.watch=watch;
		this.username=username;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
