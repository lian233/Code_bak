package com.wofu.ecommerce.groupon;


import java.util.Properties;

import com.wofu.ecommerce.groupon.Params;
import com.wofu.common.service.Service;

public class GroupOn extends Service{


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("test1");
		GroupOn groupon = new GroupOn();
		try {
			System.out.println("����");
			//groupon.init(null);
			groupon.start();
		} catch (Exception e) {
			System.out.println("����");
			System.exit(-1);
		}
		while (true) {
			try {
				Thread.sleep(30000L);
			} catch (Exception e) {
			}
			System.gc();
		}
	}
	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "�ű���������ϵͳ [V " + Version.version + "]";
	}

	@Override
	public void end() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(Properties prop) throws Exception {
		// TODO Auto-generated method stub
		Params.init(prop);
		this.setParams(prop);
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub
		// System.out.println("��ʼ�ű�����������ҵ");
	}

	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub		
		getOrders getorders = new getOrders();
		getorders.start();
		
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();

		//ProcessOrderStatus processorderstatus = new ProcessOrderStatus();
		//processorderstatus.start();

		OrderDelivery orderdelivery = new OrderDelivery();
		orderdelivery.start();
		
		UpdateStock updatestock=new UpdateStock();
		updatestock.start();
		
	}

}
