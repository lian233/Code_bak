package com.wofu.intf.tiantu;

import java.util.Properties;

import com.wofu.common.tools.config.Resource;
import com.wofu.common.tools.util.log.Log;

public class Processors {
	
	private static String DEFINE_FILE = Processors.class.getName();
	
	public static String getProcessor(String key) {
		Properties prop = Resource.load(DEFINE_FILE);
		return prop.getProperty(key.trim(), key);
	}
	

}
