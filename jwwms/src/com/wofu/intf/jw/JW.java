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
		return (new StringBuilder()).append("���������ӿڴ���ϵͳ [V ").append(Version.version).append("]").toString();
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
	{	//��Ʒ
		
		AsynProductInfo asynproductinfo = new AsynProductInfo();
		asynproductinfo.start();
		
		//����
		AsynOrderInfo asynOrderInfo = new AsynOrderInfo();
		asynOrderInfo.start();
		//ȡ������
		AsynCancelOrderInfo asynCancelOrderInfo = new AsynCancelOrderInfo();
		asynCancelOrderInfo.start();
		
		
		//����ems�����Ϣ
		SendEmsInfo sendEmsInfo= new SendEmsInfo();
		sendEmsInfo.start();
		
		
		
		
		
	}
}
