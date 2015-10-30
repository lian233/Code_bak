package com.wofu.ecommerce.taobao;
import java.util.Properties;
import com.wofu.ecommerce.taobao.GenCustomerOrder;
import com.wofu.ecommerce.taobao.Params;
import com.wofu.ecommerce.taobao.Version;
import com.wofu.common.service.Service;

public class TaoBao_remote_yyk extends Service {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "�Ա���������ϵͳ [V " + Version.version + "]";
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
		
		
		//---------------yyk��  û���շ�api
		//UpdateStock updatestock=new UpdateStock();
		//updatestock.start();
		
		//-------------------
		
		if (!Params.isrds && !Params.isdistribution)
		{							
			getOrders getorders = new getOrders();
			getorders.start();
		}
		
		//------------yyk_local
		//GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		//gencustomerorder.start();
		//----------------------yyk��
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.start();
		
		/*
		  ��ǰע�ͱ���������
		if (!Params.isc){
			ReturnBatchDeal returnbatchdeal=new ReturnBatchDeal();
			returnbatchdeal.start();
			
			genReturnBill genreturnbill=new genReturnBill();
			genreturnbill.start();
			
			genRefundBill genrefundbill=new genRefundBill();
			genrefundbill.start();
		}
	
		*/
		
		//-------------------yyk��
		
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

		
		//------------yyk_local
		//genCustomerRet gencustomerret=new genCustomerRet();
		//gencustomerret.start();
		
	}

}
