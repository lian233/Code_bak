package com.wofu.ecommerce.maisika;
import java.util.Properties;

import com.wofu.ecommerce.maisika.Params;
import com.wofu.ecommerce.maisika.Version;
import com.wofu.common.service.Service;
public class Maisika extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "��˹����������ϵͳ [V " + Version.version + "]";
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
		System.out.println("������˹������");
		// TODO Auto-generated method stub
		//----��ȡ�������¶�����
		GetOrders getorders = new GetOrders();
		getorders.start();
		
		//----��ȡ��˹���ϼ���Ʒ
//		GetItems getitems = new GetItems() ;
//		getitems.start() ;
//		
		//---��ȡ�˻�����
		GetRefund getRefundOrders = new GetRefund() ;
		getRefundOrders.start() ;
		
		//----����ϵͳ�ڲ�����
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		
		//���¶���״̬�����ȷ�ϣ��˻��յ�ȷ�ϣ�
		//UpdateStatus updatestatus=new UpdateStatus();
		//updatestatus.start();
		
		//---���¶���������Ϣ
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.start() ;
//		
//		//--���¿��  yykͣ
//		UpdateStock updatestock=new UpdateStock();
//		updatestock.start();
	}
}

