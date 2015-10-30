package com.wofu.ecommerce.uwuku;

import java.util.Properties;


import com.wofu.common.service.Service;

public class Uwuku extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "优物库处理系统 [V " + Version.version + "]";
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
		
		getOrders getorders=new getOrders();
		getorders.start();
		
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.start();
	}

}
