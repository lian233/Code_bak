package com.wofu.netshop.mogujie.fenxiao;

import java.util.concurrent.CountDownLatch;

public class GenCustomerRetRunnable implements Runnable{
	private String jobName="Ģ���ֽӿ��˻��������ɿͻ��˻�������ҵ";
	private CountDownLatch watch;
	private String username="";
	private Params param;
	public GenCustomerRetRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
	}
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
