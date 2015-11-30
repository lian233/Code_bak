package com.wofu.intf.yjn;

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
		Properties properties = Resource.load("com.wofu.intf.yjn.YjnInterface");
		version = properties.getProperty("version.number") + "." + properties.getProperty("build.number");
	}
}