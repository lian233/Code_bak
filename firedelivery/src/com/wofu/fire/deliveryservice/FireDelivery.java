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
		return (new StringBuilder()).append("�羳�������ͽӿڴ���ϵͳ [V ").append(Version.version).append("]").toString();
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
		//���ͺ����������ݵ�����Ϣ
		SendHSCodeAndOutsid sendHSCodeAndOutsid = new SendHSCodeAndOutsid();
		sendHSCodeAndOutsid.start();
	}
}
