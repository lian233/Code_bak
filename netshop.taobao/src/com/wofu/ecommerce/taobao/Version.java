package com.wofu.ecommerce.taobao;

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
		Properties properties = Resource.load("com.wofu.ecommerce.taobao.Version");
		version = properties.getProperty("version.number") + "." + properties.getProperty("build.number");
	}
}