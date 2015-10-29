
package com.wofu.intf.dtc;

import com.wofu.common.service.Service;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

import java.sql.Connection;
import java.util.Properties;

public class DTC extends Service
{

	public DTC()
	{
	}

	public String description()
	{
		return (new StringBuilder()).append("重庆跨境电商系统 [V ").append(Version.version).append("]").toString();
	}

	public void end()
		throws Exception
	{
	}

	public void init(Properties properties)
		throws Exception
	{
		Params.init(properties);
		setParams(properties);
	}

	public void process()
	{
	}

	public void start()
		throws Exception
	{
		//商品备案
		AsynProductInfo asynproductinfo = new AsynProductInfo();
		asynproductinfo.start();
		//处理订单作业
		ProcessOrderInfo processOrderInfo = new ProcessOrderInfo();
		processOrderInfo.start();
		//处理支付单作业
		ProcPaymentInfo procPaymentInfo = new ProcPaymentInfo();
		procPaymentInfo.start();
		//处理订单对应分运单号作业
		ProcOrderSetTransportNo procOrderSetTransportNo = new ProcOrderSetTransportNo();
		procOrderSetTransportNo.start();
		//处理订单退货作业
		ProcOrderReturnInfo procOrderReturnInfo = new ProcOrderReturnInfo();
		procOrderReturnInfo.start();
		
//		InternationalPayments internationalpayments = new InternationalPayments(); 
//		internationalpayments.start();
		
//		NewAsynProductInfo asynproductinfo = new NewAsynProductInfo();
//		asynproductinfo.start();
//		
		
	}
}
