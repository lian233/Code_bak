package com.wofu.ecommerce.lefeng;

import java.sql.Connection;
import java.util.Properties;


import com.wofu.common.service.Service;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;

public class LeFeng extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "乐峰订单处理系统 [V " + Version.version + "]";
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
		

		UpdateStock updatestock=new UpdateStock();
		updatestock.start();
		
							
		getOrders getorders = new getOrders();
		getorders.start();
		
		
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.start();
		

	
	}

}
