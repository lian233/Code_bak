package com.wofu.netshop.mogujie.fenxiao;

import java.util.concurrent.CountDownLatch;

public class GenCustomerRetRunnable implements Runnable{
	private String jobName="蘑菇街接口退货订单生成客户退货订单作业";
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
