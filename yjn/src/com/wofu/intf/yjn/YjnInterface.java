
package com.wofu.intf.yjn;

import com.wofu.common.service.Service;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

import java.sql.Connection;
import java.util.Properties;

public class YjnInterface extends Service
{

	public YjnInterface()
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
		
		ProcessOrderInfo internationalpayments = new ProcessOrderInfo(); 
		internationalpayments.start();
		
		
	}
}
