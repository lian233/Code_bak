package com.wofu.ecommerce.taobao;
import java.util.Properties;
import com.wofu.ecommerce.taobao.GenCustomerOrder;
import com.wofu.ecommerce.taobao.Params;
import com.wofu.ecommerce.taobao.Version;
import com.wofu.common.service.Service;

public class TaoBao extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "淘宝订单处理系统 [V " + Version.version + "]";
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
		
		if(Params.isSockServer){
			GetPublicIp getPublicIp =new GetPublicIp();
			getPublicIp.start();
		}
		
		if(Params.isSockClient){
			SockClient sockClient = new SockClient();
			sockClient.start();
		}
		
		if(Params.isNeedUpdataLocal){
			UpdateLocalExtds updateLocalExtds = new UpdateLocalExtds();
			updateLocalExtds.start();
		}
		
		if(Params.isgenorder){
			GenCustomerOrder genCustomerOrder= new GenCustomerOrder();
			genCustomerOrder.start();
		}
		
		if(Params.isgenorderRet){
			genCustomerRet gencustomerret=new genCustomerRet();
			gencustomerret.start();
		}
		
		
		
		
		//------------
		
		
		if(!Params.isStopStockSyn){
			UpdateStock updatestock=new UpdateStock();
			updatestock.start();
		}
		
		if (!Params.isrds && !Params.isdistribution)
		{							
			getOrders getorders = new getOrders();
			getorders.start();
		}
		
		
		
		//---------------yyk用  没有收费api
		
		
		//----------------------yyk用
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.start();
		
		
		
		//GlobalGenCustomerOrder globalGenCustomerOrder = new GlobalGenCustomerOrder();
		//globalGenCustomerOrder.start();
		
		
		
		
		
		
		
		
	}

}
