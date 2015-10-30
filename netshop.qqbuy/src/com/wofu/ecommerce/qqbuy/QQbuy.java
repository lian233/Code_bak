package com.wofu.ecommerce.qqbuy;

import java.util.Properties;

import com.wofu.ecommerce.qqbuy.Params;
import com.wofu.common.service.Service;

public class QQbuy extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "QQbuy��������ϵͳ [V " + Version.version + "]";
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
		//��ȡ�������¶�����ȡ���Ķ������˻�����
		GetOrders getorders = new GetOrders();
		getorders.start();
		
		//����ϵͳ����
		GenCustomerOrder genCustomerOrder = new GenCustomerOrder() ;
		genCustomerOrder.start() ;
		
		//���¶������״̬
		UpdateStatus updateStatus = new UpdateStatus() ;
		updateStatus.start() ;
		
		//ͬ�����
		UpdateStock updateStock = new UpdateStock() ;
		updateStock.start() ;
		
		//��������
		OrderDelivery orderDelivery = new OrderDelivery() ;
		orderDelivery.start() ;
		
		//��ȡQQ������Ʒ
		GetGoods getGoods = new GetGoods() ;
		getGoods.start() ;
	}
}

