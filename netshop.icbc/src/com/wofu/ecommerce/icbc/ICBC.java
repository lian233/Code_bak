package com.wofu.ecommerce.icbc;
import java.util.Properties;

import com.wofu.ecommerce.icbc.Params;
import com.wofu.common.service.Service;
public class ICBC extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "工行商城订单处理系统 [V " + Version.version + "]";
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
		//----获取订单（新订单）
		GetOrders getorders = new GetOrders();
		getorders.start();
		
		//----获取工行商城上架商品
		//getItems getitems = new getItems() ;
		//getitems.start() ;
		
		//---获取工行商城退换货单
		//GetRefundOrders getRefundOrders = new GetRefundOrders() ;
		//getRefundOrders.start() ;
		
		//----生成系统内部订单
		//GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		//gencustomerorder.start();
		
		//更新订单状态（审核确认，退货收到确认）
		//UpdateStatus updatestatus=new UpdateStatus();
		//updatestatus.start();
		
		//---更新订单发货信息
		//OrderDelivery orderdelivery = new OrderDelivery() ;
		//orderdelivery.start() ;
		
		//--更新库存  yyk停
		//UpdateStock updatestock=new UpdateStock();
		//updatestock.start();
	}
}

