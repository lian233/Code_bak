package com.wofu.fire.deliveryservice;
import com.wofu.common.service.Service;
import java.util.Properties;
public class FireDelivery extends Service
{

	public FireDelivery()
	{
	}

	public String description()
	{
		return (new StringBuilder()).append("跨境订单传送接口处理系统 [V ").append(Version.version).append("]").toString();
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
		//发送海关条码跟快递单号信息
		SendHSCodeAndOutsid sendHSCodeAndOutsid = new SendHSCodeAndOutsid();
		sendHSCodeAndOutsid.start();
	}
}
