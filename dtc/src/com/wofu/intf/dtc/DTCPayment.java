
package com.wofu.intf.dtc;

import com.wofu.common.service.Service;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

import java.sql.Connection;
import java.util.Properties;

public class DTCPayment extends Service
{

	public DTCPayment()
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
		
		InternationalPayments internationalpayments = new InternationalPayments(); 
		internationalpayments.start();
		
		
	}
}
