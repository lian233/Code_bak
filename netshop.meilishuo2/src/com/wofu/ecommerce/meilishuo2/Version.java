package com.wofu.ecommerce.meilishuo2;

import java.util.Properties;
import com.wofu.common.tools.config.Resource;

public class Version
{

	public static String version;

	public Version()
	{
	}

	static 
	{
		Properties properties = Resource.load("com.wofu.ecommerce.suning.Version");
		version = properties.getProperty("version.number") + "." + properties.getProperty("build.number");
	}
}