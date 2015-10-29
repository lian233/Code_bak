package com.wofu.intf.fedex;
import com.wofu.common.service.Service;
import java.util.Properties;
public class Fedex extends Service
{

	public Fedex()
	{
	}

	public String description()
	{
		return (new StringBuilder()).append("联邦快递接口处理系统 [V ").append(Version.version).append("]").toString();
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
		//同步商品资料
		AsynProductInfo asynproductinfo = new AsynProductInfo();
		asynproductinfo.start();
		//同步订单
		AsynOrderInfo asyncOrderInfo = new AsynOrderInfo();
		asyncOrderInfo.start();
		//取消订单
		AsyncCancelOrderInfo asynccancelorderinfo = new AsyncCancelOrderInfo();
		asynccancelorderinfo.start();
		
	}
}
