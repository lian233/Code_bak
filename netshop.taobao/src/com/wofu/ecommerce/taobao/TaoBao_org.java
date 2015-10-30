package com.wofu.ecommerce.taobao;
import java.util.Properties;
import com.wofu.ecommerce.taobao.GenCustomerOrder;
import com.wofu.ecommerce.taobao.Params;
import com.wofu.ecommerce.taobao.Version;
import com.wofu.common.service.Service;

public class TaoBao_org extends Service {

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
		
		
		//---------------yyk用  没有收费api
		UpdateStock updatestock=new UpdateStock();
		updatestock.start();
		
		//-------------------
		
		if (!Params.isrds && !Params.isdistribution)
		{							
			getOrders getorders = new getOrders();
			getorders.start();
		}
		
		//------------
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		//----------------------yyk用
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.start();
		
		/*
		  以前注释保留下来的
		if (!Params.isc){
			ReturnBatchDeal returnbatchdeal=new ReturnBatchDeal();
			returnbatchdeal.start();
			
			genReturnBill genreturnbill=new genReturnBill();
			genreturnbill.start();
			
			genRefundBill genrefundbill=new genRefundBill();
			genrefundbill.start();
		}
	
		*/
		
		//-------------------yyk用
		/*
		if (Params.isdistribution)
		{		
			//---------------
			getDistributor getdistributor=new getDistributor();
			getdistributor.start();
			
			getDealerOrders getdealerorders=new getDealerOrders();
			getdealerorders.start();
			//------------4
			getDistributionProduct getdistributionproduct=new getDistributionProduct();
			getdistributionproduct.start();
			
			if (!Params.isrds)
			{
				getDistributionOrders getdistributionorders=new getDistributionOrders();
				getdistributionorders.start();
			}
			
		}
		else
		{
			
			getItems getitems=new getItems();
			getitems.start();
		} //yyk注释
*/		
		//------------
		genCustomerRet gencustomerret=new genCustomerRet();
		gencustomerret.start();
		
	}

}
