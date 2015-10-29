package com.wofu.intf.jw;
import com.wofu.common.service.Service;
import java.util.Properties;

public class JW extends Service
{

	public JW()
	{
	}

	public String description()
	{
		return (new StringBuilder()).append("巨沃物流接口处理系统 [V ").append(Version.version).append("]").toString();
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
	{	//商品
		
		AsynProductInfo asynproductinfo = new AsynProductInfo();
		asynproductinfo.start();
		
		//订单
		AsynOrderInfo asynOrderInfo = new AsynOrderInfo();
		asynOrderInfo.start();
		//取消订单
		AsynCancelOrderInfo asynCancelOrderInfo = new AsynCancelOrderInfo();
		asynCancelOrderInfo.start();
		
		
		//发送ems快递信息
		SendEmsInfo sendEmsInfo= new SendEmsInfo();
		sendEmsInfo.start();
		
		
		
		
		
	}
}
