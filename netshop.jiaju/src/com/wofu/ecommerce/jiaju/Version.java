package com.wofu.ecommerce.jiaju;

import java.util.Properties;
import com.wofu.common.tools.config.Resource;

//家居就订单处理系统版本号获取
public class Version
{

	public static String version;

	public Version()
	{
	}

	static 
	{//获取版本号信息
		Properties properties = Resource.load("com.wofu.ecommerce.suning.Version");
		version = properties.getProperty("version.number") + "." + properties.getProperty("build.number");
	}
}