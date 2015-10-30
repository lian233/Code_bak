package com.wofu.ecommerce.ming_xie_ku;

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
		Properties properties = Resource.load("com.wofu.ecommerce.ming_xie_ku.Version");
		version = properties.getProperty("version.number") + "." + properties.getProperty("build.number");
	}
}