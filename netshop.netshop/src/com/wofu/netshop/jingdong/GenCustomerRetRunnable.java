package com.wofu.netshop.jingdong;

import java.util.concurrent.CountDownLatch;

public class GenCustomerRetRunnable implements Runnable{
	private String jobName="�����ӿ��˻��������ɿͻ��˻�������ҵ";
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