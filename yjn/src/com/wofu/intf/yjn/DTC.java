
package com.wofu.intf.yjn;

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
		return (new StringBuilder()).append("����羳����ϵͳ [V ").append(Version.version).append("]").toString();
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
		//��Ʒ����
		AsynProductInfo asynproductinfo = new AsynProductInfo();
		asynproductinfo.start();
		//��������ҵ
		ProcessOrderInfo processOrderInfo = new ProcessOrderInfo();
		processOrderInfo.start();
		//����֧������ҵ
		ProcPaymentInfo procPaymentInfo = new ProcPaymentInfo();
		procPaymentInfo.start();
		//��������Ӧ���˵�����ҵ
		ProcOrderSetTransportNo procOrderSetTransportNo = new ProcOrderSetTransportNo();
		procOrderSetTransportNo.start();
		//�������˻���ҵ
		ProcOrderReturnInfo procOrderReturnInfo = new ProcOrderReturnInfo();
		procOrderReturnInfo.start();
		
//		InternationalPayments internationalpayments = new InternationalPayments(); 
//		internationalpayments.start();
		
//		NewAsynProductInfo asynproductinfo = new NewAsynProductInfo();
//		asynproductinfo.start();
//		
		
	}
}
