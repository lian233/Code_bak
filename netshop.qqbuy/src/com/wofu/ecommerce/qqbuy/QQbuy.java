package com.wofu.ecommerce.qqbuy;

import java.util.Properties;

import com.wofu.ecommerce.qqbuy.Params;
import com.wofu.common.service.Service;

public class QQbuy extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "QQbuy订单处理系统 [V " + Version.version + "]";
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
		//获取订单（新订单，取消的订单，退货单）
		GetOrders getorders = new GetOrders();
		getorders.start();
		
		//生成系统订单
		GenCustomerOrder genCustomerOrder = new GenCustomerOrder() ;
		genCustomerOrder.start() ;
		
		//更新订单审核状态
		UpdateStatus updateStatus = new UpdateStatus() ;
		updateStatus.start() ;
		
		//同步库存
		UpdateStock updateStock = new UpdateStock() ;
		updateStock.start() ;
		
		//发货处理
		OrderDelivery orderDelivery = new OrderDelivery() ;
		orderDelivery.start() ;
		
		//获取QQ网购商品
		GetGoods getGoods = new GetGoods() ;
		getGoods.start() ;
	}
}

