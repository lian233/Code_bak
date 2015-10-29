package com.wofu.ecommerce.threeg;

import java.util.Properties;

import com.wofu.ecommerce.threeg.Params;
import com.wofu.ecommerce.threeg.Version;
import com.wofu.ecommerce.threeg.getOrders;
import com.wofu.common.service.Service;

public class ThreeG extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "3G订单处理系统 [V " + Version.version + "]";
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
		
		getOrders getorders = new getOrders();
		getorders.start();

		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		
		UpdateStatus updatestatus=new UpdateStatus();
		updatestatus.start();
		
		//ProcessOrderStatus processorderstatus=new ProcessOrderStatus();
		//processorderstatus.start();
		
		UpdateStock updatestock=new UpdateStock();
		updatestock.start();
	}
}
