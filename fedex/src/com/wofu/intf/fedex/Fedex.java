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
		return (new StringBuilder()).append("�����ݽӿڴ���ϵͳ [V ").append(Version.version).append("]").toString();
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
		//ͬ����Ʒ����
		AsynProductInfo asynproductinfo = new AsynProductInfo();
		asynproductinfo.start();
		//ͬ������
		AsynOrderInfo asyncOrderInfo = new AsynOrderInfo();
		asyncOrderInfo.start();
		//ȡ������
		AsyncCancelOrderInfo asynccancelorderinfo = new AsyncCancelOrderInfo();
		asynccancelorderinfo.start();
		
	}
}
