package com.wofu.ecommerce.vjia;

import java.util.Properties;

import com.wofu.common.service.Service;

public class Vjia extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "Vjia��������ϵͳ [V " + Version.version + "]";
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

	}

	@Override
	public void start() throws Exception {
		
		// TODO Auto-generated method stub
		//��ȡ�������¶�����--------------------
		GetOrders getorders = new GetOrders();
		getorders.start();
		
		//��ȡvjia��Ʒ
		//getItems getitems = new getItems() ;
		//getitems.start() ;
		
		//��ȡ�˻�����----------------
		GetReturnOrders getreturnorders = new GetReturnOrders() ;
		getreturnorders.start() ;
		
		//����ϵͳ�ڲ�����-----------------
			GenCustomerOrder gencustomerorder = new GenCustomerOrder();
			gencustomerorder.start();
	
		
		//���¶���״̬�����ȷ�ϣ��˻��յ�ȷ�ϣ�--------------
		UpdateStatus updatestatus=new UpdateStatus();
		updatestatus.start();
		
		//���¶���������Ϣ-----------------
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.start() ;
		
		//���¿��
		UpdateStock updatestock=new UpdateStock();
		updatestock.start();
	}
}

