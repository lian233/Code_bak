package com.wofu.ecommerce.jit;

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
		Properties properties = Resource.load("com.wofu.ecommerce.jit.Version");
		version = properties.getProperty("version.number") + "." + properties.getProperty("build.number");
	}
}